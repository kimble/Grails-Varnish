package grails.plugins.varnish.impl

import grails.plugin.spock.UnitSpec
import spock.extension.httpdmock.EndpointRoute
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.RequestToContract

class DefaultAsyncVarnishPurgerSpec extends UnitSpec {
    
    @HttpServerCfg(port=20500) HttpTestServer firstVarnish
    @HttpServerCfg(port=20501) HttpTestServer secondVarnish

    @RequestToContract(ShoppingRequestToContract)
    ContentPurger purger = Mock()
    
    def "Should be able to send purge requests to multiple Varnish instances"() {
        given: "a async varnish purger configured with two instances"
        DefaultAsyncVarnishPurger varnishPurger = new DefaultAsyncVarnishPurger()
        varnishPurger.varnishInstances = [ firstVarnish.baseUri, secondVarnish.baseUri ]
        varnishPurger.afterPropertiesSet()
        
        when: "we execute the following purge request"
        Set futures = varnishPurger.purge("developer-b.com\$", "^/blog")
        List responseBodies = futures*.get()*.getText()
        
        then: "two method invocations are registed on the mock"
        2 * purger.purge('developer-b.com$', "^/blog")
        
        and: "the response bodies indicates that both instances has been hit"
        responseBodies.size() == 2
        responseBodies.contains("Purged from localhost:20500")
        responseBodies.contains("Purged from localhost:20501")
    }
    
}

class ShoppingRequestToContract {
    
    ContentPurger contract
    
    @EndpointRoute("/")
    def purge = {
        assert request.getHeader("X-Purge") == "PURGE"
        String hostPattern = request.getHeader("X-Purge-Host-Regex")
        String pathPattern = request.getHeader("X-Purge-Url-Regex")
        String varnishHost = request.getHeader("Host")
        
        contract.purge(hostPattern, pathPattern)
        plainResponse "Purged from $varnishHost"
    }

}


interface ContentPurger {
    
    void purge(String hostPattern, String pathPattern)
    
}