package com.beastbikes.hybrid;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Collections;

import android.app.Activity;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebResourceResponse;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowHttpResponseCache;

import com.beastbikes.hybrid.WebRequestInterceptorService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21, shadows = { ShadowHttpResponseCache.class })
public class WebRequestInterceptorTest {

    @Test
    public void interceptWithDefault() {
        final Uri uri = Uri.parse("http://beastbikes.com");
        final Activity activity = Robolectric.setupActivity(Activity.class);
        assertTrue(null != activity);

        final File dir = new File(activity.getCacheDir(), "http");
        try {
            final Class<?> clazz = Class.forName("android.net.http.HttpResponseCache");
            final Method install = clazz.getMethod("install", File.class, long.class);
            final Object cache = install.invoke(clazz, dir, Long.MAX_VALUE);
            assertTrue(null != cache);
        } catch (final Exception e) {
            fail("HTTP response cache is unavailable");
        }

        final WebView browser = new WebView(activity);
        final Map<String, String> headers = Collections.<String, String>emptyMap();
        final WebResourceResponse wrr = WebRequestInterceptorService.getInstance().intercept(browser, uri, headers);
        assertTrue(null != wrr);
    }

}
