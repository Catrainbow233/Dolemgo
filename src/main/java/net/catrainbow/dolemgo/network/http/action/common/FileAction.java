package net.catrainbow.dolemgo.network.http.action.common;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.network.ProxySetting;
import net.catrainbow.dolemgo.network.http.ProxyActionInterface;
import net.catrainbow.dolemgo.network.http.ProxyRequest;
import net.catrainbow.dolemgo.network.http.ProxyResponse;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class FileAction implements ProxyActionInterface {

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private static final SimpleDateFormat HTTP_DATE_FORMATER = new SimpleDateFormat(DatePattern.HTTP_DATETIME_PATTERN, Locale.US);

    @Override
    public void doAction(ProxyRequest request, ProxyResponse response) {
        if (false == ProxyRequest.METHOD_GET.equalsIgnoreCase(request.getMethod())) {
            response.sendError(HttpResponseStatus.METHOD_NOT_ALLOWED, "Please use GET method to request file!");
            return;
        }

        if (ProxySetting.isRootAvailable() == false) {
            response.sendError(HttpResponseStatus.NOT_FOUND, "404 Root dir not avaliable!");
            return;
        }

        final File file = getFileByPath(request.getPath());

        if (file.isHidden() || !file.exists()) {
            response.sendError(HttpResponseStatus.NOT_FOUND, "404 File not found!");
            return;
        }

        if (false == file.isFile()) {
            response.sendError(HttpResponseStatus.FORBIDDEN, "403 Forbidden!");
            return;
        }

        String ifModifiedSince = request.getHeader(HttpHeaderNames.IF_MODIFIED_SINCE.toString());
        if (StrUtil.isNotBlank(ifModifiedSince)) {
            Date ifModifiedSinceDate = null;
            try {
                ifModifiedSinceDate = DateUtil.parse(ifModifiedSince, HTTP_DATE_FORMATER);
            } catch (Exception e) {
                Server.getInstance().getLogger().error("If-Modified-Since header parse error: {}", e.getMessage());
            }
            if (ifModifiedSinceDate != null) {
                long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
                long fileLastModifiedSeconds = file.lastModified() / 1000;
                if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                    Server.getInstance().getLogger().debug("File {} not modified.", file.getPath());
                    response.sendNotModified();
                    return;
                }
            }
        }

        response.setContent(file);
    }

    public static File getFileByPath(String httpPath) {
        try {
            httpPath = URLDecoder.decode(httpPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (httpPath.isEmpty() || httpPath.charAt(0) != '/') {
            return null;
        }

        if (httpPath.contains("/.") || httpPath.contains("./") || httpPath.charAt(0) == '.' || httpPath.charAt(httpPath.length() - 1) == '.' || ReUtil.isMatch(INSECURE_URI, httpPath)) {
            return null;
        }

        return FileUtil.file(ProxySetting.getRoot(), httpPath);
    }

}
