package net.catrainbow.dolemgo.command.data;
public enum CommandParamType {
    INT,
    FLOAT,
    VALUE,
    WILDCARD_INT,
    OPERATOR,
    TARGET,
    WILDCARD_TARGET,
    FILE_PATH,
    INT_RANGE,
    STRING,
    POSITION,
    BLOCK_POSITION,
    MESSAGE,
    TEXT,
    JSON,
    COMMAND;

    private CommandParamType() {
    }
}