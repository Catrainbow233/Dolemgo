package net.catrainbow.dolemgo.listener;

import cn.hutool.core.io.IoUtil;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import net.catrainbow.dolemgo.Server;

import java.io.RandomAccessFile;

public class ProxyFileListener implements ChannelProgressiveFutureListener {

    private RandomAccessFile raf;

    public ProxyFileListener(RandomAccessFile raf) {
        this.raf = raf;
    }

    @Override
    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
        Server.getInstance().getLogger().debug("Transfer progress: {} / {}", progress, total);
    }

    @Override
    public void operationComplete(ChannelProgressiveFuture future) {
        IoUtil.close(raf);
        Server.getInstance().getLogger().debug("Transfer complete.");
    }

    public static ProxyFileListener build(RandomAccessFile raf) {
        return new ProxyFileListener(raf);
    }

}
