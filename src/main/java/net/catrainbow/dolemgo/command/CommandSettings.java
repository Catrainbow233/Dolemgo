package net.catrainbow.dolemgo.command;

/**
 * A container holding base information of each command
 */
public class CommandSettings {

    private static final CommandSettings EMPTY_SETTINGS = CommandSettings.builder().build();

    private final String usageMessage;
    private final String description;
    private final boolean quoteAware;

    private final String permission;
    private final String permissionMessage;

    private final String[] aliases;

    private CommandSettings(String usageMessage, String description, String permission, String[] aliases, String permissionMessage, boolean quoteAware) {
        this.usageMessage = usageMessage;
        this.description = description;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.quoteAware = quoteAware;
        this.aliases = aliases;
    }

    private CommandSettings(String usageMessage, String description, String permission, String[] aliases, String permissionMessage){
        this(usageMessage, description, permission, aliases, permissionMessage, false);
    }

    public static CommandSettings empty() {
        return EMPTY_SETTINGS;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsageMessage() {
        return this.usageMessage;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getPermissionMessage() {
        return this.permissionMessage;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public boolean isQuoteAware() {
        return quoteAware;
    }

    public static class Builder {
        private String usageMessage = "";
        private String description = null;
        private String permission = "";
        private String permissionMessage = "dolemgo.command.permission.failed";
        private String[] aliases = new String[0];
        private boolean quoteAware = false;

        public CommandSettings build() {
            return new CommandSettings(
                    this.usageMessage,
                    this.description == null ? this.usageMessage : this.description,
                    this.permission,
                    this.aliases,
                    this.permissionMessage,
                    this.quoteAware
            );
        }

        public String getUsageMessage() {
            return this.usageMessage;
        }

        public Builder setUsageMessage(String usageMessage) {
            this.usageMessage = usageMessage;
            return this;
        }

        public boolean isQuoteAware() {
            return quoteAware;
        }

        public Builder setQuoteAware(boolean quoteAware) {
            this.quoteAware = quoteAware;

            return this;
        }

        public String getDescription() {
            return this.description;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getPermission() {
            return this.permission;
        }

        public Builder setPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public String getPermissionMessage() {
            return this.permissionMessage;
        }

        public Builder setPermissionMessage(String permissionMessage) {
            this.permissionMessage = permissionMessage;
            return this;
        }

        public String[] getAliases() {
            return this.aliases;
        }

        public Builder setAliases(String[] aliases) {
            this.aliases = aliases;
            return this;
        }
    }
}
