package grails.plugins.varnish.spock;

import grails.util.BuildSettingsHolder

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.Assert

public class VarnishInstance {

    private static final Logger log = LoggerFactory.getLogger(VarnishInstance)
    
    private int varnishListeningPort
    private File varnishProcessIdFile, varnishTemporaryDirectory

    public VarnishInstance(int port) {
        this.varnishListeningPort = port
    }

    public String getBaseUri() {
        "http://localhost:${varnishListeningPort}"
    }
    
    public void start() {
        log.info("Staring Varnish")
        File projectDirectory = findProjectDirectory()
        File testConfiguration = findVarnishTestConfiguration(projectDirectory)
        varnishProcessIdFile = new File("target/varnish.pid", projectDirectory)
        varnishTemporaryDirectory = new File("target/varnish-tmp", projectDirectory)
        
        Process varnishStarter = [ 
                "varnishd", "-f", testConfiguration.absolutePath, 
                "-s", "malloc,16M", 
                "-a", "localhost:${varnishListeningPort}", 
                "-n", varnishTemporaryDirectory.absolutePath,
                "-P",  varnishProcessIdFile.absolutePath
            ].execute()
            
        varnishStarter.waitFor()
        Assert.isTrue(varnishStarter.exitValue() == 0, 
            "Unexpected exit code: " + varnishStarter.exitValue() + ", " + varnishStarter.err.text)
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

    public void stop() {
        log.info("Stopping Varnish")
        if (varnishProcessIdFile.exists()) {
            String pid = varnishProcessIdFile.getText()
            if (pid.isLong()) {
                Process varnishKiller = [ "kill", pid ].execute()
                varnishProcessIdFile.delete()
                varnishKiller.waitFor()
            }
        }
    }

}
