package net.catrainbow.dolemgo;

import cn.hutool.log.Log;
import net.catrainbow.dolemgo.command.*;
import net.catrainbow.dolemgo.command.console.TerminalConsole;
import net.catrainbow.dolemgo.command.utils.CommandUtils;
import net.catrainbow.dolemgo.event.EventManager;
import net.catrainbow.dolemgo.event.common.ServerStartEvent;
import net.catrainbow.dolemgo.network.RaknetInterface;
import net.catrainbow.dolemgo.network.protocol.Network;
import net.catrainbow.dolemgo.plugin.PluginManager;
import net.catrainbow.dolemgo.scheduler.ServerScheduler;
import net.catrainbow.dolemgo.utils.Config;
import net.catrainbow.dolemgo.utils.ConfigSection;
import net.catrainbow.dolemgo.utils.RSA;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    public boolean isRunning;
    protected final long runningTime;
    public static Server instance;
    private final Log logger;
    private final ServerScheduler scheduler;
    public String dataPath;
    public String pluginPath;
    private final Config properties;
    public RaknetInterface raknetInterface;
    public PluginManager pluginManager;
    public CommandSender consoleSender;
    private CommandMap commandMap;
    private TerminalConsole console;
    public final int port;
    private int currentTick = 0;
    public Network networkInterface;
    public HashMap<String, Player> onlinePlayers;
    public String publicKey;
    public String privateKey;

    public EventManager eventManager;

    public Server(Log logger, String dataPath, String pluginPath) {
        instance = this;
        this.isRunning = true;
        this.runningTime = System.currentTimeMillis();
        this.logger = logger;
        this.dataPath = dataPath;
        this.pluginPath = pluginPath;

        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }


        this.getLogger().info("Loading server properties...");
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
            {
                put("private_key", getPrivateKey());
                put("public_key", getPublicKey());
                put("server-ip", "0:0:0:0");
                put("port", 88);
            }
        });

        this.port = Integer.parseInt(this.properties.getString("port"));
        this.privateKey = this.properties.getString("private_key");
        this.publicKey = this.properties.getString("public_key");
        this.scheduler = new ServerScheduler();
        this.pluginManager = new PluginManager(this);
        this.commandMap = new DefaultCommandMap(this, SimpleCommandMap.DEFAULT_PREFIX);
        this.console = new TerminalConsole(this);
        this.onlinePlayers = new HashMap<>();
        this.consoleSender = new ConsoleCommandSender(this);
        this.networkInterface = new Network();
        this.eventManager = new EventManager(this);

        this.properties.save(true);
        this.start();
    }

    private void start() {
        this.console.getConsoleThread().start();
        this.getLogger().info("Loading plugins...");
        this.pluginManager.enableAllPlugins();
        this.raknetInterface = new RaknetInterface(this);
        this.raknetInterface.start();

        this.isRunning = true;
        this.eventManager.callEvent(new ServerStartEvent(this));
        this.getLogger().info("Done! proxy is running on " + port + ". (" + (System.currentTimeMillis() - this.runningTime) + "ms)");
        this.tickProcessor();
        this.shutdown();
    }

    public CommandMap getCommandMap() {
        return this.commandMap;
    }

    public void shutdown() {
        this.isRunning = false;
        this.pluginManager.disableAllPlugins();
        System.exit(0);
    }

    public boolean dispatchCommand(CommandSender sender, String message) {
        if (message.trim().isEmpty()) return false;
        String[] args = message.split("\\s+");
        if (args.length < 1) return false;
        Command command = this.getCommandMap().getCommand(args[0]);
        if (command == null) return false;
        String[] shiftedArgs = new String[0];
        if (command.getSettings().isQuoteAware()) {
            ArrayList<String> val = CommandUtils.parseArguments(message);
            val.remove(0);
            shiftedArgs = val.toArray(new String[]{});
        }
        return this.commandMap.handleCommand(sender, args[0], shiftedArgs);
    }

    public static Server getInstance() {
        return instance;
    }

    public Log getLogger() {
        return this.logger;
    }

    public CommandSender getConsoleSender() {
        return this.consoleSender;
    }

    public ServerScheduler getScheduler() {
        return this.scheduler;
    }

    public void tickProcessor() {
        while (this.isRunning) {
            this.tick();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                this.logger.error(e);
            }
        }
    }

    private void tick() {
        ++this.currentTick;
        this.scheduler.mainThreadHeartbeat(this.currentTick);
    }

    protected String getPrivateKey() {
        try {
            return RSA.getRSAKeyString(1024).get(1);
        } catch (Exception e) {
            this.getLogger().error(e);
            return null;
        }
    }

    private String getPublicKey() {
        try {
            return RSA.getRSAKeyString(1024).get(0);
        } catch (Exception e) {
            this.getLogger().error(e);
            return null;
        }
    }


}
