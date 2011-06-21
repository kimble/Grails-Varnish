package grails.plugins.varnish;

import java.util.Collection;
import java.util.concurrent.Future;

/**
 *
 * @author Kim A. Betti
 */
public interface AsyncVarnishPurger {

    /**
     * Purges all objects found in cache matching both
     * the host and path pattern expression.
     * @param hostPattern regex matching the http host
     * @param pathPattern for example ^/images
     * @return
     */
    Collection<Future<?>> purge(String hostPattern, String pathPattern);

    /**
     * Purges all objects that matches the path expression
     * regarding what http host they're associated with.
     * @param pathPattern for example ^/images
     * @return
     */
    Collection<Future<?>> purge(String pathPattern);

}