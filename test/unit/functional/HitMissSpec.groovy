package functional

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import grails.plugin.spock.UnitSpec
import grails.plugins.varnish.spock.VarnishConfiguration
import grails.plugins.varnish.spock.VarnishInstance
import groovyx.net.http.HTTPBuilder
import spock.extension.httpdmock.EndpointRoute
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.RequestToContract

class HitMissSpec extends UnitSpec {

    @HttpServerCfg(port=20203) // Jetty
    HttpTestServer server
    
    @VarnishConfiguration
    VarnishInstance varnish

    @RequestToContract(CacheableServiceRequestToContract)
    CacheableService cacheableService = Mock()

    def "Should be able to cache cacheable objects"() {
        given:
        HTTPBuilder http = new HTTPBuilder(varnish.baseUri)
        
        when: "we hit the url for the first time"
        String xCache = http.get(path: "/api/eternallyCacheable") { resp -> resp.headers."X-Cache" }
        
        then: "it will not be found in cache"
        xCache == "MISS"
        
        when: "we do it again"
        xCache = http.get(path: "/api/eternallyCacheable") { resp -> resp.headers."X-Cache" }
        
        then: "it should be fetched from Varnish's cache"
        xCache == "HIT"
    }
}

class CacheableServiceRequestToContract {

    CacheableService contract

    @EndpointRoute("/api/eternallyCacheable")
    def cacheable = {
        setHeaders {
            expires = "Thu, 24 Dec 2030 16:00:00 GMT"
        }
        
        plainResponse "should be cached"
    }
}

interface CacheableService {

    void eternallyCacheable()
}