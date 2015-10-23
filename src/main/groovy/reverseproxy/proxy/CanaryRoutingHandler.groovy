package reverseproxy.proxy

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.registry.Registry

import java.util.concurrent.ThreadLocalRandom

class CanaryRoutingHandler extends GroovyHandler {
	@Override
	protected void handle(GroovyContext context) {
		context.with {
			ProxyModule.Config config = context.get(ProxyModule.Config)
			URI requestURI = new URI(request.rawUri)
			URI proxyUri

			if (config.canaryEnabled) {
				Random random = ThreadLocalRandom.current()
				Long randomLong = random.nextLong()

				if (randomLong % 100 <= config.canaryPercentage) {
					proxyUri = new URI(
							config.canaryScheme,
							requestURI.userInfo,
							config.canaryHost,
							config.canaryPort,
							requestURI.path,
							requestURI.query,
							requestURI.fragment)
				}
			}

			if (!proxyUri) {
				proxyUri = new URI(
						config.forwardToScheme,
						requestURI.userInfo,
						config.forwardToHost,
						config.forwardToPort,
						requestURI.path,
						requestURI.query,
						requestURI.fragment)

			}

			next(Registry.single(proxyUri))
		}
	}
}
