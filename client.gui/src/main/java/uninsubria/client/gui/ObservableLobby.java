package uninsubria.client.gui;

import javafx.beans.property.*;
import uninsubria.utils.business.Lobby;
import uninsubria.utils.languages.Language;
import uninsubria.utils.ruleset.Ruleset;

import java.util.Objects;
import java.util.UUID;

/**
 * Wrapper of a lobby implementing observable properties for display on the GUI.
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class ObservableLobby {
    /*---Fields---*/
    private final UUID roomId;
    private StringProperty roomName;
    private IntegerProperty numPlayers;
    private ObjectProperty<Language> language;
    private ObjectProperty<Ruleset> ruleset;
    private ObjectProperty<Lobby.LobbyStatus> status;

    /*---Constructors---*/
    private ObservableLobby(UUID roomId, String roomName, Integer numPlayers,
                           Language language, Ruleset ruleset,
                           Lobby.LobbyStatus status) {
        this.roomId = roomId;
        this.roomName = new SimpleStringProperty(roomName);
        this.numPlayers = new SimpleIntegerProperty(numPlayers);
        this.language = new SimpleObjectProperty<>(language);
        this.ruleset = new SimpleObjectProperty<>(ruleset);
        this.status = new SimpleObjectProperty<>(status);
    }

    /*---Methods---*/
    public static ObservableLobby toObservableLobby(Lobby lobby) {
        return new ObservableLobby(lobby.getRoomId(), lobby.getRoomName(), lobby.getNumPlayers(),
                lobby.getLanguage(), lobby.getRuleset(), lobby.getStatus());
    }

    public UUID getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName.get();
    }

    public StringProperty roomNameProperty() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName.set(roomName);
    }

    public int getNumPlayers() {
        return numPlayers.get();
    }

    public IntegerProperty numPlayersProperty() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers.set(numPlayers);
    }

    public Language getLanguage() {
        return language.get();
    }

    public ObjectProperty<Language> languageProperty() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language.set(language);
    }

    public Ruleset getRuleset() {
        return ruleset.get();
    }

    public ObjectProperty<Ruleset> rulesetProperty() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset.set(ruleset);
    }

    public Lobby.LobbyStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<Lobby.LobbyStatus> statusProperty() {
        return status;
    }

    public void setStatus(Lobby.LobbyStatus status) {
        this.status.set(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservableLobby that = (ObservableLobby) o;
        return roomId.equals(that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public String toString() {
        return "ObservableLobby{" +
                "roomId=" + roomId +
                ", roomName=" + roomName +
                ", numPlayers=" + numPlayers +
                ", language=" + language +
                ", ruleset=" + ruleset +
                ", status=" + status +
                '}';
    }
}
