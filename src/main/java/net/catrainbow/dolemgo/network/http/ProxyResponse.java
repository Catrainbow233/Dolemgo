package net.catrainbow.dolemgo.network.http;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.listener.ProxyFileListener;
import net.catrainbow.dolemgo.network.ProxySetting;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProxyResponse {
    public final static String CONTENT_TYPE_TEXT = "text/plain";
    /**
     * 返回内容类型：HTML
     */
    public final static String CONTENT_TYPE_HTML = "text/html";
    /**
     * 返回内容类型：XML
     */
    public final static String CONTENT_TYPE_XML = "text/xml";
    /**
     * 返回内容类型：JAVASCRIPT
     */
    public final static String CONTENT_TYPE_JAVASCRIPT = "application/javascript";
    /**
     * 返回内容类型：JSON
     */
    public final static String CONTENT_TYPE_JSON = "application/json";
    public final static String CONTENT_TYPE_JSON_IE = "text/json";

    private ChannelHandlerContext ctx;
    private ProxyRequest request;

    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = CONTENT_TYPE_HTML;
    private String charset = ProxySetting.getCharset();
    private HttpHeaders headers = new DefaultHttpHeaders();
    private Set<Cookie> cookies = new HashSet<Cookie>();
    private Object content = Unpooled.EMPTY_BUFFER;
    // 发送完成标记
    private boolean isSent;

    public ProxyResponse(ChannelHandlerContext ctx, ProxyRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public ProxyResponse setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public ProxyResponse setStatus(HttpResponseStatus status) {
        this.status = status;
        return this;
    }

    public ProxyResponse setStatus(int status) {
        return setStatus(HttpResponseStatus.valueOf(status));
    }

    public ProxyResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ProxyResponse setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public ProxyResponse addHeader(String name, Object value) {
        headers.add(name, value);
        return this;
    }

    public ProxyResponse setHeader(String name, Object value) {
        headers.set(name, value);
        return this;
    }

    public ProxyResponse setContentLength(long contentLength) {
        setHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), contentLength);
        return this;
    }

    public ProxyResponse setKeepAlive() {
        setHeader(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
        return this;
    }

    public ProxyResponse addCookie(Cookie cookie) {
        cookies.add(cookie);
        return this;
    }

    public ProxyResponse addCookie(String name, String value) {
        return addCookie(new io.netty.handler.codec.http.cookie.DefaultCookie(name, value));
    }

    public ProxyResponse addCookie(String name, String value, int maxAgeInSeconds, String path, String domain) {
        Cookie cookie = new io.netty.handler.codec.http.cookie.DefaultCookie(name, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        return addCookie(cookie);
    }

    public ProxyResponse addCookie(String name, String value, int maxAgeInSeconds) {
        return addCookie(name, value, maxAgeInSeconds, "/", null);
    }

    public ProxyResponse setContent(String contentText) {
        this.content = Unpooled.copiedBuffer(contentText, Charset.forName(charset));
        return this;
    }

    public ProxyResponse setTextContent(String contentText) {
        setContentType(CONTENT_TYPE_TEXT);
        return setContent(contentText);
    }

    public ProxyResponse setJsonContent(String contentText) {
        setContentType(request.isIE() ? CONTENT_TYPE_JSON : CONTENT_TYPE_JSON);
        return setContent(contentText);
    }

    public ProxyResponse setXmlContent(String contentText) {
        setContentType(CONTENT_TYPE_XML);
        return setContent(contentText);
    }

    public ProxyResponse setContent(byte[] contentBytes) {
        return setContent(Unpooled.copiedBuffer(contentBytes));
    }

    public ProxyResponse setContent(ByteBuf byteBuf) {
        this.content = byteBuf;
        return this;
    }

    public ProxyResponse setContent(File file) {
        this.content = file;
        return this;
    }

    public void setDateAndCache(long lastModify, int httpCacheSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(DatePattern.HTTP_DATETIME_PATTERN, Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        Calendar time = new GregorianCalendar();
        setHeader(HttpHeaderNames.DATE.toString(), formatter.format(time.getTime()));

        time.add(Calendar.SECOND, httpCacheSeconds);

        setHeader(HttpHeaderNames.EXPIRES.toString(), formatter.format(time.getTime()));
        setHeader(HttpHeaderNames.CACHE_CONTROL.toString(), "private, max-age=" + httpCacheSeconds);
        setHeader(HttpHeaderNames.LAST_MODIFIED.toString(), formatter.format(DateUtil.date(lastModify)));
    }

    private DefaultHttpResponse toDefaultHttpResponse() {
        final DefaultHttpResponse defaultHttpResponse = new DefaultHttpResponse(httpVersion, status);

        fillHeadersAndCookies(defaultHttpResponse.headers());

        return defaultHttpResponse;
    }

    private FullHttpResponse toFullHttpResponse() {
        final ByteBuf byteBuf = (ByteBuf) content;
        final FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpVersion, status, byteBuf);

        final HttpHeaders httpHeaders = fullHttpResponse.headers();
        fillHeadersAndCookies(httpHeaders);
        httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH.toString(), byteBuf.readableBytes());

        return fullHttpResponse;
    }

    private void fillHeadersAndCookies(HttpHeaders httpHeaders) {
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE.toString(), StrUtil.format("{};charset={}", contentType, charset));
        httpHeaders.set(HttpHeaderNames.CONTENT_ENCODING.toString(), charset);

        for (Cookie cookie : cookies) {
            httpHeaders.add(HttpHeaderNames.SET_COOKIE.toString(), ServerCookieEncoder.LAX.encode(cookie));
        }
    }

    public ChannelFuture send() {
        ChannelFuture channelFuture;
        if (content instanceof File) {
            // 文件
            File file = (File) content;
            try {
                channelFuture = sendFile(file);
            } catch (IOException e) {
                Server.getInstance().getLogger().error(StrUtil.format("Send {} error!", file), e);
                channelFuture = sendError(HttpResponseStatus.FORBIDDEN, "");
            }
        } else {
            channelFuture = sendFull();
        }

        this.isSent = true;
        return channelFuture;
    }

    public boolean isSent() {
        return this.isSent;
    }

    private ChannelFuture sendFull() {
        if (request != null && request.isKeepAlive()) {
            setKeepAlive();
            return ctx.writeAndFlush(this.toFullHttpResponse());
        } else {
            return sendAndCloseFull();
        }
    }

    private ChannelFuture sendAndCloseFull() {
        return ctx.writeAndFlush(this.toFullHttpResponse()).addListener(ChannelFutureListener.CLOSE);
    }

    private ChannelFuture sendFile(File file) throws IOException {
        final RandomAccessFile raf = new RandomAccessFile(file, "r");

        long fileLength = raf.length();
        this.setContentLength(fileLength);

        String contentType = HttpUtil.getMimeType(file.getName());
        if (StrUtil.isBlank(contentType)) {
            contentType = "application/octet-stream";
        }
        this.setContentType(contentType);

        ctx.write(this.toDefaultHttpResponse());
        ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise()).addListener(ProxyFileListener.build(raf));

        return sendEmptyLast();
    }

    private ChannelFuture sendEmptyLast() {
        final ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (false == request.isKeepAlive()) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }

        return lastContentFuture;
    }

    public ChannelFuture sendRedirect(String uri) {
        return this.setStatus(HttpResponseStatus.FOUND).setHeader(HttpHeaderNames.LOCATION.toString(), uri).send();
    }

    public ChannelFuture sendNotModified() {
        return this.setStatus(HttpResponseStatus.NOT_MODIFIED).setHeader(HttpHeaderNames.DATE.toString(), DateUtil.formatHttpDate(DateUtil.date())).send();
    }

    public ChannelFuture sendError(HttpResponseStatus status, String msg) {
        if (ctx.channel().isActive()) {
            return this.setStatus(status).setContent(msg).send();
        }
        return null;
    }

    public ChannelFuture sendNotFound(String msg) {
        return sendError(HttpResponseStatus.NOT_FOUND, msg);
    }

    public ChannelFuture sendServerError(String msg) {
        return sendError(HttpResponseStatus.INTERNAL_SERVER_ERROR, msg);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("headers:\r\n ");
        for (Map.Entry<String, String> entry : headers.entries()) {
            sb.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("content: ").append(StrUtil.str(content, CharsetUtil.UTF_8));

        return sb.toString();
    }

    public static ProxyResponse build(ChannelHandlerContext ctx, ProxyRequest request) {
        return new ProxyResponse(ctx, request);
    }

    protected static ProxyResponse build(ChannelHandlerContext ctx) {
        return new ProxyResponse(ctx, null);
    }
}
