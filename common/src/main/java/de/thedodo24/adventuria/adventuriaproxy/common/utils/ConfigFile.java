package de.thedodo24.adventuria.adventuriaproxy.common.utils;

import com.google.common.collect.Lists;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;

public class ConfigFile {

    private String configurationName;
    private File configurationFolder;
    private File configurationFile;

    private Configuration configuration;

    public ConfigFile(File configurationFolder, String fileName) {
        this.configurationFolder = configurationFolder;
        this.configurationName = fileName;

        init();
    }

    public Configuration init() {
        if(!configurationFolder.exists())
            configurationFolder.mkdirs();
        try {
            if(!(this.configurationFile = new File(configurationFolder, configurationName)).exists())
                this.configurationFile.createNewFile();

            if(configuration == null) {
                this.configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(configurationFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.configuration;
    }

    public boolean save() {
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, configurationFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean create(String path, Object obj) {
        try {
            if(!configuration.contains(path))
                return set(path, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean set(String path, Object obj) {
        try {
            configuration.set(path, obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean contains(String path) {return get(path) != null;}

    public List<String> getKeys() {
        try {
            return Lists.newArrayList(configuration.getKeys());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object get(String path) {
        try {
            return configuration.get(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getString(String path) {
        try {
            return configuration.getString(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getInt(String path) {
        try {
            return configuration.getInt(path);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Integer> getIntList(String path) {
        try {
            return configuration.getIntList(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getDouble(String path) {
        try {
            return configuration.getDouble(path);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public List<Double> getDoubleList(String path) {
        try {
            return configuration.getDoubleList(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getLong(String path) {
        try {
            return configuration.getLong(path);
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public List<Long> getLongList(String path) {
        try {
            return configuration.getLongList(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

