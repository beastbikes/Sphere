package com.beastbikes.restful;

import android.app.Activity;
import android.content.Context;

import com.beastbikes.restful.annotation.HttpGet;
import com.beastbikes.restful.annotation.Path;
import com.beastbikes.restful.annotation.PathParameter;
import com.beastbikes.restful.annotation.QueryParameter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertTrue;

@Path("/functions")
interface SimpleServiceStub extends ServiceStub {

    @HttpGet("/hello")
    Object hello();

    @HttpGet("/{api}")
    Object hello(@PathParameter("api") String api);

}

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class ServiceStubFactoryTest {

    @Test
    public void sayHello() {
        final Context context = Robolectric.setupActivity(Activity.class);
        assertTrue(null != context);

        final ServiceStubFactory factory = new ServiceStubFactory(context);
        final SimpleServiceStub stub = factory.create(SimpleServiceStub.class, "http://beastbikes.com/1.1");
        assertTrue(null != stub);

        final Object result1 = stub.hello();
        System.out.println(result1);
        assertTrue(null != result1);

        final Object result2 = stub.hello("hello");
        System.out.println(result2);
        assertTrue(null != result2);
    }

}
