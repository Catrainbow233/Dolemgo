package net.catrainbow.dolemgo.plugin;

import cn.hutool.log.Log;
import com.google.common.base.Preconditions;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.utils.Config;
import net.catrainbow.dolemgo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class Plugin {

    protected boolean enabled = false;
    private PluginYAML description;
    private Server proxy;
    private Log logger;
    private File pluginFile;
    private File dataFolder;
    private File configFile;
    private Config config;
    private boolean initialized = false;

    public Plugin() {
        this.logger = proxy.getLogger();
    }

    protected final void init(PluginYAML description, Server proxy, File pluginFile) {
        Preconditions.checkArgument(!this.initialized, "Plugin has been already initialized!");
        this.initialized = true;
        this.description = description;
        this.proxy = proxy;
        this.logger = proxy.getLogger();

        this.pluginFile = pluginFile;
        this.dataFolder = new File(proxy.pluginPath + "/" + description.getName().toLowerCase() + "/");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        this.configFile = new File(this.dataFolder, "config.yml");
    }

    public void onStartup() {
    }

    public abstract void onEnable();

    public void onDisable() {
    }

    public InputStream getResourceFile(String filename) {
        try {
            JarFile pluginJar = new JarFile(this.pluginFile);
            JarEntry entry = pluginJar.getJarEntry(filename);
            return pluginJar.getInputStream(entry);
        } catch (IOException e) {
            this.proxy.getLogger().error("Can not get plugin resource!", e);
        }
        return null;
    }

    public boolean saveResource(String filename) {
        return this.saveResource(filename, false);
    }

    public boolean saveResource(String filename, boolean replace) {
        return this.saveResource(filename, filename, replace);
    }

    public boolean saveResource(String filename, String outputName, boolean replace) {
        Preconditions.checkArgument(filename != null && !filename.trim().isEmpty(), "Filename can not be null!");

        File file = new File(this.dataFolder, outputName);
        if (file.exists() && !replace) {
            return false;
        }

        try (InputStream resource = this.getResourceFile(filename)) {
            if (resource == null) {
                return false;
            }
            File outFolder = file.getParentFile();
            if (!outFolder.exists()) {
                outFolder.mkdirs();
            }
            FileUtils.writeFile(file, resource);
        } catch (IOException e) {
            this.proxy.getLogger().error("Can not save plugin file!", e);
            return false;
        }
        return true;
    }

    public void loadConfig() {
        try {
            this.saveResource("config.yml");
            this.config = new Config(this.configFile, 2);
        } catch (Exception e) {
            this.proxy.getLogger().error("Can not load plugin config!");
        }
    }

    public Config getConfig() {
        if (this.config == null) {
            this.loadConfig();
        }
        return this.config;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        try {
            if (enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        } catch (Exception e) {
            this.logger.error(e);
        }
    }

    public PluginYAML getDescription() {
        return this.description;
    }

    public String getName() {
        return this.description.getName();
    }

    public Server getProxy() {
        return this.proxy;
    }

    public Log getLogger() {
        return this.logger;
    }

    public File getDataFolder() {
        return this.dataFolder;
    }
}
