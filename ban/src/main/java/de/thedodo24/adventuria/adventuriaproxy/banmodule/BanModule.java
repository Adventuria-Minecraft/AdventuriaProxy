package de.thedodo24.adventuria.adventuriaproxy.banmodule;

import de.thedodo24.adventuria.adventuriaproxy.banmodule.commands.BanCommand;
import de.thedodo24.adventuria.adventuriaproxy.banmodule.commands.CheckCommand;
import de.thedodo24.adventuria.adventuriaproxy.banmodule.commands.UnbanCommand;
import de.thedodo24.adventuria.adventuriaproxy.banmodule.listener.PlayerListeners;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.module.Module;
import de.thedodo24.adventuria.adventuriaproxy.common.module.ModuleManager;
import de.thedodo24.adventuria.adventuriaproxy.common.module.ModuleSettings;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.shanerx.mojang.Mojang;

import java.util.logging.Level;

@ModuleSettings
@Getter
public class BanModule extends Module {

    @Getter
    public static BanModule instance;

    private Mojang mojangApi;


    public BanModule(ModuleSettings settings, ModuleManager manager, Plugin plugin) {
        super(settings, manager, plugin);
        instance = this;
    }


    @Override
    public void onEnable() {
        mojangApi = new Mojang().connect();

        registerListener(new PlayerListeners());
        registerCommand(new BanCommand("ban"), new CheckCommand("check"), new UnbanCommand("unban"));
    }

}
