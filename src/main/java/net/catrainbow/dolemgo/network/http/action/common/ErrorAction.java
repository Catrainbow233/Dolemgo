package net.catrainbow.dolemgo.network.http.action.common;

import cn.hutool.core.util.StrUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.network.http.ProxyActionInterface;
import net.catrainbow.dolemgo.network.http.ProxyRequest;
import net.catrainbow.dolemgo.network.http.ProxyResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorAction implements ProxyActionInterface {

    public final static String ERROR_PARAM_NAME = "_e";

    private final static String TEMPLATE_ERROR = "<!DOCTYPE html><html><head><title>Dolemgo - Error report</title><style>h1,h3 {color:white; background-color: gray;}</style></head><body><h1>HTTP Status {} - {}</h1><hr size=\"1\" noshade=\"noshade\" /><p>{}</p><hr size=\"1\" noshade=\"noshade\" /><h3>Dolemgo Server</h3></body></html>";

    @Override
    public void doAction(ProxyRequest request, ProxyResponse response) {
        Object eObj = request.getObjParam(ERROR_PARAM_NAME);
        if (eObj == null) {
            response.sendError(HttpResponseStatus.NOT_FOUND, "404 File not found!");
            return;
        }

        if (eObj instanceof Exception) {
            Exception e = (Exception) eObj;
            Server.getInstance().getLogger().error("Server action internal error!", e);
            final StringWriter writer = new StringWriter();
            // 把错误堆栈储存到流中
            e.printStackTrace(new PrintWriter(writer));
            String content = writer.toString().replace("\tat", "&nbsp;&nbsp;&nbsp;&nbsp;\tat");
            content = content.replace("\n", "<br/>\n");
            content = StrUtil.format(TEMPLATE_ERROR, 500, request.getUri(), content);

            response.sendServerError(content);
        }
    }
}
