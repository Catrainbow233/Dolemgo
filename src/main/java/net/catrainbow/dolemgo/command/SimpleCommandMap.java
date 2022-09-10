package net.catrainbow.dolemgo.command;

import net.catrainbow.dolemgo.Server;

import java.util.HashMap;

public class SimpleCommandMap implements CommandMap {

    public static final String DEFAULT_PREFIX = "/";

    private final Server proxy;
    private final String commandPrefix;

    private final HashMap<String, Command> commandsMap = new HashMap<>();
    private final HashMap<String, Command> aliasesMap = new HashMap<>();

    public SimpleCommandMap(Server proxy, String prefix) {
        this.proxy = proxy;
        this.commandPrefix = prefix;
    }

    @Override
    @Deprecated
    public boolean registerCommand(String name, Command command) {
        if (this.commandsMap.putIfAbsent(name.toLowerCase(), command) != null) {
            return false;
        }
        for (String alias : command.getAliases()) {
            this.registerAlias(alias, command);
        }
        return true;
    }

    @Override
    public boolean registerCommand(Command command) {
        if (this.commandsMap.putIfAbsent(command.getName().toLowerCase(), command) != null) {
            return false;
        }
        for (String alias : command.getAliases()) {
            this.registerAlias(alias, command);
        }
        return true;
    }

    @Override
    public boolean registerAlias(String name, Command command) {
        return this.aliasesMap.putIfAbsent(name.toLowerCase(), command) == null;
    }

    @Override
    public boolean unregisterCommand(String name) {
        Command command = this.commandsMap.remove(name.toLowerCase());
        if (command == null) return false;

        for (String alias : command.getAliases()) {
            this.aliasesMap.remove(alias.toLowerCase());
        }
        return true;
    }

    @Override
    public Command getCommand(String name) {
        Command result = this.commandsMap.get(name.toLowerCase());

        if(result == null){
            result = this.aliasesMap.get(name.toLowerCase());
        }

        return result;
    }

    @Override
    public boolean isRegistered(String name) {
        return this.commandsMap.containsKey(name.toLowerCase());
    }

    @Override
    public boolean handleMessage(CommandSender sender, String message) {
        return !message.trim().isEmpty() && message.startsWith(this.commandPrefix);
    }

    @Override
    public boolean handleCommand(CommandSender sender, String commandName, String[] args) {
        Command command = this.commandsMap.get(commandName.toLowerCase());
        if (command != null) {
            this.execute(command, sender, null, args);
            return true;
        }

        Command aliasCommand = this.aliasesMap.get(commandName.toLowerCase());
        if (aliasCommand != null) {
            this.execute(aliasCommand, sender, commandName, args);
            return true;
        }

        if (!sender.isPlayer()) { // Player commands may be handled by servers
            sender.sendMessage("Unknown command");
        }
        return false;
    }

    private void execute(Command command, CommandSender sender, String alias, String[] args) {
        boolean permission = sender.hasPermission(command.getPermission());
        if (!permission) {
            sender.sendMessage("You don't have premission to use this command.");
            return;
        }

        try {
            boolean success = command.onExecute(sender, alias, args);
            if (!success) {
                sender.sendMessage("§cCommand usage: " + command.getUsageMessage());
            }
        } catch (Exception e) {
            this.proxy.getLogger().error("Error appeared while processing command!", e);
        }
    }

    @Override
    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    @Override
    public HashMap<String, Command> getCommands() {
        return this.commandsMap;
    }
}
