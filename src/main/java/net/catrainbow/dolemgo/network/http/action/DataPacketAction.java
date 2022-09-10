package net.catrainbow.dolemgo.network.http.action;

import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.network.http.ProxyActionInterface;
import net.catrainbow.dolemgo.network.http.ProxyRequest;
import net.catrainbow.dolemgo.network.http.ProxyResponse;
import net.catrainbow.dolemgo.network.protocol.DataPacket;
import net.catrainbow.dolemgo.network.protocol.Network;

public class DataPacketAction implements ProxyActionInterface {
    @Override
    public void doAction(ProxyRequest request, ProxyResponse response) {
        if (request.getParam("id") != null) {
            if (Network.PACKET_MAP.containsValue(Integer.parseInt(request.getParam("id")))) {
                int pid = Integer.parseInt(request.getParam("id"));
                DataPacket dataPacket = new DataPacket(pid);
                dataPacket.key = request.getParam("key");
                dataPacket.putString(request.getParam("packet"));
                Server.getInstance().networkInterface.handlePacket(dataPacket, response);
            }
        }
    }
}
