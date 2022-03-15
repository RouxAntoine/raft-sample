package tk.antoine.roux.protocol;

public enum Verbs {
    EXIT,
    LEADER_EXIST,
    I_AM_LEADER,
    I_AM_CANDIDATE,
    AM_I_LEADER,
    NO,
    YES,
    INFO,
    HEALTH,
    HEALTHY,
    NONE
    ;

    public static Verbs fromBytes(byte[] data) {
        try {
            return Verbs.valueOf(new String(data).trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }

    public byte[] getBytes() {
        return name().getBytes();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
