package de.thedodo24.adventuria.adventuriaproxy.common.utils;

import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import lombok.Getter;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.score.*;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;

public class TabList {


    public static void setPrefix(ProxiedPlayer p, Scoreboard scoreboard) {
        User luckpermsUser = CommonModule.getInstance().getLuckPerms().getUserManager().getUser(p.getUniqueId());
        String group = luckpermsUser.getPrimaryGroup();
        Group playerGroup = CommonModule.getInstance().getLuckPerms().getGroupManager().getGroup(group);
        int weight = playerGroup.getWeight().orElse(0);
        String prefix = playerGroup.getCachedData().getMetaData().getPrefix();
        String suffix = playerGroup.getCachedData().getMetaData().getSuffix();

        ChatColor color = ChatColor.of(prefix);

        int teamWeight = 100 - weight;
        Team t;
        if(scoreboard.getTeam(teamWeight + "") == null) {
            t = new Team(teamWeight + "");
            t.setColor(color.hashCode());
            t.setPrefix(suffix + " §8● " + color);
            scoreboard.addTeam(t);
        } else
            t = scoreboard.getTeam(teamWeight + "");
        scoreboard.getTeams().stream().filter(ts -> ts.getPlayers().contains(p.getName())).forEach(ts -> ts.removePlayer(p.getName()));

        t.addPlayer(p.getName());
        p.setDisplayName(color + suffix + " §8● " + color + p.getName());
    }
}
