package de.thedodo24.adventuria.adventuriaproxy;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import de.thedodo24.adventuria.adventuriaproxy.banmodule.BanModule;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.module.ModuleManager;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.ConfigFile;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

@Getter
public class AdventuriaProxy extends Plugin  {

    @Getter
    public static AdventuriaProxy instance;

    public ArangoDatabase arangoDatabase;
    public ArangoDB arangoDB;
    public String prefix = "[Adventuria] ";

    private ModuleManager moduleManager;



    @Override
    public void onEnable() {
        instance = this;

        ConfigFile configFile = new ConfigFile(getDataFolder(), "config.yml");
        configFile.create("database.host", "localhost");
        configFile.create("database.user", "user");
        configFile.create("database.password", "password");
        configFile.create("database.database", "database");
        configFile.create("database.port", 8529);
        configFile.save();

        arangoDB = new ArangoDB.Builder().host(configFile.getString("database.host"), configFile.getInt("database.port"))
                .user(configFile.getString("database.user")).password(configFile.getString("database.password")).build();
        arangoDatabase = arangoDB.db(configFile.getString("database.database"));

        moduleManager = new ModuleManager(getArangoDatabase(), this);
        moduleManager.loadModules(CommonModule.class, BanModule.class);
        getLogger().log(Level.INFO, "Loaded Adventuria proxy plugin");

    }
}
