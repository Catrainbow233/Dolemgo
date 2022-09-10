package net.catrainbow.dolemgo.command.data;


import java.util.Arrays;
import lombok.NonNull;

public final class CommandEnumData {
    private final @NonNull String name;
    private final @NonNull String[] values;
    private final boolean isSoft;

    public CommandEnumData(@NonNull String name, @NonNull String[] values, boolean isSoft) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else if (values == null) {
            throw new NullPointerException("values is marked non-null but is null");
        } else {
            this.name = name;
            this.values = values;
            this.isSoft = isSoft;
        }
    }

    public @NonNull String getName() {
        return this.name;
    }

    public @NonNull String[] getValues() {
        return this.values;
    }

    public boolean isSoft() {
        return this.isSoft;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CommandEnumData)) {
            return false;
        } else {
            CommandEnumData other;
            label28: {
                other = (CommandEnumData)o;
                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name == null) {
                        break label28;
                    }
                } else if (this$name.equals(other$name)) {
                    break label28;
                }

                return false;
            }

            if (!Arrays.deepEquals(this.getValues(), other.getValues())) {
                return false;
            } else {
                return this.isSoft() == other.isSoft();
            }
        }
    }

    public int hashCode() {
        int result = 1;
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getValues());
        result = result * 59 + (this.isSoft() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "CommandEnumData(name=" + this.getName() + ", values=" + Arrays.deepToString(this.getValues()) + ", isSoft=" + this.isSoft() + ")";
    }
}

