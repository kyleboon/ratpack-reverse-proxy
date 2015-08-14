import com.google.common.io.Resources
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.handlebars.HandlebarsModule
import ratpack.http.client.HttpClient
import ratpack.http.client.RequestSpec
import ratpack.http.client.StreamedResponse
import reverseproxy.config.ProxyConfig

import static ratpack.groovy.Groovy.ratpack
import static ratpack.handlebars.Template.handlebarsTemplate

ratpack {
  serverConfig { d -> d
    .props(Resources.asByteSource(Resources.getResource('application.properties')))
    .env()
    .sysProps()
    .require('/proxyConfig', ProxyConfig)
  }

  bindings {
    module HandlebarsModule
  }

  handlers {
    get('reverseProxyAdmin') { ProxyConfig proxyConfig ->
      render handlebarsTemplate('reverseProxyAdmin.html', config: proxyConfig)
    }

    all { HttpClient httpClient, ProxyConfig proxyConfig ->
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
