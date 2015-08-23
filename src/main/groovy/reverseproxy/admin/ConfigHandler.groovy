package reverseproxy.admin

import com.google.inject.Inject
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import reverseproxy.proxy.ProxyModule

import static ratpack.handlebars.Template.handlebarsTemplate

class ConfigHandler extends GroovyHandler {
	ProxyModule.Config config

	@Inject
	ConfigHandler(ProxyModule.Config config) {
		this.config = config
	}

	@Override
	protected void handle(GroovyContext context) {
		context.with {
			render handlebarsTemplate('reverseProxyAdmin.html', config: config)
		}
	}
}
