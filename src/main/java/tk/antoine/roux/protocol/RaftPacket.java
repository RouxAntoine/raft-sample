package tk.antoine.roux.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static tk.antoine.roux.protocol.MagicHeader.VALID_PEER;

public class RaftPacket {

    private final Verbs verb;
    private final DatagramPacket datagramPacket;
    private MagicHeader magicHeader;
    private final byte[] dataBuffer;

    private RaftPacket(DatagramPacket datagramPacket, byte[] buffer) {
        this.dataBuffer = buffer;
        this.datagramPacket = extractPeerHeader(datagramPacket);
        this.verb = Verbs.fromBytes(buffer);
    }

    private DatagramPacket extractPeerHeader(DatagramPacket datagramPacket) {
        byte[] bufferWithoutHeader = new byte[dataBuffer.length];
        magicHeader = new MagicHeader(dataBuffer[0]);

        if(magicHeader.equals(VALID_PEER)) {
            System.arraycopy(dataBuffer, 1, bufferWithoutHeader, 0, dataBuffer.length-1);
            datagramPacket.setData(bufferWithoutHeader);
        }
        return datagramPacket;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String data() {
        return new String(dataBuffer).trim();
    }

    public DatagramPacket metadata() {
        return datagramPacket;
    }

    public Verbs verb() {
        return verb;
    }

    public MagicHeader magicHeader() {
        return magicHeader;
    }

    public static final class Builder {

        private DatagramPacket datagramPacket;
        private byte[] buffer = new byte[256];

        public DatagramPacket getDatagramPacket() {
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            return datagramPacket;
        }

        public Builder withBuffer(byte[] buffer) {
            this.buffer = new byte[buffer.length + 1];
            System.arraycopy(buffer, 0, this.buffer, 1, buffer.length);
            this.buffer[0] = VALID_PEER.toByte();

            return this;
        }

        public Builder withTarget(InetAddress host, Integer port) {
            datagramPacket = new DatagramPacket(buffer, buffer.length, host, port);
            return this;
        }

        public RaftPacket build() {


            return new RaftPacket(datagramPacket, buffer);
        }
    }
}
