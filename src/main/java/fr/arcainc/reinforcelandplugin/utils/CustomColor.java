package fr.arcainc.reinforcelandplugin.utils;

public enum CustomColor {
    WHITE((byte) 0),
    ORANGE((byte) 1),
    MAGENTA((byte) 2),
    LIGHT_BLUE((byte) 3),
    YELLOW((byte) 4),
    LIME((byte) 5),
    PINK((byte) 6),
    GRAY((byte) 7),
    SILVER((byte) 8),
    CYAN((byte) 9),
    PURPLE((byte) 10),
    BLUE((byte) 11),
    BROWN((byte) 12),
    GREEN((byte) 13),
    RED((byte) 14),
    BLACK((byte) 15);

    private final byte dataValue;

    CustomColor(byte dataValue) {
        this.dataValue = dataValue;
    }

    public byte getDataValue() {
        return dataValue;
    }
}
