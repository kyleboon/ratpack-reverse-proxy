import com.google.common.io.Resources

import ratpack.handlebars.HandlebarsModule
import reverseproxy.admin.AdminModule
import reverseproxy.admin.ConfigHandler
import reverseproxy.proxy.ProxyModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
  serverConfig { d -> d
    .props(Resources.asByteSource(Resources.getResource('application.properties')))
    .env()
    .sysProps()
    .require('/proxyConfig', ProxyModule.Config)
  }

  bindings {
    module HandlebarsModule
    module ProxyModule
    module AdminModule
  }

  handlers {
    get('reverseProxyAdmin', ConfigHandler)

    all ProxyModule.loggingHandler()
    all ProxyModule.blacklistHandler()
    all ProxyModule.canaryHandler()
    all ProxyModule.proxyHandler()
  }
}
