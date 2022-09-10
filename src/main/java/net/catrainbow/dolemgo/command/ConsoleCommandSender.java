package net.catrainbow.dolemgo.command;

import net.catrainbow.dolemgo.Server;

public class ConsoleCommandSender implements CommandSender {

    private final Server proxy;

    public ConsoleCommandSender(Server proxy) {
        this.proxy = proxy;
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void sendMessage(String message) {
        for (String line : message.trim().split("\n")) {
            this.proxy.getLogger().info(line);
        }
    }

    @Override
    public Server getProxy() {
        return this.proxy;
    }
}
