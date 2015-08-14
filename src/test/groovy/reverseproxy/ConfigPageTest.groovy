package reverseproxy

import geb.spock.GebReportingSpec
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.test.ApplicationUnderTest
import reverseproxy.pages.ConfigPage
import spock.lang.Shared

class ConfigPageTest extends GebReportingSpec {
	@Shared
	ApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()

	def setupSpec() {
		System.setProperty('ratpack.proxyConfig.forwardToHost', 'testhost')
		System.setProperty('ratpack.proxyConfig.forwardToPort', '123')
		System.setProperty('ratpack.proxyConfig.forwardToScheme', 'https')
	}

	def setup() {
		browser.baseUrl = aut.address.toString() + 'reverseProxyAdmin'
	}

	def "no books are listed"() {
		when:
		to ConfigPage

		then:
		host == "Proxied Host: testhost"
		port == "Proxied Port: 123"
		scheme == "Proxied Scheme: https"
	}
}
