package reverseproxy.proxy

import groovy.util.logging.Slf4j
import ratpack.exec.Blocking
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.http.Request
import ratpack.http.TypedData

@Slf4j
class LoggingHandler extends GroovyHandler {
	@Override
	protected void handle(GroovyContext context) {
		context.with {
			ProxyModule.Config config = context.get(ProxyModule.Config)

			if (config.logRequests) {
				Request request = context.request
				request.body.then { TypedData body ->
					Blocking.exec {
						String logMessage  = """path=${request.path}
                                            method=${request.method.name}
                                            params=${request.queryParams}
                                             body=${body.text}
                                         """.stripIndent()
						log.info(logMessage)
					}
				}

			}
			next()
		}
	}
}
