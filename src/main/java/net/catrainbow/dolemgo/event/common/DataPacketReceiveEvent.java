package net.catrainbow.dolemgo.event.common;

import net.catrainbow.dolemgo.event.CancellableEvent;
import net.catrainbow.dolemgo.event.Event;
import net.catrainbow.dolemgo.network.http.ProxyResponse;
import net.catrainbow.dolemgo.network.protocol.DataPacket;

public class DataPacketReceiveEvent extends Event implements CancellableEvent {

    private final DataPacket dataPacket;
    public final ProxyResponse resend;

    public DataPacketReceiveEvent(DataPacket dataPacket, ProxyResponse response) {
        this.dataPacket = dataPacket;
        this.resend = response;
    }

    public DataPacket getDataPacket() {
        return dataPacket;
    }

}
