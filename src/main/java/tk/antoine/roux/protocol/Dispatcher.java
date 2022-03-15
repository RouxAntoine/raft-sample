package tk.antoine.roux.protocol;

import tk.antoine.roux.node.Peer;
import tk.antoine.roux.node.PeerDefinition;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.DatagramPacket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static tk.antoine.roux.node.PeerStatus.CANDIDATE;
import static tk.antoine.roux.node.PeerStatus.FOLLOWER;
import static tk.antoine.roux.node.PeerStatus.LEADER;
import static tk.antoine.roux.protocol.Verbs.AM_I_LEADER;
import static tk.antoine.roux.protocol.Verbs.HEALTH;
import static tk.antoine.roux.protocol.Verbs.HEALTHY;
import static tk.antoine.roux.protocol.Verbs.I_AM_LEADER;
import static tk.antoine.roux.protocol.Verbs.I_AM_CANDIDATE;
import static tk.antoine.roux.protocol.Verbs.LEADER_EXIST;
import static tk.antoine.roux.protocol.Verbs.NO;
import static tk.antoine.roux.protocol.Verbs.YES;

public class Dispatcher {
    protected static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final int THRESHOLD_NO_LEADER = 2;

    private final Peer self;
    private int noLeaderCounter = 0;

    private final Set<PeerDefinition> voteYes = new HashSet<>();

    public Dispatcher(Peer peer) {
        self = peer;
    }

    public boolean dispatch(PeerDefinition remote, Verbs msg) throws IOException {
        boolean exit = false;
        switch (msg) {
            case EXIT -> exit = true;
            case I_AM_LEADER -> {
                noLeaderCounter = 0;
                self.setLeader(remote);
            }
            case I_AM_CANDIDATE, NO -> resetCandidacy();
            case LEADER_EXIST -> {
                switch (self.status) {
                    case LEADER -> unicastMessage(remote, I_AM_LEADER);
                    case CANDIDATE -> unicastMessage(remote, I_AM_CANDIDATE);
                }
            }
            case AM_I_LEADER -> {
                if (self.status.equals(FOLLOWER)) {
                    unicastMessage(remote, YES);
                    // prevent to became candidate during another peer candidacy
                    noLeaderCounter = 0;
                } else {
                    unicastMessage(remote, NO);
                }
            }
            case YES -> {
                if (self.status.equals(CANDIDATE)) {
                    voteYes.add(remote);
                    checkLeaderCondition();
                }
            }
            case INFO -> LOGGER.info(this.toString());
            case HEALTH -> unicastMessage(remote, HEALTHY);
            case HEALTHY -> self.resetRemoteHealth(remote);
            case NONE -> LOGGER.fine("receive invalid verbs, nothing to do");
        }
        return exit;
    }

    private void checkLeaderCondition() {
        double requiredApproval = Math.ceil(((double) self.remoteClusterHealth.size()) / 2.0);
        if (voteYes.size() >= requiredApproval) {
            LOGGER.info(String.format("Majority obtained became leader %d/%f (cluster size %d)", voteYes.size(), requiredApproval, self.remoteClusterHealth.size()));
            noLeaderCounter = 0;
            self.status = LEADER;
            voteYes.clear();
            self.setLeader(self.selfDefinition.getSelf());
        }
    }

    public void keepAlive() throws IOException {
        if (noLeaderCounter >= THRESHOLD_NO_LEADER) {
            startVoting();
        } else {
            if (self.leader == null) {
                noLeaderCounter++;
                broadcastMessage(LEADER_EXIST);
            } else {
                broadcastMessage(HEALTH);
                self.countDownRemoteClusterHealth();
            }
        }
    }

    public void unicastMessage(PeerDefinition remote, Verbs verb) throws IOException {
        byte[] buffer = verb.getBytes();

        LOGGER.info(() -> String.format("send : %s to %s", new String(buffer), remote));

        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, remote.ip, remote.port);
        self.datagramSocket.send(datagramPacket);
    }

    public void broadcastMessage(Verbs verb) throws IOException {
        for (PeerDefinition remote : self.remoteClusterHealth.keySet()) {
            LOGGER.fine(() -> String.format("broadcast to peer %s", remote));
            unicastMessage(remote, verb);
        }
    }

    private void resetCandidacy() {
        noLeaderCounter = 0;
        self.status = FOLLOWER;
        voteYes.clear();
    }

    private void startVoting() throws IOException {
        self.status = CANDIDATE;
        broadcastMessage(AM_I_LEADER);
    }

    @Override
    public String toString() {
        return "Dispatcher{ self=" + self + ", noLeaderCounter=" + noLeaderCounter + '}';
    }
}
