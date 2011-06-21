package grails.plugins.varnish.spock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.spockframework.runtime.extension.ExtensionAnnotation;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ExtensionAnnotation(VarnishExtension.class)
public @interface VarnishConfiguration {

}
