package reverseproxy.proxy

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.http.client.HttpClient
import ratpack.http.client.RequestSpec
import ratpack.http.client.StreamedResponse

class ProxyHandler extends GroovyHandler {
	@Override
	protected void handle(GroovyContext context) {
		context.with {
			HttpClient httpClient = context.get(HttpClient)
			URI proxyUri = context.get(URI)

			httpClient.requestStream(proxyUri) { RequestSpec spec ->
				spec.headers.copy(request.headers)
			}.then { StreamedResponse responseStream ->
				responseStream.forwardTo(response)
			}
		}
	}
}
