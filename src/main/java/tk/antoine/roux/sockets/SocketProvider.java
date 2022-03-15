package tk.antoine.roux.sockets;

import java.lang.invoke.MethodHandles;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketProvider {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final InetAddress address;
    private final Integer port;

    public SocketProvider(InetAddress hostname, Integer portNumber) {
        port = portNumber;
        address = hostname;
    }

    public DatagramSocket create() throws SocketException {
        try {
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            return new DatagramSocket(socketAddress);
        } catch (SocketException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        }
    }
}
