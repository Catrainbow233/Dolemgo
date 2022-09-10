package net.catrainbow.dolemgo.command.data;


import com.google.common.base.Preconditions;

public final class CommandSymbolData {
    private static final int ARG_FLAG_VALID = 1048576;
    private static final int ARG_FLAG_ENUM = 2097152;
    private static final int ARG_FLAG_POSTFIX = 16777216;
    private static final int ARG_FLAG_SOFT_ENUM = 67108864;
    private final int value;
    private final boolean commandEnum;
    private final boolean softEnum;
    private final boolean postfix;

    public static CommandSymbolData deserialize(int type) {
        int value = type & '\uffff';
        boolean commandEnum = (type & 2097152) != 0;
        boolean softEnum = (type & 67108864) != 0;
        boolean postfix = (type & 16777216) != 0;
        Preconditions.checkState(postfix || (type & 1048576) != 0, "Invalid command param type: " + type);
        return new CommandSymbolData(value, commandEnum, softEnum, postfix);
    }

    public int serialize() {
        int value = this.value;
        if (this.commandEnum) {
            value |= 2097152;
        }

        if (this.softEnum) {
            value |= 67108864;
        }

        if (this.postfix) {
            value |= 16777216;
        } else {
            value |= 1048576;
        }

        return value;
    }

    public CommandSymbolData(int value, boolean commandEnum, boolean softEnum, boolean postfix) {
        this.value = value;
        this.commandEnum = commandEnum;
        this.softEnum = softEnum;
        this.postfix = postfix;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isCommandEnum() {
        return this.commandEnum;
    }

    public boolean isSoftEnum() {
        return this.softEnum;
    }

    public boolean isPostfix() {
        return this.postfix;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CommandSymbolData)) {
            return false;
        } else {
            CommandSymbolData other = (CommandSymbolData)o;
            if (this.getValue() != other.getValue()) {
                return false;
            } else if (this.isCommandEnum() != other.isCommandEnum()) {
                return false;
            } else if (this.isSoftEnum() != other.isSoftEnum()) {
                return false;
            } else {
                return this.isPostfix() == other.isPostfix();
            }
        }
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getValue();
        result = result * 59 + (this.isCommandEnum() ? 79 : 97);
        result = result * 59 + (this.isSoftEnum() ? 79 : 97);
        result = result * 59 + (this.isPostfix() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "CommandSymbolData(value=" + this.getValue() + ", commandEnum=" + this.isCommandEnum() + ", softEnum=" + this.isSoftEnum() + ", postfix=" + this.isPostfix() + ")";
    }
}
