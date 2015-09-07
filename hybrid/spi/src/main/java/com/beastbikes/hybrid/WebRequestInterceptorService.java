package com.beastbikes.hybrid;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebResourceResponse;

import com.beastbikes.logging.Logger;
import com.beastbikes.logging.LoggerFactory;
import com.beastbikes.hybrid.spi.WebRequestInterceptor;

/**
 * The service that retrieves the {@link WebRequestInterceptor} implementations
 *
 * @author johnsonlee
 */
public class WebRequestInterceptorService {

    private static final Logger logger = LoggerFactory.getLogger("WebRequestInterceptorService");

    private static WebRequestInterceptorService service;

    /**
     * Returns the singleton of {@link WebRequestInterceptorService}
     *
     * @return the singleton of {@link WebRequestInterceptorService}
     */
    public static synchronized WebRequestInterceptorService getInstance() {
        if (null == service) {
            service = new WebRequestInterceptorService();
        }
        return service;
    }

    private ServiceLoader<WebRequestInterceptor> loader;

    private WebRequestInterceptor defaultInterceptor;

    private WebRequestInterceptorService() {
        this.loader = ServiceLoader.load(WebRequestInterceptor.class);
        this.defaultInterceptor = new DefaultWebRequestInterceptor();
    }

    /**
     * Intercept the specified request
     *
     * @param view
     *           The web view
     * @param uri
     *           The request URI
     * @param headers
     *           The request headers
     * @return the response data
     */
    public WebResourceResponse intercept(final WebView view, final Uri uri, final Map<String, String> headers) {
        WebResourceResponse wrr = null;

        final String protocol = uri.getScheme();

        try {
            final Iterator<WebRequestInterceptor> interceptors = this.loader.iterator();
            while (null == wrr && interceptors.hasNext()) {
                wrr = interceptors.next().intercept(view, uri, headers);
            }
        } catch (final ServiceConfigurationError e) {
            wrr = null;
            logger.error("Service configuration error", e);
        }

        if (null != wrr) {
            return wrr;
        }

        return this.defaultInterceptor.intercept(view, uri, headers);
    }

}
