package net.catrainbow.dolemgo.network.http;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import net.catrainbow.dolemgo.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.*;

public class ProxyRequest {

    public static final String METHOD_DELETE = HttpMethod.DELETE.name();
    public static final String METHOD_HEAD = HttpMethod.HEAD.name();
    public static final String METHOD_GET = HttpMethod.GET.name();
    public static final String METHOD_OPTIONS = HttpMethod.OPTIONS.name();
    public static final String METHOD_POST = HttpMethod.POST.name();
    public static final String METHOD_PUT = HttpMethod.PUT.name();
    public static final String METHOD_TRACE = HttpMethod.TRACE.name();

    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private FullHttpRequest nettyRequest;

    private String path;
    private String ip;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, Object> params = new HashMap<String, Object>();
    private Map<String, Cookie> cookies = new HashMap<String, Cookie>();

    private ProxyRequest(ChannelHandlerContext ctx, FullHttpRequest nettyRequest) {
        this.nettyRequest = nettyRequest;
        final String uri = nettyRequest.uri();
        this.path = URLUtil.getPath(getUri());

        this.putHeadersAndCookies(nettyRequest.headers());

        this.putParams(new QueryStringDecoder(uri));
        if (nettyRequest.method() != HttpMethod.GET && !"application/octet-stream".equals(nettyRequest.headers().get("Content-Type"))) {
            HttpPostRequestDecoder decoder = null;
            try {
                decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, nettyRequest);
                this.putParams(decoder);
            } finally {
                if (null != decoder) {
                    decoder.destroy();
                    decoder = null;
                }
            }
        }

        this.putIp(ctx);
    }

    public HttpRequest getNettyRequest() {
        return this.nettyRequest;
    }

    public String getProtocolVersion() {
        return nettyRequest.protocolVersion().text();
    }

    public String getUri() {
        return nettyRequest.uri();
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return nettyRequest.method().name();
    }

    public String getIp() {
        return ip;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String headerKey) {
        return headers.get(headerKey);
    }

    public boolean isXWwwFormUrlencoded() {
        return "application/x-www-form-urlencoded".equals(getHeader("Content-Type"));
    }

    public Cookie getCookie(String name) {
        return cookies.get(name);
    }

    public Map<String, Cookie> getCookies() {
        return this.cookies;
    }

    public boolean isIE() {
        String userAgent = getHeader("User-Agent");
        if (StrUtil.isNotBlank(userAgent)) {
            userAgent = userAgent.toUpperCase();
            if (userAgent.contains("MSIE") || userAgent.contains("TRIDENT")) {
                return true;
            }
        }
        return false;
    }

    public String getParam(String name) {
        final Object value = params.get(name);
        if (null == value) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    public Object getObjParam(String name) {
        return params.get(name);
    }

    public String getParam(String name, Charset charset) {
        if (null == charset) {
            charset = Charset.forName(CharsetUtil.ISO_8859_1);
        }

        String destCharset = CharsetUtil.UTF_8;
        if (isIE()) {
            // IE浏览器GET请求使用GBK编码
            destCharset = CharsetUtil.GBK;
        }

        String value = getParam(name);
        if (METHOD_GET.equalsIgnoreCase(getMethod())) {
            value = CharsetUtil.convert(value, charset.toString(), destCharset);
        }
        return value;
    }

    public String getParam(String name, String defaultValue) {
        String param = getParam(name);
        return StrUtil.isBlank(param) ? defaultValue : param;
    }

    public Integer getIntParam(String name, Integer defaultValue) {
        return Convert.toInt(getParam(name), defaultValue);
    }

    public Long getLongParam(String name, Long defaultValue) {
        return Convert.toLong(getParam(name), defaultValue);
    }

    public Double getDoubleParam(String name, Double defaultValue) {
        return Convert.toDouble(getParam(name), defaultValue);
    }

    public Float getFloatParam(String name, Float defaultValue) {
        return Convert.toFloat(getParam(name), defaultValue);
    }

    public Boolean getBoolParam(String name, Boolean defaultValue) {
        return Convert.toBool(getParam(name), defaultValue);
    }

    public Date getDateParam(String name, Date defaultValue) {
        String param = getParam(name);
        return StrUtil.isBlank(param) ? defaultValue : DateUtil.parse(param);
    }

    public Date getDateParam(String name, String format, Date defaultValue) {
        String param = getParam(name);
        return StrUtil.isBlank(param) ? defaultValue : DateUtil.parse(param, format);
    }

    @SuppressWarnings("unchecked")
    public List<String> getArrayParam(String name) {
        Object value = params.get(name);
        if (null == value) {
            return null;
        }

        if (value instanceof List) {
            return (List<String>) value;
        } else if (value instanceof String) {
            return StrUtil.split((String) value, ',');
        } else {
            throw new RuntimeException("Value is not a List type!");
        }
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public boolean isKeepAlive() {
        final String connectionHeader = getHeader(HttpHeaderNames.CONNECTION.toString());
        if (HttpHeaderValues.CLOSE.toString().equalsIgnoreCase(connectionHeader)) {
            return false;
        }

        if (HttpVersion.HTTP_1_0.text().equals(getProtocolVersion())) {
            if (false == HttpHeaderValues.KEEP_ALIVE.toString().equalsIgnoreCase(connectionHeader)) {
                return false;
            }
        }

        return true;
    }

    protected void putParams(QueryStringDecoder decoder) {
        if (null != decoder) {
            List<String> valueList;
            for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
                valueList = entry.getValue();
                if (null != valueList) {
                    this.putParam(entry.getKey(), 1 == valueList.size() ? valueList.get(0) : valueList);
                }
            }
        }
    }

    protected void putParams(HttpPostRequestDecoder decoder) {
        if (null == decoder) {
            return;
        }

        for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
            putParam(data);
        }
    }

    protected void putParam(InterfaceHttpData data) {
        final InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();
        if (dataType == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            try {
                this.putParam(attribute.getName(), attribute.getValue());
            } catch (IOException e) {
                Server.getInstance().getLogger().error(e);
            }
        } else if (dataType == InterfaceHttpData.HttpDataType.FileUpload) {
            FileUpload fileUpload = (FileUpload) data;
            if (fileUpload.isCompleted()) {
                try {
                    this.putParam(data.getName(), fileUpload.getFile());
                } catch (IOException e) {
                    Server.getInstance().getLogger().error(e, "Get file param [{}] error!", data.getName());
                }
            }
        }
    }

    public void putParam(String key, Object value) {
        this.params.put(key, value);
    }

    protected void putHeadersAndCookies(HttpHeaders headers) {
        for (Map.Entry<String, String> entry : headers) {
            this.headers.put(entry.getKey(), entry.getValue());
        }

        final String cookieString = this.headers.get(HttpHeaderNames.COOKIE.toString());
        if (StrUtil.isNotBlank(cookieString)) {
            final Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);
            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.name(), cookie);
            }
        }
    }

    protected void putIp(ChannelHandlerContext ctx) {
        String ip = getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(ip)) {
            ip = NetUtil.getMultistageReverseProxyIp(ip);
        } else {
            final InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
            ip = insocket.getAddress().getHostAddress();
        }
        this.ip = ip;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\r\nprotocolVersion: ").append(getProtocolVersion()).append("\r\n");
        sb.append("uri: ").append(getUri()).append("\r\n");
        sb.append("path: ").append(path).append("\r\n");
        sb.append("method: ").append(getMethod()).append("\r\n");
        sb.append("ip: ").append(ip).append("\r\n");
        sb.append("headers:\r\n ");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("params: \r\n");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        return sb.toString();
    }

    public final static ProxyRequest build(ChannelHandlerContext ctx, FullHttpRequest nettyRequest) {
        return new ProxyRequest(ctx, nettyRequest);
    }

}
