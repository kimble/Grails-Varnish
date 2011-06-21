package grails.plugins.varnish.spock;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.SpecInfo;

public class VarnishExtension extends AbstractAnnotationDrivenExtension<VarnishConfiguration> {

    @Override
    public void visitFieldAnnotation(VarnishConfiguration annotation, FieldInfo field) {
        SpecInfo spec = field.getParent();
        for (FeatureInfo feature : spec.getAllFeatures()) {
            addInterceptor(annotation, feature.getFeatureMethod(), field);
        }
    }

    private void addInterceptor(VarnishConfiguration varnish, MethodInfo featureMethod, FieldInfo varnishField) {
        featureMethod.addInterceptor(new VarnishInterceptor(varnish, varnishField));
    }

}
