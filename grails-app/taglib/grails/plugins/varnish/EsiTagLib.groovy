package grails.plugins.varnish

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Edge Side Includes
 * @see http://www.varnish-cache.org/trac/wiki/ESIfeatures
 * @author Kim A. Betti
 */
class EsiTagLib {
    
    static namespace = "esi"
    
    GrailsApplication grailsApplication
    
    /**
     * @emptyTag
     * @attr src URI
     */
    def include = { Map attr ->
        String link = attr.containsKey("src") ? attr["src"] : g.createLink(attr)
        out << String.format('<esi:include src="%s" />', link)
    }

}