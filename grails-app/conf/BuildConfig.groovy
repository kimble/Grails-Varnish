grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
        mavenRepo "http://repository.codehaus.org"
        
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    
    dependencies {
        test("org.mortbay.jetty:jetty-embedded:6.1.26") {
            export = false
        }
        
        compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.0') {
            excludes "commons-logging", "xml-apis", "groovy"
        }
    }
    
    plugins {
        test(":spock:0.5-groovy-1.7") {
            export = false
        }
    }
    
}
