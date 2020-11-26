package uninsubria.client.settings;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;

/**
 * Represents a set of preferences for possible server ip addresses.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class ConnectionPrefs {
    /*---Fields---*/
    private ListProperty<String> server_addresses;

    /*---Constructors---*/
    public ConnectionPrefs(String address_pref) {
        ObservableList<String> add = stringAsAddressList(address_pref);
        this.server_addresses = new SimpleListProperty<>(add);
    }

    public ConnectionPrefs(ObservableList<String> address_pref) {
        this.server_addresses = new SimpleListProperty<>(address_pref);
    }

    /*---Methods---*/
    /**
     * Converts the values in the list of addresses in a single string that is writable to
     * preferences
     * @return A string
     */
    public String addressesAsString() {
        String concat = "";
        String current;
        Iterator<String> iter = this.server_addresses.get().iterator();
        while (iter.hasNext()) {
            current = iter.next();
            if (!iter.hasNext()) {
                concat += current;
            } else {
                concat += current + ",";
            }
        }
        return concat;
    }

    /**
     * Converts the string read from preferences to an observable list
     * @param s The string of known addresses (comma separated)
     * @return An observable list
     */
    private ObservableList<String> stringAsAddressList(String s) {
        String[] tokens = s.split(",");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }
        ObservableList<String> address_list = FXCollections.observableArrayList(tokens);
        return address_list;
    }

    public ObservableList<String> getServer_addresses() {
        return server_addresses.get();
    }

    public ListProperty<String> server_addressesProperty() {
        return server_addresses;
    }

    public void setServer_addresses(ObservableList<String> server_addresses) {
        this.server_addresses.set(server_addresses);
    }

    public void setServer_addresses(String server_addresses) {
        ObservableList<String> add = stringAsAddressList(server_addresses);
        this.server_addresses.set(add);
    }

    public void addAddress(String address) {
        this.getServer_addresses().add(address);
    }

    public void removeAddress(String address) {
        this.getServer_addresses().remove(address);
    }
}
