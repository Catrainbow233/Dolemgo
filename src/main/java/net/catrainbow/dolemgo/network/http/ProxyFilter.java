package net.catrainbow.dolemgo.network.http;

public interface ProxyFilter {

    public boolean doFilter(ProxyRequest request, ProxyResponse response);

}
