package net.catrainbow.dolemgo.network.http.action.common;

import net.catrainbow.dolemgo.Dolemgo;
import net.catrainbow.dolemgo.network.http.ProxyActionInterface;
import net.catrainbow.dolemgo.network.http.ProxyRequest;
import net.catrainbow.dolemgo.network.http.ProxyResponse;

public class DefaultAction implements ProxyActionInterface {
    @Override
    public void doAction(ProxyRequest request, ProxyResponse response) {
        response.setContent("Dolemgo Server " + Dolemgo.VERSION);
    }
}
