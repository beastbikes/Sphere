package com.beastbikes.hybrid;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.beastbikes.logging.Logger;
import com.beastbikes.logging.LoggerFactory;
import com.beastbikes.hybrid.spi.WebRequestInterceptor;

/**
 * The default web request interceptor is used for http response caching
 *
 * @author johnsonlee
 */
class DefaultWebRequestInterceptor extends AbstractWebRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("DefaultWebRequestInterceptor");

    @Override
    public WebResourceResponse intercept(final WebView view, final Uri uri, final Map<String, String> headers) {
        final String url = uri.toString();
        if (!URLUtil.isNetworkUrl(url)) {
            logger.warn("Non-HTTP request ignored: " + url);
            return null;
        }

        try {
            final Class<?> clazz = Class.forName("android.net.http.HttpResponseCache");
            final Method getDefault = clazz.getMethod("getDefault");
            final Method getInstalled = clazz.getMethod("getInstalled");
            if (null == getDefault.invoke(clazz) && null == getInstalled.invoke(clazz)) {
                throw new NullPointerException("Neither default HttpResponseCache nor installed HttpResponseCache is available");
            }
        } catch (final Exception e) {
            logger.warn("No HTTP response cache found", e);
        }

        final Locale locale = Locale.getDefault();
        final String userAgent = view.getSettings().getUserAgentString();

        try {
            final URLConnection conn = new URL(url).openConnection();
            final Set<Map.Entry<String, String>> entries = headers.entrySet();

            for (final Map.Entry<String, String> header : entries) {
                conn.addRequestProperty(header.getKey(), header.getValue());
            }

            conn.setRequestProperty("User-Agent", userAgent);
            conn.setRequestProperty("Accept-Language", locale.getLanguage());
            conn.setUseCaches(true);
            conn.connect();

            final String contentType = conn.getContentType();
            final String contentEncoding = conn.getContentEncoding();
            final String mimeType = TextUtils.isEmpty(contentType)
                    ? URLConnection.guessContentTypeFromName(url)
                    : contentType.replaceAll(";\\s*.+$", "");
            final String encoding = TextUtils.isEmpty(contentEncoding)
                    ? "utf-8" : contentEncoding;
            return new WebResourceResponse(mimeType, encoding, conn.getInputStream());
        } catch (final Exception e) {
            logger.error(String.format(locale, "Intercept %s error", url), e);
        }

        return null;
    }

}
