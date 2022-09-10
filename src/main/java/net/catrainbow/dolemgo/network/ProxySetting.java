package net.catrainbow.dolemgo.network;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.network.http.ProxyActionInterface;
import net.catrainbow.dolemgo.network.http.ProxyFilter;
import net.catrainbow.dolemgo.network.http.ProxyRoute;
import net.catrainbow.dolemgo.network.http.action.common.DefaultAction;
import net.catrainbow.dolemgo.network.http.action.common.ErrorAction;
import net.catrainbow.dolemgo.utils.exceptions.ServerSettingException;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxySetting {

    public final static String DEFAULT_CHARSET = "utf-8";

    public final static String MAPPING_ALL = "/*";

    public final static String MAPPING_ERROR = "/_error";

    private static String charset = DEFAULT_CHARSET;

    private static int port = 80;
    private static File root;
    private static Map<String, ProxyFilter> filterMap;
    private static Map<String, ProxyActionInterface> actionMap;

    static{
        filterMap = new ConcurrentHashMap<String, ProxyFilter>();

        actionMap = new ConcurrentHashMap<String, ProxyActionInterface>();
        actionMap.put(StrUtil.SLASH, new DefaultAction());
        actionMap.put(MAPPING_ERROR, new ErrorAction());
    }
    public static String getCharset() {
        return charset;
    }
    public static Charset charset() {
        return Charset.forName(getCharset());
    }
    public static void setCharset(String charset) {
        ProxySetting.charset = charset;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ProxySetting.port = port;
    }

    public static File getRoot() {
        return root;
    }

    public static boolean isRootAvailable() {
        if(root != null && root.isDirectory() && root.isHidden() == false && root.canRead()){
            return true;
        }
        return false;
    }

    public static String getRootPath() {
        return FileUtil.getAbsolutePath(root);
    }

    public static void setRoot(String root) {
        ProxySetting.root = FileUtil.mkdir(root);
        Server.getInstance().getLogger().debug("Set root to [{}]", ProxySetting.root.getAbsolutePath());
    }

    public static void setRoot(File root) {
        if(root.exists() == false){
            root.mkdirs();
        }else if(root.isDirectory() == false){
            throw new ServerSettingException(StrUtil.format("{} is not a directory!", root.getPath()));
        }
        ProxySetting.root = root;
    }

    public static Map<String, ProxyFilter> getProxyFilterMap() {
        return filterMap;
    }

    public static ProxyFilter getProxyFilter(String path){
        if(StrUtil.isBlank(path)){
            path = StrUtil.SLASH;
        }
        return getProxyFilterMap().get(path.trim());
    }

    public static void setProxyFilterMap(Map<String, ProxyFilter> filterMap) {
        ProxySetting.filterMap = filterMap;
    }

    public static void setProxyFilter(String path, ProxyFilter filter) {
        if(StrUtil.isBlank(path)){
            path = StrUtil.SLASH;
        }

        if(null == filter) {
            Server.getInstance().getLogger().warn("Added blank action, pass it.");
            return;
        }
        if(false == path.startsWith(StrUtil.SLASH)) {
            path = StrUtil.SLASH + path;
        }

        ProxySetting.filterMap.put(path, filter);
    }

    public static void setProxyFilter(String path, Class<? extends ProxyFilter> filterClass) {
        setProxyFilter(path, (ProxyFilter) Singleton.get(filterClass));
    }

    public static Map<String, ProxyActionInterface> getActionMap() {
        return actionMap;
    }

    public static ProxyActionInterface getAction(String path){
        if(StrUtil.isBlank(path)){
            path = StrUtil.SLASH;
        }
        return getActionMap().get(path.trim());
    }

    public static void setActionMap(Map<String, ProxyActionInterface> actionMap) {
        ProxySetting.actionMap = actionMap;
    }

    public static void setAction(String path, ProxyActionInterface action) {
        if(StrUtil.isBlank(path)){
            path = StrUtil.SLASH;
        }

        if(null == action) {
            Server.getInstance().getLogger().warn("Added blank action, pass it.");
            return;
        }
        if(false == path.startsWith(StrUtil.SLASH)) {
            path = StrUtil.SLASH + path;
        }

        ProxySetting.actionMap.put(path, action);
    }

    public static void setAction(String path, Class<? extends ProxyActionInterface> actionClass) {
        setAction(path, (ProxyActionInterface)Singleton.get(actionClass));
    }

    public static void setAction(ProxyActionInterface action) {
        final ProxyRoute route = action.getClass().getAnnotation(ProxyRoute.class);
        if(route != null){
            final String path = route.value();
            if(StrUtil.isNotBlank(path)){
                setAction(path, action);
                return;
            }
        }
        throw new ServerSettingException("Can not find Route annotation,please add annotation to Action class!");
    }
    public static void setAction(Class<? extends ProxyActionInterface> actionClass) {
        setAction((ProxyActionInterface) Singleton.get(actionClass));
    }

}
