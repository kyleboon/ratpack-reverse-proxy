package reverseproxy

import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.test.ApplicationUnderTest
import ratpack.test.embed.EmbeddedApp
import ratpack.test.http.TestHttpClient
import spock.lang.Shared
import spock.lang.Specification

class ReverseProxyBasicTest extends Specification {
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
	}

	def "get request to ratpack is proxied to the embedded app"() {
		expect:
		client.getText(url) == "rendered /${url}"

		where:
		url << ["", "api", "about"]
	}
}
