package functional

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import org.springframework.util.Assert

import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.RequestToContract
import functional.httpservice.CacheableService
import functional.httpservice.CacheableServiceRequestToContract
import grails.plugin.spock.UnitSpec
import grails.plugins.varnish.spock.VarnishConfiguration
import grails.plugins.varnish.spock.VarnishInstance
import groovyx.net.http.HTTPBuilder

class PurgeSpec extends UnitSpec {
    
    @HttpServerCfg(port=20203) HttpTestServer server
    @VarnishConfiguration VarnishInstance varnish

    @RequestToContract(CacheableServiceRequestToContract)
    CacheableService cacheableService = Mock()

    def "We should be able to purge objects from Varnish cache"() {
        given: "a http client configured for Varnish"
        HTTPBuilder http = new HTTPBuilder(varnish.baseUri)
        
        when:
        ensurePathInCache(http, "/api/eternallyCacheable")
        purge(http, "localhost", "/api/eternallyCacheable")
        
        then:
        getXCacheHeader(http, "/api/eternallyCacheable") == "MISS"
        getXCacheHeader(http, "/api/eternallyCacheable") == "HIT"
    }
    
    def "We should be able to purge objects based on wildcard expression"() {
        given: "a http client configured for Varnish"
        HTTPBuilder http = new HTTPBuilder(varnish.baseUri)
        
        when:
        ensurePathInCache(http, "/api/eternallyCacheable")
        purge(http, "localhost", "/api/[a-zA-Z]+")
        
        then:
        getXCacheHeader(http, "/api/eternallyCacheable") == "MISS"
        getXCacheHeader(http, "/api/eternallyCacheable") == "HIT"
    }
    
    private void ensurePathInCache(HTTPBuilder http, String path) {
        Assert.isTrue(getXCacheHeader(http, path) == "MISS", "Object should not be in cache!")
        Assert.isTrue(getXCacheHeader(http, path) == "HIT", "Object should be cached")
    }
    
    private String getXCacheHeader(HTTPBuilder http, String requestPath) {
        http.get(path: requestPath) { resp -> resp.headers."X-Cache" }
    }
    
    private void purge(HTTPBuilder http, String hostRegex, String pathRegex) {
        http.request(varnish.baseUri, GET, TEXT) { 
            headers."X-Purge" = "PURGE"
            headers."X-Purge-Host-Regex" = hostRegex
            headers."X-Purge-Url-Regex" = pathRegex
            
            response.success = { resp, reader ->
                assert resp.statusLine.statusCode == 200 
                assert reader.text.contains("Purged.")
            }
        }
    }
    
}
