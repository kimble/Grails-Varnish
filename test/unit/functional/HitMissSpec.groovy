package functional

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.RequestToContract
import functional.httpservice.CacheableService
import functional.httpservice.CacheableServiceRequestToContract
import grails.plugin.spock.UnitSpec
import grails.plugins.varnish.spock.VarnishConfiguration
import grails.plugins.varnish.spock.VarnishInstance
import groovyx.net.http.HTTPBuilder

class HitMissSpec extends UnitSpec {

    @HttpServerCfg(port=20203) HttpTestServer server
    @VarnishConfiguration VarnishInstance varnish

    @RequestToContract(CacheableServiceRequestToContract)
    CacheableService cacheableService = Mock()

    def "Should be able to cache cacheable objects"() {
        given: "a http client configured for Varnish"
        HTTPBuilder http = new HTTPBuilder(varnish.baseUri)
        
        expect: "the first object will not be served from cache"
        getXCacheHeader(http, "/api/eternallyCacheable") == "MISS"
        getXCacheHeader(http, "/api/eternallyCacheable") == "HIT"
    }
    
    private String getXCacheHeader(HTTPBuilder http, String requestPath) {
        http.get(path: requestPath) { resp -> resp.headers."X-Cache" }
    }
    
}
