package net.catrainbow.dolemgo.plugin;

import net.catrainbow.dolemgo.Server;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {

    private final PluginManager pluginManager;

    public PluginLoader(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    protected static boolean isJarFile(Path file) {
        return file.getFileName().toString().endsWith(".jar");
    }

    protected Plugin loadPluginJAR(PluginYAML pluginConfig, File pluginJar) {
        PluginClassLoader loader;
        try {
            loader = new PluginClassLoader(this.pluginManager, this.getClass().getClassLoader(), pluginJar);
            this.pluginManager.pluginClassLoaders.put(pluginConfig.getName(), loader);
        } catch (MalformedURLException e) {
            Server.getInstance().getLogger().error(e);
            return null;
        }

        try {
            Class<?> mainClass = loader.loadClass(pluginConfig.getMain());
            if (!Plugin.class.isAssignableFrom(mainClass)) {
                return null;
            }

            Class<? extends Plugin> castedMain = mainClass.asSubclass(Plugin.class);
            Plugin plugin = castedMain.getDeclaredConstructor().newInstance();
            plugin.init(pluginConfig, this.pluginManager.getProxy(), pluginJar);
            return plugin;
        } catch (Exception e) {
            Server.getInstance().getLogger().error("Error while loading plugin main class(main=" + pluginConfig.getMain() + ",plugin=" + pluginConfig.getName() + ")", e);
        }
        return null;
    }

    protected PluginYAML loadPluginData(File file, Yaml yaml) {
        try (JarFile pluginJar = new JarFile(file)) {
            JarEntry configEntry = pluginJar.getJarEntry("dolemgo.yml");
            if (configEntry == null) {
                configEntry = pluginJar.getJarEntry("plugin.yml");
            }

            if (configEntry == null) {
                Server.getInstance().getLogger().warn("Jar file " + file.getName() + " doesnt contain a dolemgo.yml or plugin.yml!");
                return null;
            }

            try (InputStream fileStream = pluginJar.getInputStream(configEntry)) {
                PluginYAML pluginConfig = yaml.loadAs(fileStream, PluginYAML.class);
                if (pluginConfig.getMain() != null && pluginConfig.getName() != null) {
                    // Valid plugin.yml, main and name set
                    return pluginConfig;
                }
            }
            Server.getInstance().getLogger().warn("Invalid plugin.yml for " + file.getName() + ": main and/or name property missing");
        } catch (Exception e) {
            Server.getInstance().getLogger().error("Can not load plugin files in " + file.getPath(), e);
        }
        return null;
    }
}
