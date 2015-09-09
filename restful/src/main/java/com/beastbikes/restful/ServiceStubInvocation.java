package com.beastbikes.restful;

import java.io.InputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.net.Uri;
import android.os.Build;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONTokener;

import com.beastbikes.logging.Logger;
import com.beastbikes.logging.LoggerFactory;
import com.beastbikes.restful.annotation.HttpDelete;
import com.beastbikes.restful.annotation.HttpPost;
import com.beastbikes.restful.annotation.HttpPut;
import com.beastbikes.restful.annotation.Path;
import com.beastbikes.restful.annotation.BodyParameter;
import com.beastbikes.restful.annotation.MatrixParameter;
import com.beastbikes.restful.annotation.PathParameter;
import com.beastbikes.restful.annotation.QueryParameter;

class ServiceStubInvocation implements Invocation {

    private static final Logger logger = LoggerFactory.getLogger("ServiceStubInvocation");

    final Context context;

    final Class<?> iface;

    final Method method;

    final String baseUrl;

    final AndroidHttpClient client;

    final Map<String, String> headers;

    ServiceStubInvocation(final Context context, final Class<?> iface, final Method method, final String baseUrl) {
        this(context, iface, method, baseUrl, Collections.<String, String>emptyMap());
    }

    ServiceStubInvocation(final Context context, final Class<?> iface, final Method method, final String baseUrl, final Map<String, String> headers) {
        this.context = context;
        this.iface = iface;
        this.method = method;
        this.baseUrl = baseUrl;
        this.client = AndroidHttpClient.newInstance(buildUserAgent(context), context);
        this.headers = null == headers ? Collections.<String, String>emptyMap() : headers;
    }

    @Override
    public Object invoke(final Object... args) throws InvocationException {
        logger.debug("Invoking " + this.iface.getName() + "#" + this.method.getName() + " " + Arrays.toString(args));

        final String topPath;
        if (this.iface.isAnnotationPresent(Path.class)) {
            topPath = this.iface.getAnnotation(Path.class).value();
        } else {
            topPath = "/";
        }

        final String path;
        if (this.method.isAnnotationPresent(Path.class)) {
            path = this.method.getAnnotation(Path.class).value();
        } else {
            path = "/";
        }

        final String httpMethod;
        if (this.method.isAnnotationPresent(HttpPost.class)) {
            httpMethod = "POST";
        } else if (this.method.isAnnotationPresent(HttpPut.class)) {
            httpMethod = "PUT";
        } else if (this.method.isAnnotationPresent(HttpDelete.class)) {
            httpMethod = "DELETE";
        } else {
            httpMethod = "GET";
        }

        final List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        if (null != paramAnnotations && paramAnnotations.length > 0) {
            for (int i = 0; i < paramAnnotations.length; i++) {
                final String value = String.valueOf(args[i]);
                final Annotation[] annotations = paramAnnotations[i];
                if (null != annotations && annotations.length > 0) {
                    for (final Annotation annotation : annotations) {
                        if (QueryParameter.class.equals(annotation.annotationType())) {
                            final String name = ((QueryParameter) annotation).value();
                            queryParams.add(new BasicNameValuePair(name, value));
                        }
                    }
                }
            }
        }

        final StringBuilder queryString = new StringBuilder();
        if (queryParams.size() > 0) {
            try {
                queryString.append("?" + EntityUtils.toString(new UrlEncodedFormEntity(queryParams)));
            } catch (final Exception e) {
                logger.error("Encoding query parameters error", e);
            }
        }

        final HttpRequestBase request;
        final String url = baseUrl + topPath + path + queryString;
        final InvocationTarget target = new InvocationTarget(url, httpMethod);

        logger.debug(target.toString());

        try {
            final URI uri = new URI(url);

            if (this.method.isAnnotationPresent(HttpPost.class)) {
                request = new org.apache.http.client.methods.HttpPost(uri);
            } else if (this.method.isAnnotationPresent(HttpPut.class)) {
                request = new org.apache.http.client.methods.HttpPut(uri);
            } else if (this.method.isAnnotationPresent(HttpDelete.class)) {
                request = new org.apache.http.client.methods.HttpDelete(uri);
            } else {
                request = new org.apache.http.client.methods.HttpGet(uri);
            }
        } catch (final URISyntaxException uriSyntaxException) {
            throw new InvocationException(target, null, uriSyntaxException);
        }

        HttpResponse response = null;

        try {
            for (final Map.Entry<String, String> entry : this.headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }

            request.setHeader("User-Agent", buildUserAgent(this.context));
            request.setHeader("Accept-Language", Locale.getDefault().getLanguage());

            if (null == (response = this.client.execute(request))) {
                throw new InvocationException(target);
            }
        } catch (final IOException e) {
            throw new InvocationException(target);
        }

        final StatusLine status = response.getStatusLine();
        if (null == status) {
            throw new InvocationException(target, status);
        }

        switch (status.getStatusCode()) {
        case 200: {
            final HttpEntity entity = response.getEntity();
            if (null == entity) {
                throw new InvocationException(target, status);
            }

            try {
                return new JSONTokener(EntityUtils.toString(entity)).nextValue();
            } catch (final Exception e) {
                throw new InvocationException(target, status, e);
            }
        }
        default:
            logger.debug(status.toString());
            throw new InvocationException(target, status);
        }
    }

    static String buildUserAgent(final Context context) {
        final String osVersion = "Android/" + Build.VERSION.RELEASE;
        final PackageManager pm = context.getPackageManager();
        final String packageName = context.getPackageName();

        try {
            final String versionName = pm.getPackageInfo(packageName, 0).versionName;
            return osVersion + " " + packageName + "/" + versionName;
        } catch (final Exception e) {
            return osVersion;
        }
    }

}
