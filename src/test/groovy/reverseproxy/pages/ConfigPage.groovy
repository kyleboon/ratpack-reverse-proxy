package reverseproxy.pages

import geb.Page

class ConfigPage  extends Page {

	static at = { heading == "Config Data" }

	static content = {
		heading { $("h1").text() }
		host { $("li").text() }
		port { $("li", 1).text() }
		scheme { $("li", 2).text() }

	}
}
