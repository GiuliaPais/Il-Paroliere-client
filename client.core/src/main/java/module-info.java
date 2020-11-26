module client.core {
	exports uninsubria.client.centralmanagement;
	exports uninsubria.client.comm;
	exports uninsubria.client.settings;

	requires transitive java.prefs;
	requires transitive javafx.base;
    requires utils.managers.api;
	requires utils.serviceResults;
	requires utils.connection;
	requires utils.business;
    requires utils.security;
}