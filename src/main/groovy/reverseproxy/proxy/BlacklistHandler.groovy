package reverseproxy.proxy

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

class BlacklistHandler extends GroovyHandler {
	@Override
	protected void handle(GroovyContext context) {
		context.with {
			ProxyModule.Config config = context.get(ProxyModule.Config)

			if (config.filterOut.any { it.matcher(context.request.path).matches() }) {
				context.render("Request path has been blacklisted")
			} else {
				next()
			}
		}
	}
}
