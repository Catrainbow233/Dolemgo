package net.catrainbow.dolemgo.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
 * Simple class loader which holds classes of plugins.
 * It allows plugins to access each other.
 */
public class PluginClassLoader extends URLClassLoader {

    private final PluginManager pluginManager;
    private final HashMap<String, Class<?>> classes = new HashMap<>();

    public PluginClassLoader(PluginManager pluginManager, ClassLoader parent, File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.pluginManager = pluginManager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return this.findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("dev.waterdog.waterdogpe.")) { // Proxy classes should be known
            throw new ClassNotFoundException(name);
        }

        Class<?> result = this.classes.get(name);
        if (result != null) {
            return result;
        }

        if (checkGlobal) {
            result = this.pluginManager.getClassFromCache(name);
        }

        if (result == null && (result = super.findClass(name)) != null) {
            this.pluginManager.cacheClass(name, result);
        }
        this.classes.put(name, result);
        return result;
    }

}
