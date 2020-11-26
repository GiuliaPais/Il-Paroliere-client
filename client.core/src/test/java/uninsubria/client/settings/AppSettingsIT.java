package uninsubria.client.settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

class AppSettingsIT {

    private final Preferences prefs = Preferences.userRoot();
    private ConnectionPrefs connPrefs;
    private AppSettings appSettings;

    @BeforeEach
    private void initParams() {
        this.connPrefs = new ConnectionPrefs("localhost, 192.168.36.1, 172.36.45.1");
        this.appSettings = new AppSettings(prefs, true, 16.0/9.0, 1920.0, 1080.0, "NIGHT_SKY", "ITALIAN", connPrefs);
    }

    @Test
    void testChangesToAppSettingsReflectOnConnectionPrefs() {
        appSettings.getConnectionPrefs().addAddress("156.45.13.1");
        assertEquals(appSettings.getConnectionPrefs().getServer_addresses(), connPrefs.getServer_addresses());
    }
}