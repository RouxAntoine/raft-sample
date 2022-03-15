package tk.antoine.roux.node;

import tk.antoine.roux.protocol.Dispatcher;
import tk.antoine.roux.protocol.Verbs;
import tk.antoine.roux.sockets.SocketProvider;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static tk.antoine.roux.node.PeerStatus.FOLLOWER;

public class Peer {
    protected static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final int MAX_REMOTE_ALIVE = 2;

    public final Dispatcher protocol;

    public final DatagramSocket datagramSocket;
    public final LocalPeerDefinition selfDefinition;
    public PeerStatus status = FOLLOWER;
    public ConcurrentMap<PeerDefinition, Integer> remoteClusterHealth;
    public PeerDefinition leader;

    private volatile boolean exit = false;

    public Peer(LocalPeerDefinition localPeerDefinition) throws IOException {
        selfDefinition = localPeerDefinition;
        remoteClusterHealth = localPeerDefinition
                .getPeers()
                .stream()
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toMap(Function.identity(), (x) -> MAX_REMOTE_ALIVE), ConcurrentHashMap::new
                        )
                );

        SocketProvider socketProvider = new SocketProvider(selfDefinition.self.ip, selfDefinition.self.port);
        datagramSocket = socketProvider.create();

        protocol = new Dispatcher(this);
    }

    public Thread listen() {
        byte[] buffer = new byte[256];

        Thread serverThread = new Thread(() -> {
            boolean exit = false;
            LOGGER.config("reception thread start");
            while (!exit) {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    datagramSocket.receive(datagramPacket);
                    PeerDefinition peer = trackPeer(datagramPacket.getAddress(), datagramPacket.getPort());

                    LOGGER.fine(() -> String.format("receive byte %s from remote %s", new String(buffer).trim(), peer));
                    Verbs msg = Verbs.fromBytes(buffer);
                    LOGGER.info(() -> String.format("receive verb %s from remote %s", msg.toString(), peer));
                    exit = protocol.dispatch(peer, msg);
                } catch (IOException e) {
                    LOGGER.warning(() -> String.format("Exception during packet reception %s", e.getMessage()));
                }
                Arrays.fill(buffer, (byte) '\0');
            }
            LOGGER.config("reception thread end");
        });
        serverThread.start();

        return serverThread;
    }

    public Thread quorum() {
        Thread electionThread = new Thread(() -> {
            int waitingTimeout;
            LOGGER.config("election thread start");
            while (!exit) {
                if (leader != null) {
                    waitingTimeout = 60_000;
                } else {
                    waitingTimeout = 30_000;
                }
                try {
                    protocol.keepAlive();

                    //noinspection BusyWait
                    Thread.sleep(waitingTimeout);

                } catch (InterruptedException | IOException e) {
                    LOGGER.warning(() -> String.format("Error into election loop, %s", e.getMessage()));
                }
            }
            LOGGER.config("election thread end");
        });
        electionThread.start();

        return electionThread;
    }

    private PeerDefinition trackPeer(InetAddress ip, Integer port) {
        return searchRemote(ip, port).orElseGet(() -> {
            PeerDefinition unknownPeer = new PeerDefinition(ip, port);
            remoteClusterHealth.put(unknownPeer, MAX_REMOTE_ALIVE);
            return unknownPeer;
        });
    }

    private Optional<PeerDefinition> searchRemote(InetAddress ip, Integer port) {
        return remoteClusterHealth.keySet()
                .stream()
                .filter(peer -> peer.ip.equals(ip) && peer.port.equals(port))
                .findFirst();
    }

    public void stop() {
        this.exit = true;
    }

    public void setLeader(PeerDefinition remote) {
        if (leader != remote) {
            this.leader = remote;
        }
    }

    public void countDownRemoteClusterHealth() {
        for (Map.Entry<PeerDefinition, Integer> entry : remoteClusterHealth.entrySet()) {
            PeerDefinition x = entry.getKey();
            Integer v = entry.getValue();
            if (v > 1) {
                remoteClusterHealth.replace(x, v - 1);
            } else {
                remoteClusterHealth.remove(x);
                if (x.equals(leader)) {
                    leader = null;
                }
            }
        }
    }

    public void resetRemoteHealth(PeerDefinition remote) {
        remoteClusterHealth.replace(remote, 2);
    }

    @Override
    public String toString() {
        return "Peer{ selfDefinition=" + selfDefinition + ", status=" + status + ", remoteCluster=" + remoteClusterHealth + ", leader=" + leader + " }";
    }
}
