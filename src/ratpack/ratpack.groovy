import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.config.ConfigData
import ratpack.handlebars.HandlebarsModule
import ratpack.http.client.HttpClient
import ratpack.http.client.RequestSpec
import ratpack.http.client.StreamedResponse
import reverseproxy.config.ProxyConfig

import static ratpack.groovy.Groovy.ratpack
import static ratpack.handlebars.Template.handlebarsTemplate

final Logger log = LoggerFactory.getLogger(ratpack)

ratpack {
  bindings {
    ConfigData configData = ConfigData.of { d ->
            d.props("$serverConfig.baseDir.file/application.properties")
            .env()
            .sysProps()
            .build()
    }

    module HandlebarsModule

    bindInstance(ProxyConfig, configData.get("/proxyConfig", ProxyConfig))
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

      log.info("Forwarding request from ${request.rawUri} to ${proxyUri.toString()}")

      httpClient.requestStream(proxyUri) { RequestSpec spec ->
        spec.headers.copy(request.headers)
      }.then { StreamedResponse responseStream ->
        responseStream.forwardTo(response)
      }
    }
  }
}
