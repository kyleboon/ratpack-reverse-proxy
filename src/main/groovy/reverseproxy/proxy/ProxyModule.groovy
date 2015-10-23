package reverseproxy.proxy

import com.google.inject.AbstractModule

import java.util.regex.Pattern

class ProxyModule extends AbstractModule {
	@Override
	protected void configure() {

	}

	static class Config {
		String forwardToHost = 'InvalidHost'
		Integer forwardToPort = 80
		String forwardToScheme = 'http'
		Boolean logRequests = false
		List<Pattern> filterOut = []

		String canaryHost = null
		Integer canaryPort = null
		String canaryScheme = null
		Integer canaryPercentage = null

		boolean isCanaryEnabled() {
			canaryHost && canaryPort && canaryScheme && canaryPercentage
		}
	}

	static ProxyHandler proxyHandler() {
		return new ProxyHandler()
	}

	static LoggingHandler loggingHandler() {
		return new LoggingHandler()
	}

	static CanaryRoutingHandler canaryHandler() {
		return new CanaryRoutingHandler()
	}

	static BlacklistHandler blacklistHandler() {
		return new BlacklistHandler()
	}
}
