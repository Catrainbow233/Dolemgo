package net.catrainbow.dolemgo.command.data;


public class CommandParam {
    public static final CommandParam INT;
    public static final CommandParam FLOAT;
    public static final CommandParam VALUE;
    public static final CommandParam WILDCARD_INT;
    public static final CommandParam OPERATOR;
    public static final CommandParam TARGET;
    public static final CommandParam WILDCARD_TARGET;
    public static final CommandParam FILE_PATH;
    public static final CommandParam INT_RANGE;
    public static final CommandParam STRING;
    public static final CommandParam POSITION;
    public static final CommandParam BLOCK_POSITION;
    public static final CommandParam MESSAGE;
    public static final CommandParam TEXT;
    public static final CommandParam JSON;
    public static final CommandParam COMMAND;
    private final CommandParamType paramType;
    private final int defaultValue;

    public CommandParam(CommandParamType paramType) {
        this.paramType = paramType;
        this.defaultValue = -1;
    }

    public CommandParam(int defaultValue) {
        this.defaultValue = defaultValue;
        this.paramType = null;
    }


    public String toString() {
        return "CommandParam(type=" + (this.paramType == null ? "UNKNOWN" : this.paramType.name()) + ", defaultValue=" + this.defaultValue + ")";
    }

    public CommandParam(CommandParamType paramType, int defaultValue) {
        this.paramType = paramType;
        this.defaultValue = defaultValue;
    }

    public CommandParamType getParamType() {
        return this.paramType;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    static {
        INT = new CommandParam(CommandParamType.INT);
        FLOAT = new CommandParam(CommandParamType.FLOAT);
        VALUE = new CommandParam(CommandParamType.VALUE);
        WILDCARD_INT = new CommandParam(CommandParamType.WILDCARD_INT);
        OPERATOR = new CommandParam(CommandParamType.OPERATOR);
        TARGET = new CommandParam(CommandParamType.TARGET);
        WILDCARD_TARGET = new CommandParam(CommandParamType.WILDCARD_TARGET);
        FILE_PATH = new CommandParam(CommandParamType.FILE_PATH);
        INT_RANGE = new CommandParam(CommandParamType.INT_RANGE);
        STRING = new CommandParam(CommandParamType.STRING);
        POSITION = new CommandParam(CommandParamType.POSITION);
        BLOCK_POSITION = new CommandParam(CommandParamType.BLOCK_POSITION);
        MESSAGE = new CommandParam(CommandParamType.MESSAGE);
        TEXT = new CommandParam(CommandParamType.TEXT);
        JSON = new CommandParam(CommandParamType.JSON);
        COMMAND = new CommandParam(CommandParamType.COMMAND);
    }
}
