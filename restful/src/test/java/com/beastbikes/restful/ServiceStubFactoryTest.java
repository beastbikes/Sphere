package com.beastbikes.restful;

import android.app.Activity;
import android.content.Context;

import com.beastbikes.restful.annotation.HttpGet;
import com.beastbikes.restful.annotation.Path;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertTrue;

@Path("/")
interface SimpleServiceStub extends ServiceStub {

    @HttpGet
    @Path("/hello")
    void hello();

}

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class ServiceStubFactoryTest {

    @Test
    public void sayHello() {
        final Context context = Robolectric.setupActivity(Activity.class);
        assertTrue(null != context);

        final ServiceStubFactory factory = new ServiceStubFactory(context);
        final SimpleServiceStub stub = factory.create(SimpleServiceStub.class, "http://beastbikes.com");
        assertTrue(null != stub);

        stub.hello();
    }

}
