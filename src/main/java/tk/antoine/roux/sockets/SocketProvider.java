package tk.antoine.roux.sockets;

import tk.antoine.roux.node.PeerDefinition;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class SocketProvider {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final InetAddress address;
    private final Integer port;
    private final DatagramSocket datagramSocket;

    public SocketProvider(InetAddress hostname, Integer portNumber) throws SocketException {
        port = portNumber;
        address = hostname;
        datagramSocket = create();
    }

    private DatagramSocket create() throws SocketException {
        try {
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            return new DatagramSocket(socketAddress);
        } catch (SocketException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        }
    }

    public DatagramPacket receive(byte[] buffer) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(datagramPacket);
        return datagramPacket;
    }

    public void send(byte[] buffer, PeerDefinition remote) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, remote.ip, remote.port);
        datagramSocket.send(datagramPacket);
    }
}
