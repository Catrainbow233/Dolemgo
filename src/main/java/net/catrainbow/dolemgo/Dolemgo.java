package net.catrainbow.dolemgo;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.StaticLog;

public class Dolemgo {

    public static final String VERSION = "1.0.0";
    public static final String PATH = System.getProperty("user.dir") + "/";
    public static final String PLUGIN_PATH = PATH + "plugins";
    public static final Log log = LogFactory.get("Server");

    public static void main(String[] args) {
        getLogger().info("Dolemgo Proxy started ");
        try {
            new Server(log, PATH, PLUGIN_PATH);
        } catch (Exception e) {
            getLogger().error(e);
        }
    }

    public static Log getLogger() {
        return log;
    }

}
