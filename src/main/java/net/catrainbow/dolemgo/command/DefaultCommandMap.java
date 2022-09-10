package net.catrainbow.dolemgo.command;

import net.catrainbow.dolemgo.Server;

public class DefaultCommandMap extends SimpleCommandMap {
    public DefaultCommandMap(Server proxy, String prefix) {
        super(proxy, prefix);
        this.registerDefaults();
    }

    public void registerDefaults() {

    }
}
