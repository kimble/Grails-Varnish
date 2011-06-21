import grails.util.BuildSettingsHolder
import grails.util.Environment

import org.springframework.util.Assert

class BootStrap {
    
    static final int TEST_PORT = 20202
    
    File varnishProcessIdFile, varnishTemporaryDirectory

    def stopVarnish = {
        println "STOPPING VARNISH"
        if (varnishProcessIdFile.exists()) {
            String pid = varnishProcessIdFile.getText()
            if (pid.isLong()) {
                Process varnishKiller = [ "kill", pid ].execute()
                varnishProcessIdFile.delete()
                varnishKiller.waitFor()
            }
        }
    }
    
    def init = { servletContext ->
        if (this.respondsTo(Environment.current.name)) {
            this.invokeMethod(Environment.current.name, servletContext)
        }
    }
 
    def test(servletContext) {
        println "TEST BOOTSTRAP"
        startVarnish()
    }
    
    private void startVarnish() {
        File projectDirectory = findProjectDirectory()
        File testConfiguration = findVarnishTestConfiguration(projectDirectory)
        varnishProcessIdFile = new File("target/varnish.pid", projectDirectory)
        varnishTemporaryDirectory = new File("target/varnish-tmp", projectDirectory)
        
        Process varnishStarter = [ 
                "varnishd", "-f", testConfiguration.absolutePath, 
                "-s", "malloc,50M", 
                "-a", "localhost:${TEST_PORT}", 
                "-n", varnishTemporaryDirectory.absolutePath,
                "-P",  varnishProcessIdFile.absolutePath
            ].execute()
            
        varnishStarter.waitFor()
        Assert.isTrue(varnishStarter.exitValue() == 0, "Unexpected exit code: " + varnishStarter.exitValue() + ", " + varnishStarter.err.text)
        System.addShutdownHook(stopVarnish)
    }
    
    private File findVarnishTestConfiguration(File projectDirectory) {
        File testConfiguration = new File("test/config/test.vlc", projectDirectory)
        Assert.isTrue(testConfiguration.exists(), "Unable to find test configuration")
        return testConfiguration
    }
    
    private File findProjectDirectory() {
        File baseDirectory = new File(BuildSettingsHolder.settings.baseDir.toString())
        Assert.isTrue(baseDirectory.exists(), "Unable to get project directory")
        return baseDirectory
    }
  
}
