import grails.util.Environment

class BootStrap {
    
    def init = { servletContext ->
        if (this.respondsTo(Environment.current.name)) {
            this.invokeMethod(Environment.current.name, servletContext)
        }
    }
 
    def test(servletContext) {
        // called before integration tests
    }
    
}
