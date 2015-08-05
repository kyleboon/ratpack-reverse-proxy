import ratpack.http.client.HttpClient
import ratpack.http.client.RequestSpec
import ratpack.http.client.StreamedResponse

import static ratpack.groovy.Groovy.ratpack

ratpack {
  handlers {
    all { HttpClient httpClient ->
      URI proxyUri = new URI(request.rawUri)
      proxyUri.host = 'www.cellarhq.com'
      proxyUri.scheme = 'https'
      httpClient.requestStream(proxyUri) { RequestSpec spec ->
        spec.headers.copy(request.headers)
      }.then { StreamedResponse responseStream ->
        responseStream.send(response)
      }
    }
  }
}
