package net.catrainbow.dolemgo.event.common;

import net.catrainbow.dolemgo.event.Event;
import net.catrainbow.dolemgo.plugin.Plugin;

public class PluginLoadEvent extends Event {

    private final Plugin plugin;

    public PluginLoadEvent(Plugin plugin){
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
