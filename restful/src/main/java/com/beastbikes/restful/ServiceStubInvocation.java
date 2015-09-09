package com.beastbikes.restful;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.net.Uri;
import android.os.Build;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpUriRequest;

import org.json.JSONException;
import org.json.JSONTokener;

import com.beastbikes.logging.Logger;
import com.beastbikes.logging.LoggerFactory;
import com.beastbikes.restful.annotation.HttpDelete;
import com.beastbikes.restful.annotation.HttpPost;
import com.beastbikes.restful.annotation.HttpPut;
import com.beastbikes.restful.annotation.Path;

class ServiceStubInvocation implements Invocation {

    private static final Logger logger = LoggerFactory.getLogger("ServiceStubInvocation");

    final Context context;

    final Class<?> iface;

    final Method method;

    final String baseUrl;

    final AndroidHttpClient client;

    ServiceStubInvocation(final Context context, final Class<?> iface, final Method method, final String baseUrl) {
        this.context = context;
        this.iface = iface;
        this.method = method;
        this.baseUrl = baseUrl;
        this.client = AndroidHttpClient.newInstance(buildUserAgent(context), context);
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

        final HttpUriRequest request;
        final String url = baseUrl + topPath + path;
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
