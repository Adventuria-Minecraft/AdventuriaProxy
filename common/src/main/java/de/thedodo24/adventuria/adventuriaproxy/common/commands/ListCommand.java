package de.thedodo24.adventuria.adventuriaproxy.common.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListCommand extends Command implements TabExecutor {

    public ListCommand(String list) {
        super(list);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        HashMap<Group, List<String>> groupMap = new HashMap<>();
        for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
            User luckpermsUser = CommonModule.getInstance().getLuckPerms().getUserManager().getUser(all.getUniqueId());
            String group = luckpermsUser.getPrimaryGroup();
            Group playerGroup = CommonModule.getInstance().getLuckPerms().getGroupManager().getGroup(group);

            if(groupMap.containsKey(playerGroup)) {
                List<String> playersInGroup = groupMap.get(playerGroup);
                playersInGroup.add(all.getName());
                groupMap.replace(playerGroup, playersInGroup);
            } else {
                groupMap.put(playerGroup, Lists.newArrayList(all.getName()));
            }
        }


        s.sendMessage(TextComponent.fromLegacyText(Language.get("player-online", ProxyServer.getInstance().getOnlineCount())));
        groupMap.keySet().stream().sorted(Comparator.comparingInt(g -> 100 - g.getWeight().orElse(0))).forEach(g -> {
            ChatColor color = ChatColor.of(g.getCachedData().getMetaData().getPrefix());
            s.sendMessage(TextComponent.fromLegacyText("§7» " + color + g.getCachedData().getMetaData().getSuffix() + "§7: " + color + String.join("§7, " + color, groupMap.get(g))));
        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
