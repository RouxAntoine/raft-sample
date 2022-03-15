package tk.antoine.roux.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalPeerDefinition {

    String id;
    PeerDefinition self;
    List<PeerDefinition> peers = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PeerDefinition> getPeers() {
        return new ArrayList<>(peers);
    }

    public void addPeer(PeerDefinition peer) {
        this.peers.add(peer);
    }

    public void setPeers(List<PeerDefinition> peers) {
        this.peers = peers;
    }

    public PeerDefinition getSelf() {
        return self;
    }

    public void setSelf(PeerDefinition self) {
        this.self = self;
    }

    public boolean isValid() {
        return id != null && self != null;
    }

    @Override
    public String toString() {
        return "LocalPeerDefinition{" +
                "id='" + id + '\'' +
                ", self=" + self +
                ", peers=" + peers +
                '}';
    }
}
