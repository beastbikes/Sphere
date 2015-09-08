package com.beastbikes.restful;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

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

    ServiceStubInvocation(final Context context, final Class<?> iface, final Method method, final String baseUrl) {
        this.context = context;
        this.iface = iface;
        this.method = method;
        this.baseUrl = baseUrl;
    }

    @Override
    public Object invoke(final Object... args) {
        logger.debug("Invoking " + this.iface.getName() + "#" + this.method.getName() + " " + Arrays.toString(args));

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

        final Uri uri = Uri.parse(baseUrl + topPath + path);
        logger.debug(httpMethod + " " +  uri.toString());

        // TODO

        return null;
    }

    static String getUserAgent(final Context context) {
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
