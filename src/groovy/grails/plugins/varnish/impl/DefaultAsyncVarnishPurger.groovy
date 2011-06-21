package grails.plugins.varnish.impl

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET
import grails.plugins.varnish.AsyncVarnishPurger
import groovyx.net.http.AsyncHTTPBuilder

import java.util.Collection
import java.util.concurrent.Future

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean

/**
 * Purges content from Varnish using HTTPBuilder. 
 * @author Kim A. Betti
 */
class DefaultAsyncVarnishPurger implements InitializingBean, AsyncVarnishPurger {

    private final static Logger log = LoggerFactory.getLogger(DefaultAsyncVarnishPurger)

    AsyncHTTPBuilder httpBuilder
    List<String> varnishInstances = []

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info "Setting up async http builder"
        httpBuilder = new AsyncHTTPBuilder(poolSize: 2, contentType: TEXT)
    }

    @Override
    Collection<Future<?>> purge(String pathPattern) {
        return purge(".*", pathPattern)
    }

    @Override
    Collection<Future<?>> purge(String hostPattern, String pathPattern) {
        Set<Future<?>> futures = new HashSet<Future<?>>()
        varnishInstances.each { String varnishUri ->
            futures << httpBuilder.request(varnishUri, GET, TEXT) {
                headers."X-Purge" = "PURGE"
                headers."X-Purge-Host-Regex" = hostPattern
                headers."X-Purge-Url-Regex" = pathPattern
            }
        }

        return futures
    }

    public void teardown() {
        log.info "Shutting down async http builder.."
        httpBuilder.shutdown()
    }
    
}
