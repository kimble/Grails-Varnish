import grails.plugins.varnish.DefaultAsyncVarnishPurger

class VarnishGrailsPlugin {

    def version = "0.1"
    def grailsVersion = "1.3.7 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/*.gsp"
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "Varnish"
    def description = "Simplified Varnish integration"
    def documentation = "https://github.com/kimble/Grails-Varnish"

    def doWithSpring = {
        asyncVarnishPurger(DefaultAsyncVarnishPurger) { bean ->
            bean.destroyMethod = "teardown"
            varnishInstances = application.config.varnishInstances
        }
    }
    
    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    def doWithWebDescriptor = { xml -> }
    
}
