package reverseproxy

import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.test.ApplicationUnderTest
import ratpack.test.embed.EmbeddedApp
import ratpack.test.http.TestHttpClient
import spock.lang.Shared
import spock.lang.Specification

class BlacklistedUrlTest extends Specification {
	@Shared
	ApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()

	TestHttpClient client = aut.httpClient

	@Shared
	EmbeddedApp proxiedHost = GroovyEmbeddedApp.of {
		handlers {
			all {
				render "rendered ${request.rawUri}"
			}
		}
	}

	def setupSpec() {
		System.setProperty('ratpack.proxyConfig.forwardToHost', proxiedHost.address.host)
		System.setProperty('ratpack.proxyConfig.forwardToPort', Integer.toString(proxiedHost.address.port))
		System.setProperty('ratpack.proxyConfig.forwardToScheme', proxiedHost.address.scheme)
		System.setProperty('ratpack.proxyConfig.logRequests', true.toString())
		System.setProperty('ratpack.proxyConfig.filterOut[0]', /.*/.toString())
	}

	def cleanupSpec() {
		System.clearProperty('ratpack.proxyConfig.forwardToHost')
		System.clearProperty('ratpack.proxyConfig.forwardToPort')
		System.clearProperty('ratpack.proxyConfig.forwardToScheme')
		System.clearProperty('ratpack.proxyConfig.logRequests')
		System.clearProperty('ratpack.proxyConfig.filterOut[0]')
	}

	def "get request to ratpack is blacklisted"() {
		expect:
		client.getText(url) == "Request path has been blacklisted"

		where:
		url << ["", "api", "about"]
	}
}
