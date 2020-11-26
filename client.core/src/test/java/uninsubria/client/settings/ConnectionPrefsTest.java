package uninsubria.client.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionPrefsTest {

    private ConnectionPrefs connectionPrefs = new ConnectionPrefs("localhost, 192.168.2.1, 172.39.15.2");
    private String actual_val_1;
    private String actual_val_2;

    @Test
    void testStringAsAddressListWorks() {
        ObservableList<String> expected = FXCollections.observableArrayList("localhost", "192.168.2.1", "172.39.15.2");
        assertEquals(connectionPrefs.getServer_addresses(), expected);
    }

    @Test
    void testAddressesAsString() {
        assertEquals(connectionPrefs.addressesAsString(), "localhost,192.168.2.1,172.39.15.2");
    }

    @Test
    void testSetWorks() {
        ConnectionPrefs local = new ConnectionPrefs("localhost, 192.168.2.1, 172.39.15.2");
        String newPref = "localhost";
        local.setServer_addresses(newPref);
        assertEquals(local.getServer_addresses(), FXCollections.observableArrayList("localhost"));
        ObservableList<String> newAdd = FXCollections.observableArrayList("192.168.23.1");
        local.setServer_addresses(newAdd);
        assertEquals(local.getServer_addresses(), newAdd);
    }

    @Test
    void listenerWorksForListProperty_entireValue() {
        ConnectionPrefs local = new ConnectionPrefs("localhost, 192.168.2.1, 172.39.15.2");
        /* Check it works when all the observable list changes */
        local.server_addressesProperty().addListener(new ChangeListener<ObservableList<String>>() {

            @Override
            public void changed(ObservableValue<? extends ObservableList<String>> observable, ObservableList<String> oldValue, ObservableList<String> newValue) {
                String as_string = newValue.toString();
                actual_val_1 = as_string;
            }
        });
        local.setServer_addresses("localhost");
        assertEquals(actual_val_1, "[localhost]");
        local.setServer_addresses(FXCollections.observableArrayList("localhost", "192.16.22.1"));
        assertEquals(actual_val_1, "[localhost, 192.16.22.1]");
    }

    @Test
    void listenerWorksForListProperty_someEntries() {
        ConnectionPrefs local = new ConnectionPrefs("localhost, 192.168.2.1, 172.39.15.2");
        /* Check it works when all the observable list changes */
        local.server_addressesProperty().addListener(new ChangeListener<ObservableList<String>>() {

            @Override
            public void changed(ObservableValue<? extends ObservableList<String>> observable, ObservableList<String> oldValue, ObservableList<String> newValue) {
                String as_string = newValue.toString();
                actual_val_2 = as_string;
            }
        });
        local.getServer_addresses().add("172.5.13.1");
        assertEquals(actual_val_2, "[localhost, 192.168.2.1, 172.39.15.2, 172.5.13.1]");
    }
}