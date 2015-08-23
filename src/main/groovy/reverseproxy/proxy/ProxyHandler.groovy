package reverseproxy.proxy

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.http.client.RequestSpec
import ratpack.http.client.StreamedResponse
import sun.net.www.http.HttpClient

class ProxyHandler extends GroovyHandler {
	@Override
	protected void handle(GroovyContext context) {
		context.with {
			ProxyModule.Config proxyConfig = context.get(ProxyModule.Config)
			HttpClient httpClient = context.get(HttpClient)

			URI requestURI = new URI(request.rawUri)
			URI proxyUri = new URI(
					proxyConfig.forwardToScheme,
					requestURI.userInfo,
					proxyConfig.forwardToHost,
					proxyConfig.forwardToPort,
					requestURI.path,
					requestURI.query,
					requestURI.fragment)

			httpClient.requestStream(proxyUri) { RequestSpec spec ->
				spec.headers.copy(request.headers)
			}.then { StreamedResponse responseStream ->
				responseStream.forwardTo(response)
			}
		}
	}
}
