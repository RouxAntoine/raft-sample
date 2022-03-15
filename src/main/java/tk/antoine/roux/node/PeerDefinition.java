package tk.antoine.roux.node;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeerDefinition {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public final InetAddress ip;
    public final Integer port;
    private static final String PEER_PORT_SEPARATOR = ":";

    public PeerDefinition(String peer) throws UnknownHostException {
        String[] remote = peer.split(PEER_PORT_SEPARATOR);
        try {
            this.ip = InetAddress.getByName(remote[0]);
            this.port = Integer.valueOf(remote[1]);
        } catch (UnknownHostException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        }
    }

    public PeerDefinition(InetAddress ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", ip.getHostAddress(), port);
    }
}
