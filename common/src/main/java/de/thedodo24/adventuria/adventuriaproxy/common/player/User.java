package de.thedodo24.adventuria.adventuriaproxy.common.player;

import com.arangodb.entity.BaseDocument;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.arango.ArangoWritable;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.Ban;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User implements ArangoWritable<UUID> {

    private UUID key;

    public Map<String, Object> values = new HashMap<>();

    public User(UUID key) {
        this.key = key;
    }

    @Override
    public UUID getKey() {
        return this.key;
    }

    @Override
    public void read(BaseDocument document) {
        this.values = document.getProperties();
    }

    @Override
    public void save(BaseDocument document) {
        document.setProperties(values);
    }

    public Object getProperty(String key) {
        if(this.values.containsKey(key))
            return this.values.get(key);
        return null;
    }

    public <Type> void updateProperty(String property, Type value) {
        this.updateProperty$(property, value);
    }

    private <Type> void  updateProperty$(String property, Type value) {
        if(this.values.containsKey(property))
            this.values.replace(property, value);
        else
            this.values.put(property, value);
        CommonModule.getInstance().getManager().getUserManager().save(this);
    }

    public void deleteProperty(String property) {
        this.values.remove(property);
        CommonModule.getInstance().getManager().getUserManager().save(this);
    }

    public boolean isSetProperty(String property) {
        return this.values.containsKey(property);
    }

    public String getName() {
        return (String) getProperty("name");
    }

    public void setName(String name) {
        updateProperty("name", name);
        CommonModule.getInstance().getManager().getUserManager().getNames().put(name, key);
    }

    public String getIP() {
        return (String) getProperty("ip");
    }

    public void setIP(String ip) {
        updateProperty("ip", ip);
    }

    public boolean isNameBanned() {
        Ban ban = CommonModule.getInstance().getManager().getBanManager().getBan(getKey().toString());
        if(ban != null)
            return ban.isActive();
        return false;
    }

    public boolean isIPBanned() {
        if(getIP() != null) {
            Ban ban = CommonModule.getInstance().getManager().getBanManager().getBan(getIP());
            if (ban != null)
                return ban.isActive();
        }
        return false;
    }

    public void setLastJoin(long lastJoin) {
        updateProperty("lastJoin", lastJoin);
    }

    public long getLastJoin() {
        return (long) getProperty("lastJoin");
    }

}