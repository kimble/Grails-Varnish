package grails.plugins.varnish.spock;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.FieldInfo;

public class VarnishInterceptor implements IMethodInterceptor {

    private final static Logger log = LoggerFactory.getLogger(VarnishInterceptor.class);

    private FieldInfo varnishField;

    public VarnishInterceptor(VarnishConfiguration varnish, FieldInfo varnishField) {
        this.varnishField = varnishField;
    }

    @Override
    public void intercept(IMethodInvocation invocation) throws Throwable {
        log.debug("Setting up Varnish instance for test");
        VarnishInstance varnishInstance = new VarnishInstance(20202);
        injectVarnishInstanceIntoSpec(varnishInstance, invocation.getTarget());
        try {
            varnishInstance.start();
            invocation.proceed();
        } finally {
            varnishInstance.stop();
        }
    }

    private void injectVarnishInstanceIntoSpec(VarnishInstance varnishInstance, Object specInstance) {
        String varnishFieldName = varnishField.getName();
        InvokerHelper.setProperty(specInstance, varnishFieldName, varnishInstance);
    }

}