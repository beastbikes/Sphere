package com.beastbikes.hybrid.spi;

import java.util.Map;

import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

/**
 * The web request interceptor is used for URL intercepting by web view
 *
 * @author johnsonlee
 */
public interface WebRequestInterceptor {

    /**
     * Intercept the specified URL
     *
     * @param view
     *           The web view
     * @param uri
     *           The request URI
     * @param headers
     *           The request headers
     * @return the response data or null
     */
    public WebResourceResponse intercept(final WebView view, final Uri uri, final Map<String, String> headers);

}
