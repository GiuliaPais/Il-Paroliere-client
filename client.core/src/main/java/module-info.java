module client.core {
	exports uninsubria.client.centralmanagement;
	exports uninsubria.client.settings;
	exports uninsubria.client.monitors;
	exports uninsubria.client.comm;

	requires transitive java.prefs;
	requires transitive javafx.base;
    requires utils.managers.api;
	requires utils.serviceResults;
	requires utils.connection;
	requires utils.business;
    requires utils.security;
}