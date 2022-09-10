package net.catrainbow.dolemgo.command.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class CommandData {
    private final String name;
    private final String description;
    private final List<CommandData.Flag> flags;
    private final int permission;
    private final CommandEnumData aliases;
    private final CommandParamData[][] overloads;

    public String toString() {
        StringBuilder overloads = new StringBuilder("[\r\n");
        CommandParamData[][] var2 = this.overloads;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            CommandParamData[] overload = var2[var4];
            overloads.append("    [\r\n");
            CommandParamData[] var6 = overload;
            int var7 = overload.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                CommandParamData parameter = var6[var8];
                overloads.append("       ").append(parameter).append("\r\n");
            }

            overloads.append("    ]\r\n");
        }

        overloads.append("]\r\n");
        StringBuilder builder = new StringBuilder("CommandData(\r\n");
        List<?> objects = Arrays.asList("name=" + this.name, "description=" + this.description, "flags=" + Arrays.toString(this.flags.toArray()), "permission=" + this.permission, "aliases=" + this.aliases, "overloads=" + overloads);
        Iterator var12 = objects.iterator();

        while(var12.hasNext()) {
            Object object = var12.next();
            builder.append("    ").append(Objects.toString(object).replaceAll("\r\n", "\r\n    ")).append("\r\n");
        }

        return builder.append(")").toString();
    }

    public CommandData(String name, String description, List<CommandData.Flag> flags, int permission, CommandEnumData aliases, CommandParamData[][] overloads) {
        this.name = name;
        this.description = description;
        this.flags = flags;
        this.permission = permission;
        this.aliases = aliases;
        this.overloads = overloads;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<CommandData.Flag> getFlags() {
        return this.flags;
    }

    public int getPermission() {
        return this.permission;
    }

    public CommandEnumData getAliases() {
        return this.aliases;
    }

    public CommandParamData[][] getOverloads() {
        return this.overloads;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CommandData)) {
            return false;
        } else {
            CommandData other = (CommandData)o;
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
                if (other$name != null) {
                    return false;
                }
            } else if (!this$name.equals(other$name)) {
                return false;
            }

            label57: {
                Object this$description = this.getDescription();
                Object other$description = other.getDescription();
                if (this$description == null) {
                    if (other$description == null) {
                        break label57;
                    }
                } else if (this$description.equals(other$description)) {
                    break label57;
                }

                return false;
            }

            Object this$flags = this.getFlags();
            Object other$flags = other.getFlags();
            if (this$flags == null) {
                if (other$flags != null) {
                    return false;
                }
            } else if (!this$flags.equals(other$flags)) {
                return false;
            }

            if (this.getPermission() != other.getPermission()) {
                return false;
            } else {
                label42: {
                    Object this$aliases = this.getAliases();
                    Object other$aliases = other.getAliases();
                    if (this$aliases == null) {
                        if (other$aliases == null) {
                            break label42;
                        }
                    } else if (this$aliases.equals(other$aliases)) {
                        break label42;
                    }

                    return false;
                }

                if (!Arrays.deepEquals(this.getOverloads(), other.getOverloads())) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public int hashCode() {
        int result = 1;
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Object $flags = this.getFlags();
        result = result * 59 + ($flags == null ? 43 : $flags.hashCode());
        result = result * 59 + this.getPermission();
        Object $aliases = this.getAliases();
        result = result * 59 + ($aliases == null ? 43 : $aliases.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getOverloads());
        return result;
    }

    public static enum Flag {
        USAGE,
        VISIBILITY,
        SYNC,
        EXECUTE,
        TYPE,
        CHEAT,
        UNKNOWN_6;

        private Flag() {
        }
    }

    public static final class Builder {
        private final String name;
        private final String description;
        private final int flags;
        private final int permission;
        private final int aliases;
        private final CommandParamData.Builder[][] overloads;

        public Builder(String name, String description, int flags, int permission, int aliases, CommandParamData.Builder[][] overloads) {
            this.name = name;
            this.description = description;
            this.flags = flags;
            this.permission = permission;
            this.aliases = aliases;
            this.overloads = overloads;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public int getFlags() {
            return this.flags;
        }

        public int getPermission() {
            return this.permission;
        }

        public int getAliases() {
            return this.aliases;
        }

        public CommandParamData.Builder[][] getOverloads() {
            return this.overloads;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof CommandData.Builder)) {
                return false;
            } else {
                CommandData.Builder other = (CommandData.Builder)o;
                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                label41: {
                    Object this$description = this.getDescription();
                    Object other$description = other.getDescription();
                    if (this$description == null) {
                        if (other$description == null) {
                            break label41;
                        }
                    } else if (this$description.equals(other$description)) {
                        break label41;
                    }

                    return false;
                }

                if (this.getFlags() != other.getFlags()) {
                    return false;
                } else if (this.getPermission() != other.getPermission()) {
                    return false;
                } else if (this.getAliases() != other.getAliases()) {
                    return false;
                } else if (!Arrays.deepEquals(this.getOverloads(), other.getOverloads())) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        public int hashCode() {
            int result = 1;
            Object $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            Object $description = this.getDescription();
            result = result * 59 + ($description == null ? 43 : $description.hashCode());
            result = result * 59 + this.getFlags();
            result = result * 59 + this.getPermission();
            result = result * 59 + this.getAliases();
            result = result * 59 + Arrays.deepHashCode(this.getOverloads());
            return result;
        }

        public String toString() {
            return "CommandData.Builder(name=" + this.getName() + ", description=" + this.getDescription() + ", flags=" + this.getFlags() + ", permission=" + this.getPermission() + ", aliases=" + this.getAliases() + ", overloads=" + Arrays.deepToString(this.getOverloads()) + ")";
        }
    }
}

