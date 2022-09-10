package net.catrainbow.dolemgo.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.network.http.action.DataPacketAction;
import net.catrainbow.dolemgo.network.http.action.common.DefaultAction;
import net.catrainbow.dolemgo.network.http.handler.ProxyActionHandler;
import net.catrainbow.dolemgo.scheduler.AsyncTask;

public class RaknetInterface {

    private Server proxy;

    public RaknetInterface(Server proxy) {
        this.proxy = proxy;
    }

    public void start() {
        try {
            //数据包监听
            ProxySetting.setAction("/handler", DataPacketAction.class);
            ProxySetting.setPort(this.proxy.port);
            this.start(this.proxy);
        } catch (InterruptedException e) {
            this.proxy.getLogger().error(e);
        }
    }

    protected void start(Server proxy) throws InterruptedException {
        this.proxy.getScheduler().scheduleAsyncTask(new AsyncTask() {
            @Override
            public void onRun() {
                int port = ProxySetting.getPort();

                final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                final EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    final ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .channel(NioServerSocketChannel.class)
//
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline()
                                            .addLast(new HttpServerCodec())
                                            .addLast(new HttpObjectAggregator(65536))
                                            .addLast(new ChunkedWriteHandler())
                                            .addLast(new ProxyActionHandler());
                                }
                            });

                    final Channel ch = b.bind(port).sync().channel();
                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
