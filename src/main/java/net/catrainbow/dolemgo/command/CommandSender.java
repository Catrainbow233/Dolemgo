package net.catrainbow.dolemgo.command;

import net.catrainbow.dolemgo.Server;

/**
 * Base interface for all instances that are able to issue commands.
 */
public interface CommandSender {

    String getName();

    boolean isPlayer();

    boolean hasPermission(String permission);

    void sendMessage(String message);

    Server getProxy();
}
