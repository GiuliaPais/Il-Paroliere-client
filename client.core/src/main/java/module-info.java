module client.core {
	exports uninsubria.client.centralmangment;
	exports uninsubria.client.comm;
	exports uninsubria.client.settings;

	requires transitive java.prefs;
	requires javafx.graphics;
	requires transitive javafx.base;
}