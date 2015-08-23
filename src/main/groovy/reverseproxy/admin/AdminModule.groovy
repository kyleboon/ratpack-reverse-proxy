package reverseproxy.admin

import com.google.inject.AbstractModule

class AdminModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ConfigHandler)
	}
}
