package de.thedodo24.adventuria.adventuriaproxy.common.ban;

import com.arangodb.entity.BaseDocument;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.arango.ArangoWritable;
import de.thedodo24.adventuria.adventuriaproxy.common.player.User;

import java.util.HashMap;
import java.util.Map;

public class Ban implements ArangoWritable<Long> {

    private Long key;

    public Map<String, Object> values;

    public Ban(long key, User issuer, String reason, BanType type, String banable, long duration, long timestamp) {
        this.key = key;
        values = new HashMap<>() {{
            put("readableID", CommonModule.getInstance().getManager().getBanManager().getReadableID(key, type));
            put("type", type.toString());
            put("banned", banable);
            put("issuer", issuer.getName());
            put("reason", reason);
            put("duration", duration);
            put("timestamp", timestamp);
            put("active", true);
        }};
    }

    @Override
    public Long getKey() {
        return key;
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
        CommonModule.getInstance().getManager().getBanManager().save(this);
    }

    public void deleteProperty(String property) {
        this.values.remove(property);
        CommonModule.getInstance().getManager().getBanManager().save(this);
    }

    public boolean isSetProperty(String property) {
        return this.values.containsKey(property);
    }

    public String getReadableID() {
        return (String) getProperty("readableID");
    }

    public BanType getType() {
        return BanType.fromString((String) getProperty("type"));
    }

    public boolean isIPBan() {
        return getType().equals(BanType.IP_BAN_TEMP) || getType().equals(BanType.IP_BAN);
    }

    public boolean isTempBan() {
        return getType().equals(BanType.IP_BAN_TEMP) || getType().equals(BanType.PLAYER_BAN_TEMP);
    }

    public String getBanned() {
        return (String) getProperty("banned");
    }

    public User getIssuer() {
        return CommonModule.getInstance().getManager().getUserManager().getByName((String) getProperty("issuer"));
    }

    public String getReason() {
        return (String) getProperty("reason");
    }

    public long getDuration() {
        return (long) getProperty("duration");
    }

    public boolean isActive() {
        return (boolean) getProperty("active");
    }

    public void setActive(boolean active) {
        updateProperty("active", active);
    }

    public long getTimestamp() {
        return (long) getProperty("timestamp");
    }


}
