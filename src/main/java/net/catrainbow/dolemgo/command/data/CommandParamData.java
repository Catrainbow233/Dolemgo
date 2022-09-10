package net.catrainbow.dolemgo.command.data;


import java.util.List;
import lombok.NonNull;

public final class CommandParamData {
    private final @NonNull String name;
    private final boolean optional;
    private final CommandEnumData enumData;
    private final CommandParam type;
    private final String postfix;
    private final List<CommandParamOption> options;

    public CommandParamData(@NonNull String name, boolean optional, CommandEnumData enumData, CommandParam type, String postfix, List<CommandParamOption> options) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            this.name = name;
            this.optional = optional;
            this.enumData = enumData;
            this.type = type;
            this.postfix = postfix;
            this.options = options;
        }
    }

    public @NonNull String getName() {
        return this.name;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public CommandEnumData getEnumData() {
        return this.enumData;
    }

    public CommandParam getType() {
        return this.type;
    }

    public String getPostfix() {
        return this.postfix;
    }

    public List<CommandParamOption> getOptions() {
        return this.options;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CommandParamData)) {
            return false;
        } else {
            CommandParamData other;
            label72: {
                other = (CommandParamData)o;
                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name == null) {
                        break label72;
                    }
                } else if (this$name.equals(other$name)) {
                    break label72;
                }

                return false;
            }

            if (this.isOptional() != other.isOptional()) {
                return false;
            } else {
                Object this$enumData = this.getEnumData();
                Object other$enumData = other.getEnumData();
                if (this$enumData == null) {
                    if (other$enumData != null) {
                        return false;
                    }
                } else if (!this$enumData.equals(other$enumData)) {
                    return false;
                }

                label57: {
                    Object this$type = this.getType();
                    Object other$type = other.getType();
                    if (this$type == null) {
                        if (other$type == null) {
                            break label57;
                        }
                    } else if (this$type.equals(other$type)) {
                        break label57;
                    }

                    return false;
                }

                Object this$postfix = this.getPostfix();
                Object other$postfix = other.getPostfix();
                if (this$postfix == null) {
                    if (other$postfix != null) {
                        return false;
                    }
                } else if (!this$postfix.equals(other$postfix)) {
                    return false;
                }

                Object this$options = this.getOptions();
                Object other$options = other.getOptions();
                if (this$options == null) {
                    if (other$options == null) {
                        return true;
                    }
                } else if (this$options.equals(other$options)) {
                    return true;
                }

                return false;
            }
        }
    }

    public int hashCode() {
        int result = 1;
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        result = result * 59 + (this.isOptional() ? 79 : 97);
        Object $enumData = this.getEnumData();
        result = result * 59 + ($enumData == null ? 43 : $enumData.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $postfix = this.getPostfix();
        result = result * 59 + ($postfix == null ? 43 : $postfix.hashCode());
        Object $options = this.getOptions();
        result = result * 59 + ($options == null ? 43 : $options.hashCode());
        return result;
    }

    public String toString() {
        return "CommandParamData(name=" + this.getName() + ", optional=" + this.isOptional() + ", enumData=" + this.getEnumData() + ", type=" + this.getType() + ", postfix=" + this.getPostfix() + ", options=" + this.getOptions() + ")";
    }

    public static final class Builder {
        private final String name;
        private final CommandSymbolData type;
        private final boolean optional;
        private final byte options;

        /** @deprecated */
        @Deprecated
        public Builder(String name, CommandSymbolData type, boolean optional) {
            this(name, type, optional, (byte)0);
        }

        public String getName() {
            return this.name;
        }

        public CommandSymbolData getType() {
            return this.type;
        }

        public boolean isOptional() {
            return this.optional;
        }

        public byte getOptions() {
            return this.options;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof CommandParamData.Builder)) {
                return false;
            } else {
                CommandParamData.Builder other;
                label40: {
                    other = (CommandParamData.Builder)o;
                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null) {
                        if (other$name == null) {
                            break label40;
                        }
                    } else if (this$name.equals(other$name)) {
                        break label40;
                    }

                    return false;
                }

                label33: {
                    Object this$type = this.getType();
                    Object other$type = other.getType();
                    if (this$type == null) {
                        if (other$type == null) {
                            break label33;
                        }
                    } else if (this$type.equals(other$type)) {
                        break label33;
                    }

                    return false;
                }

                if (this.isOptional() != other.isOptional()) {
                    return false;
                } else {
                    return this.getOptions() == other.getOptions();
                }
            }
        }

        public int hashCode() {
            int result = 1;
            Object $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            Object $type = this.getType();
            result = result * 59 + ($type == null ? 43 : $type.hashCode());
            result = result * 59 + (this.isOptional() ? 79 : 97);
            result = result * 59 + this.getOptions();
            return result;
        }

        public String toString() {
            return "CommandParamData.Builder(name=" + this.getName() + ", type=" + this.getType() + ", optional=" + this.isOptional() + ", options=" + this.getOptions() + ")";
        }

        public Builder(String name, CommandSymbolData type, boolean optional, byte options) {
            this.name = name;
            this.type = type;
            this.optional = optional;
            this.options = options;
        }
    }
}
