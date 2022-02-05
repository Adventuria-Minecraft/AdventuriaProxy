package de.thedodo24.adventuria.adventuriaproxy.common.module;

import de.thedodo24.adventuria.adventuriaproxy.common.utils.ConfigFile;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Module {

    protected String name;
    protected String commandGroup;

    protected ConfigFile config;
    protected ModuleManager manager;
    protected Plugin plugin;
    protected List<Listener> activeListeners;

    @Setter
    protected boolean enabled;

    public Module(ModuleSettings settings, ModuleManager manager, Plugin plugin) {
        this.name = settings.name().replace("module", getClass().getSimpleName().replace("Module", "")).toLowerCase();
        this.commandGroup = settings.commandGroup().replace("[module]", this.name);

        this.manager = manager;
        this.plugin = plugin;

        this.enabled = true;
        this.activeListeners = new ArrayList<>();

        this.config = new ConfigFile(plugin.getDataFolder(), this.name);
    }

    protected final void registerListener(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, listener);
    }

    protected final void registerCommand(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, command);
    }

    protected final void registerCommand(Command... commands) {
        for(Command command : commands)
            registerCommand(command);
    }

    protected final void registerListener(Listener... listeners) {
        for(Listener listener : listeners)
            registerListener(listener);
    }

    public void onLoad() { plugin.getLogger().info("Loaded " + name + " module"); }

    public void onEnable() { plugin.getLogger().info("Enabling " + name + " module"); }

    public void onDisable() { plugin.getLogger().info("Disabling " + name + " module"); }

}
