package net.catrainbow.dolemgo.event.common;

import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.event.Event;

public class ServerStartEvent extends Event {

    private final Server proxy;

    public ServerStartEvent(Server proxy){
        this.proxy = proxy;
    }

    public Server getProxy() {
        return proxy;
    }
}
