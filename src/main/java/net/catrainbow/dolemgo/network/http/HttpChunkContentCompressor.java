package net.catrainbow.dolemgo.network.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;

public class HttpChunkContentCompressor extends HttpContentCompressor {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof FileRegion || msg instanceof DefaultHttpResponse) {
            ctx.write(msg, promise);
        }else{
            super.write(ctx, msg, promise);
        }
    }
}

