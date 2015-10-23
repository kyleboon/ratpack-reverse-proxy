package reverseproxy

import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.test.ApplicationUnderTest
import ratpack.test.embed.EmbeddedApp
import ratpack.test.http.TestHttpClient
import spock.lang.Shared
import spock.lang.Specification

/**
 *  Functional test for canary routing
 */
class CanaryRoutingTest  extends Specification {
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

	@Shared
	EmbeddedApp canaryHost = GroovyEmbeddedApp.of {
		handlers {
			all {
				render "canary ${request.rawUri}"
			}
		}
	}

	def setupSpec() {
		System.setProperty('ratpack.proxyConfig.forwardToHost', proxiedHost.address.host)
		System.setProperty('ratpack.proxyConfig.forwardToPort', Integer.toString(proxiedHost.address.port))
		System.setProperty('ratpack.proxyConfig.forwardToScheme', proxiedHost.address.scheme)

		System.setProperty('ratpack.proxyConfig.canaryHost', canaryHost.address.host)
		System.setProperty('ratpack.proxyConfig.canaryPort', Integer.toString(canaryHost.address.port))
		System.setProperty('ratpack.proxyConfig.canaryScheme', canaryHost.address.scheme)
		System.setProperty('ratpack.proxyConfig.canaryPercentage', Integer.toString(100))
	}

	def cleanupSpec() {
		System.clearProperty('ratpack.proxyConfig.forwardToHost')
		System.clearProperty('ratpack.proxyConfig.forwardToPort')
		System.clearProperty('ratpack.proxyConfig.forwardToScheme')

		System.clearProperty('ratpack.proxyConfig.canaryHost')
		System.clearProperty('ratpack.proxyConfig.canaryPort')
		System.clearProperty('ratpack.proxyConfig.canaryScheme')
		System.clearProperty('ratpack.proxyConfig.canaryPercentage')
	}

	def "get request to ratpack is proxied to the embedded app when canary percentage is 100"() {
		expect:
		client.getText(url) == "canary /${url}"

		where:
		url << ["", "api", "about"]
	}
}
