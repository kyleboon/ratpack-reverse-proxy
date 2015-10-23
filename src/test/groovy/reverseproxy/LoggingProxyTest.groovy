package reverseproxy

import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.test.ApplicationUnderTest
import ratpack.test.embed.EmbeddedApp
import ratpack.test.http.TestHttpClient
import spock.lang.Shared
import spock.lang.Specification

class LoggingProxyTest extends Specification {
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
	}

	def cleanupSpec() {
		System.clearProperty('ratpack.proxyConfig.forwardToHost')
		System.clearProperty('ratpack.proxyConfig.forwardToPort')
		System.clearProperty('ratpack.proxyConfig.forwardToScheme')
		System.clearProperty('ratpack.proxyConfig.logRequests')
	}

	// ok so i don't have a great way to verify the log is actully written to, but i want to at least
	// make sure nothing breaks when logging is enabled
	def "get request to ratpack is proxied to the embedded app and logged"() {
		expect:
		client.getText(url) == "rendered /${url}"

		where:
		url << ["", "api", "about"]
	}
}
