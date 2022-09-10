package net.catrainbow.dolemgo.network.protocol;

import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.network.http.ProxyResponse;
import net.catrainbow.dolemgo.utils.AES;
import net.catrainbow.dolemgo.utils.RSA;

import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;

public class Network {

    public static HashMap<String, Integer> PACKET_MAP = new HashMap<>();

    static {
        PACKET_MAP.put("BATCH_PACKET", 0);
    }

    public void handlePacket(DataPacket packet, ProxyResponse response) {
        try {
            String original = packet.toString();
            RSAPrivateKey key = RSA.getPrivateKey(Server.getInstance().privateKey);
            String keyV2 = RSA.decryptByPrivateKey(packet.key, key);
            original = new String(AES.decrypt(original.getBytes(), keyV2.getBytes()));
            packet.clear();
            packet.putString(original);

        } catch (Exception e) {
            Server.getInstance().getLogger().error("Error while handle packet:\nID=" + packet.id, e);
        }
    }

}
