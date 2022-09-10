package net.catrainbow.dolemgo;

import net.catrainbow.dolemgo.command.CommandSender;

public class Player implements CommandSender {

    public String name;
    public String clientName;

    public Player(String playerName, String clientName) {
        this.name = playerName;
        this.clientName = clientName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public Server getProxy() {
        return Server.getInstance();
    }
}
