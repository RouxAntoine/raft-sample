package tk.antoine.roux.protocol;

public class MagicHeader {

    public static final MagicHeader VALID_PEER = new MagicHeader(0x1e);

    private final byte header;

    MagicHeader(int i) {
        header = (byte) i;
    }
    MagicHeader(byte b) {
        header = b;
    }

    public byte toByte() {
        return header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MagicHeader that = (MagicHeader) o;
        return header == that.header;
    }
}
