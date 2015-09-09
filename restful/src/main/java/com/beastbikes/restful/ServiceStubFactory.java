package com.beastbikes.restful;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class ServiceStubFactory {

    private Context context;

    public ServiceStubFactory(final Context context) {
        this.context = context;
    }

    public <T extends ServiceStub> T create(final Class<T> iface, final String baseUrl) {
        return this.create(iface, baseUrl, Collections.<String, String>emptyMap());
    }

    public <T extends ServiceStub> T create(final Class<T> iface, final String baseUrl, final Map<String, String> headers) {
        final Map<Method, Invocation> invocations = new HashMap<Method, Invocation>();

        for (final Method method : iface.getMethods()) {
            invocations.put(method, new ServiceStubInvocation(this.context, iface, method, baseUrl, headers));
        }

        final ServiceStubProxy proxy = new ServiceStubProxy(iface, invocations);
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[] { iface }, proxy);
    }

}
