package net.catrainbow.dolemgo.plugin;

import net.catrainbow.dolemgo.Server;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PluginManager {

    public static final Yaml yamlLoader;

    static {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        yamlLoader = new Yaml(new CustomClassLoaderConstructor(PluginManager.class.getClassLoader()), representer);
    }

    private final Server proxy;
    private final PluginLoader pluginLoader;

    protected final HashMap<String, PluginClassLoader> pluginClassLoaders = new HashMap<>();
    private final HashMap<String, Plugin> pluginMap = new HashMap<>();
    private final HashMap<String, Class<?>> cachedClasses = new HashMap<>();

    public PluginManager(Server proxy) {
        this.proxy = proxy;
        this.pluginLoader = new PluginLoader(this);
        try {
            this.loadPluginsIn(Paths.get(this.proxy.pluginPath));
        } catch (IOException e) {
            this.proxy.getLogger().error("Error while filtering plugin files", e);
        }
    }

    public void loadPluginsIn(Path folderPath) throws IOException {
        Comparator<PluginYAML> comparator = (o1, o2) -> {
            if (o2.getName().equals(o1.getName())) {
                return 0;
            }
            if (o2.getDepends() == null) {
                return 1;
            }
            return o2.getDepends().contains(o1.getName()) ? -1 : 1;
        };

        Map<PluginYAML, Path> plugins = new TreeMap<>(comparator);

        Files.walk(folderPath).filter(Files::isRegularFile).filter(PluginLoader::isJarFile).forEach(jarPath -> {
            PluginYAML config = this.loadPluginConfig(jarPath);
            if (config != null) {
                plugins.put(config, jarPath);
            }
        });

        plugins.forEach(this::loadPlugin);
    }

    public PluginYAML loadPluginConfig(Path path) {
        if (!Files.isRegularFile(path) || !PluginLoader.isJarFile(path)) {
            this.proxy.getLogger().warn("Cannot load plugin: Provided file is no jar file: " + path.getFileName());
            return null;
        }

        File pluginFile = path.toFile();
        if (!pluginFile.exists()) {
            return null;
        }
        return this.pluginLoader.loadPluginData(pluginFile, this.yamlLoader);
    }

    public Plugin loadPlugin(PluginYAML config, Path path) {
        File pluginFile = path.toFile();
        if (this.getPluginByName(config.getName()) != null) {
            this.proxy.getLogger().warn("Plugin is already loaded: " + config.getName());
            return null;
        }

        Plugin plugin = this.pluginLoader.loadPluginJAR(config, pluginFile);
        if (plugin == null) {
            return null;
        }

        try {
            plugin.onStartup();
        } catch (Exception e) {
            this.proxy.getLogger().error("failed to load plugin " + config.getName() + "!", e);
            return null;
        }

        this.proxy.getLogger().info("Loaded plugin " + config.getName() + " successfully! (version=" + config.getVersion() + ",author=" + config.getAuthor() + ")");
        this.pluginMap.put(config.getName(), plugin);
        return plugin;
    }

    public void enableAllPlugins() {
        LinkedList<Plugin> failed = new LinkedList<>();

        for (Plugin plugin : this.pluginMap.values()) {
            if (!this.enablePlugin(plugin, null)) {
                failed.add(plugin);
            }
        }

        if (failed.isEmpty()) {
            return;
        }

        StringBuilder builder = new StringBuilder("§cFailed to load plugins: §e");
        while (failed.peek() != null) {
            Plugin plugin = failed.poll();
            builder.append(plugin.getName());
            if (failed.peek() != null) {
                builder.append(", ");
            }
        }
        this.proxy.getLogger().warn(builder.toString());
    }

    public boolean enablePlugin(Plugin plugin, String parent) {
        if (plugin.isEnabled()) return true;
        String pluginName = plugin.getName();

        if (plugin.getDescription().getDepends() != null) {
            for (String depend : plugin.getDescription().getDepends()) {
                if (depend.equals(parent)) {
                    this.proxy.getLogger().warn("§cCan not enable plugin " + pluginName + " circular dependency " + parent + "!");
                    return false;
                }

                Plugin dependPlugin = this.getPluginByName(depend);
                if (dependPlugin == null) {
                    this.proxy.getLogger().warn("§cCan not enable plugin " + pluginName + " missing dependency " + depend + "!");
                    return false;
                }

                if (!dependPlugin.isEnabled() && !this.enablePlugin(dependPlugin, pluginName)) {
                    return false;
                }
            }
        }

        try {
            plugin.setEnabled(true);
        } catch (Exception e) {
            this.proxy.getLogger().error(e.getMessage(), e.getCause());
            return false;
        }
        return true;
    }

    public void disableAllPlugins() {
        for (Plugin plugin : this.pluginMap.values()) {
            this.proxy.getLogger().info("Disabling plugin " + plugin.getName() + "!");
            try {
                plugin.setEnabled(false);
            } catch (Exception e) {
                this.proxy.getLogger().error(e.getMessage(), e.getCause());
            }
        }
    }

    public Class<?> getClassFromCache(String className) {
        Class<?> clazz = this.cachedClasses.get(className);
        if (clazz != null) {
            return clazz;
        }

        for (PluginClassLoader loader : this.pluginClassLoaders.values()) {
            try {
                if ((clazz = loader.findClass(className, false)) != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
        return null;
    }

    protected void cacheClass(String className, Class<?> clazz) {
        this.cachedClasses.putIfAbsent(className, clazz);
    }

    public Map<String, Plugin> getPluginMap() {
        return Collections.unmodifiableMap(this.pluginMap);
    }

    public Collection<Plugin> getPlugins() {
        return Collections.unmodifiableCollection(this.pluginMap.values());
    }

    public Plugin getPluginByName(String pluginName) {
        return this.pluginMap.getOrDefault(pluginName, null);
    }

    public Server getProxy() {
        return this.proxy;
    }
}
