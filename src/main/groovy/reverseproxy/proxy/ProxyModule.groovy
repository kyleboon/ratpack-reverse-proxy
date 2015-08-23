package reverseproxy.proxy

import ratpack.guice.ConfigurableModule

class ProxyModule extends ConfigurableModule<ProxyModule.Config> {
	@Override
	protected void configure() {

	}

	static class Config {
		String forwardToHost = 'InvalidHost'
		Integer forwardToPort = 80
		String forwardToScheme = 'http'
	}

	static ProxyHandler proxyHandler() {
		return new ProxyHandler()
	}
}
