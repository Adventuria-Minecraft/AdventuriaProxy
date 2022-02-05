package de.thedodo24.adventuria.adventuriaproxy.common.ban;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.google.common.collect.Lists;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.arango.CollectionManager;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class BanManager extends CollectionManager<Ban, Long> {


    private ArangoDatabase database;

    public BanManager(ArangoDatabase database) {
        super("ban", (key -> new Ban(key, CommonModule.getInstance().getConsoleUser(), Language.get("ban-message"), BanType.PLAYER_BAN, "", 0, System.currentTimeMillis())), database, ((key, obj) -> false));
        this.database = database;
    }

    public long getHighestID() {
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR ban IN " + this.collection.name() + " SORT TO_NUMBER(ban._key) DESC LIMIT 1 RETURN {id: ban._key}",
                VPackSlice.class);
        long highestID = 0;
        while(cursor.hasNext()) {
            long id = Long.parseLong(cursor.next().get("id").getAsString());
            highestID = id;
        }
        this.closeCursor(cursor);
        return highestID;
    }

    public List<Ban> getAllBans() {
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR ban IN " + this.collection.name() + " RETURN {id: ban._key}",
                null, null, VPackSlice.class);
        List<Ban> bans = Lists.newArrayList();
        while(cursor.hasNext())
            bans.add(getOrGenerate(Long.parseLong(cursor.next().get("id").getAsString())));
        this.closeCursor(cursor);
        return bans;
    }


    public List<Ban> getBans(String s) {
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR ban IN " + this.collection.name() + " FILTER LIKE (ban.banned, @name, true) RETURN {id: ban._key}",
                new MapBuilder().put("name", s).get(), null, VPackSlice.class);
        List<Ban> bans = Lists.newArrayList();
        while(cursor.hasNext())
            bans.add(getOrGenerate(Long.parseLong(cursor.next().get("id").getAsString())));
        this.closeCursor(cursor);
        return bans;
    }

    public List<Ban> getActiveBans(String s) {
        return getBans(s).stream().filter(Ban::isActive).collect(Collectors.toList());
    }

    public Ban getBan(String s) {
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR ban IN " + this.collection.name() + " FILTER LIKE (ban.banned, @name, true) RETURN {id: ban._key}",
                new MapBuilder().put("name", s).get(), null, VPackSlice.class);
        List<Long> ids = Lists.newArrayList();
        while(cursor.hasNext())
            ids.add(Long.parseLong(cursor.next().get("id").getAsString()));
        this.closeCursor(cursor);
        if(ids.size() > 0) {
            return getOrGenerate(ids.stream().max(Comparator.naturalOrder()).get());
        }
        return null;
    }

    public long getBanByID(String s) {
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR ban IN " + this.collection.name() + " FILTER LIKE (ban.readableID, @name, true) RETURN {id: ban._key}",
                new MapBuilder().put("name", s).get(), null, VPackSlice.class);
        long id = -1;
        if(cursor.hasNext())
            id = Long.parseLong(cursor.next().get("id").getAsString());
        this.closeCursor(cursor);
        return id;
    }

    public String getReadableID(long id, BanType banType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        Date date = new Date();
        String formatedDate = dateFormat.format(date);
        return switch (banType) {
            case IP_BAN -> "#I" + formatedDate + "-" + id;
            case PLAYER_BAN -> "#P" + formatedDate + "-" + id;
            case IP_BAN_TEMP -> "#IT" + formatedDate + "-" + id;
            case PLAYER_BAN_TEMP -> "#PT" + formatedDate + "-" + id;
        };
    }

    public boolean isIPBanned(String ip) {
        Ban ban = getBan(ip);
        if(ban != null)
            return ban.isActive();
        return false;
    }

    public boolean isValidIP(String ip) {
        try {
            if(ip == null || ip.isEmpty())
                return false;

            String[] parts = ip.split("\\.");
            if(parts.length != 4)
                return false;
            for(String s : parts) {
                int i = Integer.parseInt(s);
                if((i < 0) || (i > 255))
                    return false;
            }

            if(ip.endsWith("."))
                return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getReadableDuration(long duration, long begin) {
        long current = System.currentTimeMillis();
        long end = begin + duration;
        long mom = end - current;
        Duration dur = Duration.ofMillis(mom);
        if(dur.toDays() > 0) {
            if(dur.toDays() == 1) {
                return dur.toDays() + " Tag";
            } else {
                return dur.toDays() + " Tage";
            }
        } else {
            return String.format("%02d:%02d:%02d", dur.toHours(), dur.toMinutesPart(), dur.toSecondsPart());
        }
    }

    public void update(Ban ban) {
        this.save(ban);
        this.uncache(ban.getKey());
    }
}
