package functional.httpservice.shopping

import spock.extension.httpdmock.EndpointRoute

class ShoppingRequestToContract {
    
    ShoppingContract contract
    
    @EndpointRoute("/shopping/cart")
    def shoppingcart = {
        setHeaders {
            expires = "Wed, 24 Dec 1980 16:00:00 GMT"
        }
        
        contract.cart()
        plainResponse "Shopping cart contents: ..."
    }
    
    @EndpointRoute("/shopping/product/@productName")
    def productDetails = {
        setHeaders {
            expires = "Thu, 24 Dec 2030 16:00:00 GMT"
        }
        
        contract.productDetails(route.productName)
        plainResponse """<esi:include src="/shopping/cart" />\nProduct: ${route.productName}"""
    }

}
