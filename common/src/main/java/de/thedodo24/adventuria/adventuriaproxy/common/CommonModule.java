package de.thedodo24.adventuria.adventuriaproxy.common;

import de.thedodo24.adventuria.adventuriaproxy.common.commands.ListCommand;
import de.thedodo24.adventuria.adventuriaproxy.common.listener.PlayerListener;
import de.thedodo24.adventuria.adventuriaproxy.common.module.Module;
import de.thedodo24.adventuria.adventuriaproxy.common.module.ModuleManager;
import de.thedodo24.adventuria.adventuriaproxy.common.module.ModuleSettings;
import de.thedodo24.adventuria.adventuriaproxy.common.player.User;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.TabList;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;
import java.util.logging.Level;

@ModuleSettings
@Getter
public class CommonModule extends Module {


    @Getter
    public static CommonModule instance;

    private LuckPerms luckPerms;

    private final UUID consoleUUID = UUID.fromString("93ea745e-62b3-4d45-b91f-ff8b5a9d993e");
    private User consoleUser;


    public CommonModule(ModuleSettings settings, ModuleManager manager, Plugin plugin) {
        super(settings, manager, plugin);
        instance = this;
    }

    @Override
    public void onLoad() {
        try {
            luckPerms = LuckPermsProvider.get();
            getManager().getPlugin().getLogger().log(Level.INFO, "Found LuckPerms!");
        } catch (IllegalStateException e) {
            getManager().getPlugin().getLogger().log(Level.SEVERE, "LuckPerms is not loaded!");
        }
    }

    @Override
    public void onEnable() {
        Language.init();
        consoleUser = getManager().getUserManager().getOrGenerate(consoleUUID, User::new);
        consoleUser.setName("CONSOLE");
        registerListener(new PlayerListener());
        registerCommand(new ListCommand("list"));
    }
}
