package net.catrainbow.dolemgo.network.http.handler;

import cn.hutool.core.lang.Singleton;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.network.ProxySetting;
import net.catrainbow.dolemgo.network.http.ProxyActionInterface;
import net.catrainbow.dolemgo.network.http.ProxyFilter;
import net.catrainbow.dolemgo.network.http.ProxyRequest;
import net.catrainbow.dolemgo.network.http.ProxyResponse;
import net.catrainbow.dolemgo.network.http.action.common.ErrorAction;
import net.catrainbow.dolemgo.network.http.action.common.FileAction;

import java.io.IOException;

public class ProxyActionHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {

        final ProxyRequest request = ProxyRequest.build(ctx, fullHttpRequest);
        final ProxyResponse response = ProxyResponse.build(ctx, request);

        try {
            boolean isPass = this.doProxyFilter(request, response);

            if(isPass){
                this.doAction(request, response);
            }
        } catch (Exception e) {
            ProxyActionInterface errorAction = ProxySetting.getAction(ProxySetting.MAPPING_ERROR);
            request.putParam(ErrorAction.ERROR_PARAM_NAME, e);
            errorAction.doAction(request, response);
        }

        if(false ==response.isSent()){
            response.send();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof IOException){
            Server.getInstance().getLogger().error(cause);
        }else{
            super.exceptionCaught(ctx, cause);
        }
    }

    private boolean doProxyFilter(ProxyRequest request, ProxyResponse response) {
        ProxyFilter filter = ProxySetting.getProxyFilter(ProxySetting.MAPPING_ALL);
        if(null != filter){
            if(false == filter.doFilter(request, response)){
                return false;
            }
        }

        filter = ProxySetting.getProxyFilter(request.getPath());
        if(null != filter){
            if(false == filter.doFilter(request, response)){
                return false;
            }
        }

        return true;
    }

    private void doAction(ProxyRequest request, ProxyResponse response){
        ProxyActionInterface action = ProxySetting.getAction(request.getPath());
        if (null == action) {
            action = ProxySetting.getAction(ProxySetting.MAPPING_ALL);
            if(null == action){
                action = Singleton.get(FileAction.class);
            }
        }

        action.doAction(request, response);
    }

}
