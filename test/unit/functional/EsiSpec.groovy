package functional

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import org.springframework.util.Assert

import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.RequestToContract
import functional.httpservice.shopping.ShoppingContract
import functional.httpservice.shopping.ShoppingRequestToContract
import grails.plugin.spock.UnitSpec
import grails.plugins.varnish.spock.VarnishConfiguration
import grails.plugins.varnish.spock.VarnishInstance
import groovyx.net.http.HTTPBuilder

class EsiSpec extends UnitSpec {
    
    @HttpServerCfg(port=20203) HttpTestServer server
    @VarnishConfiguration VarnishInstance varnish

    @RequestToContract(ShoppingRequestToContract)
    ShoppingContract shoppingService = Mock()

    def "Shopping cart contents should never be cached"() {
        given: "a http client configured for Varnish"
        HTTPBuilder http = new HTTPBuilder(varnish.baseUri)
        
        expect:
        getXCacheHeader(http, "/shopping/cart") == "MISS"
        getXCacheHeader(http, "/shopping/cart") == "MISS"
    }
    
    def "Requesting product details should include the shopping cart"() {
        given: "a http client configured for Varnish"
        HTTPBuilder http = new HTTPBuilder(varnish.baseUri)
        
        when: "we request a page containing product information and a shopping cart two times"
        String firstResult = request(http, "/shopping/product/Groovy-in-Action")
        String secondResult = request(http, "/shopping/product/Groovy-in-Action")
        
        then: "only the shopping cart should be requested two times from the backend"
        1 * shoppingService.productDetails("Groovy-in-Action")
        2 * shoppingService.cart()
        
        and: "both results contains shopping cart and product information"
        firstResult.contains("Shopping cart contents") && secondResult.contains("Shopping cart contents")
        firstResult.contains("Product: Groovy-in-Action") && secondResult.contains("Product: Groovy-in-Action")
    }
    
    private void ensurePathInCache(HTTPBuilder http, String... paths) {
        paths.each { path ->
            Assert.isTrue(getXCacheHeader(http, path) == "MISS", "Object should not be in cache!")
            Assert.isTrue(getXCacheHeader(http, path) == "HIT", "Object should be cached")
        }
    }
    
    private String getXCacheHeader(HTTPBuilder http, String requestPath) {
        http.get(path: requestPath) { resp -> resp.headers."X-Cache" }
    }
        
    private String request(HTTPBuilder http, String requestPath) {
        http.request(varnish.baseUri + requestPath, GET, TEXT) { }.text
    }

}
