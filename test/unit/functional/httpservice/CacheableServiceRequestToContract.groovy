package functional.httpservice

import spock.extension.httpdmock.EndpointRoute

class CacheableServiceRequestToContract {

    CacheableService contract

    @EndpointRoute("/api/eternallyCacheable")
    def cacheable = {
        contract.eternallyCacheable()
        
        setHeaders {
            expires = "Thu, 24 Dec 2030 16:00:00 GMT"
        }
        
        plainResponse "should be cached"
    }
    
}
