package de.thedodo24.adventuria.adventuriaproxy.banmodule.commands;

import de.thedodo24.adventuria.adventuriaproxy.banmodule.BanModule;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.Ban;
import de.thedodo24.adventuria.adventuriaproxy.common.player.User;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.HexColor;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CheckCommand extends Command implements TabExecutor {


    public CheckCommand(String check) {
        super(check);
    }

    private final String prefix = "§7§l| §cBann §7» ";

    @Override
    public void execute(CommandSender s, String[] args) {
        if(s.hasPermission("evergreenproxy.check")) {
            if(args.length == 1) {
                User user = CommonModule.getInstance().getManager().getUserManager().getByName(args[0]);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                if(user != null) {
                    Ban ban = null;
                    if(user.isIPBanned())
                        ban = CommonModule.getInstance().getManager().getBanManager().getBan(user.getIP());
                    else if(user.isNameBanned())
                        ban = CommonModule.getInstance().getManager().getBanManager().getBan(user.getKey().toString());
                    net.luckperms.api.model.user.User luckpermsUser = CommonModule.getInstance().getLuckPerms().getUserManager().getUser(user.getKey());
                    Group luckpermsGroup = CommonModule.getInstance().getLuckPerms().getGroupManager().getGroup(luckpermsUser.getPrimaryGroup());
                    ChatColor color = ChatColor.of(luckpermsGroup.getCachedData().getMetaData().getPrefix());
                    s.sendMessage(TextComponent.fromLegacyText(Language.get("check-player-info", user.getName(),
                            user.getKey().toString(),
                            user.getIP(),
                            "§8[" + color + luckpermsGroup.getDisplayName() + "§8] " + color + luckpermsGroup.getCachedData().getMetaData().getSuffix(),
                            sdf.format(new Date(user.getLastJoin()))
                    )));
                    if(ban != null) {
                        s.sendMessage(TextComponent.fromLegacyText(Language.get("check-ban-info",
                                ban.getType().toString(),
                                ban.getReadableID(),
                                sdf.format(new Date(ban.getTimestamp())),
                                ban.getDuration() > 0 ? CommonModule.getInstance().getManager().getBanManager().getReadableDuration(ban.getDuration(), ban.getTimestamp()) : "Permanent",
                                ban.getIssuer().getName(),
                                ban.getReason())));
                    } else {
                        s.sendMessage(TextComponent.fromLegacyText(Language.get("check-no-ban")));
                    }
                } else {
                    if(CommonModule.getInstance().getManager().getBanManager().isValidIP(args[0])) {
                        List<User> ipUsers = CommonModule.getInstance().getManager().getUserManager().getByIP(args[0]);
                        s.sendMessage(TextComponent.fromLegacyText(Language.get("check-ip-info",
                                args[0],
                                ipUsers.stream()
                                        .map(ipUser -> {
                                    net.luckperms.api.model.user.User luckpermsUser = CommonModule.getInstance().getLuckPerms().getUserManager().getUser(ipUser.getKey());
                                    Group luckpermsGroup = CommonModule.getInstance().getLuckPerms().getGroupManager().getGroup(luckpermsUser.getPrimaryGroup());
                                    ChatColor color = ChatColor.of(luckpermsGroup.getCachedData().getMetaData().getPrefix());
                                    return "§8[" + color + luckpermsGroup.getDisplayName() + "§8] " + color + ipUser.getName();
                                        }).collect(Collectors.joining("§7, ")))));

                        List<Ban> ipBans = CommonModule.getInstance().getManager().getBanManager().getActiveBans(args[0]);
                        if(ipBans.size() > 0) {
                            for(Ban ipBan : ipBans) {
                                s.sendMessage(TextComponent.fromLegacyText(Language.get("check-ban-info",
                                        ipBan.getType().toString(),
                                        ipBan.getReadableID(),
                                        sdf.format(new Date(ipBan.getTimestamp())),
                                        ipBan.getDuration() > 0 ? CommonModule.getInstance().getManager().getBanManager().getReadableDuration(ipBan.getDuration(), ipBan.getTimestamp()) : "Permanent",
                                        ipBan.getIssuer().getName(),
                                        ipBan.getReason())));
                            }
                        } else {
                            s.sendMessage(TextComponent.fromLegacyText(Language.get("check-no-ban")));
                        }
                    } else {
                        long id;
                        try {
                            id = Long.parseLong(args[0]);
                        } catch (NumberFormatException ignored) {
                            id = CommonModule.getInstance().getManager().getBanManager().getBanByID(args[0]);
                        }
                        if(id >= 0) {
                            Ban ban = CommonModule.getInstance().getManager().getBanManager().get(id);
                            if(ban != null) {
                                String banned = "";
                                switch (ban.getType()) {
                                    case IP_BAN_TEMP, IP_BAN -> banned = ban.getBanned();
                                    case PLAYER_BAN, PLAYER_BAN_TEMP -> banned = CommonModule.getInstance().getManager().getUserManager().get(UUID.fromString(ban.getBanned())).getName();
                                }
                                s.sendMessage(TextComponent.fromLegacyText(Language.get("check-ban-info-id",
                                        ban.getReadableID(),
                                        ban.isActive() ? "§aAktiv" : "§cNicht aktiv",
                                        banned,
                                        ban.getType().toString(),
                                        sdf.format(new Date(ban.getTimestamp())),
                                        ban.isActive() ?
                                                ban.getDuration() > 0 ? CommonModule.getInstance().getManager().getBanManager().getReadableDuration(ban.getDuration(), ban.getTimestamp()) : "Permanent" :
                                                "Nicht aktiv",
                                        ban.getIssuer().getName(),
                                        ban.getReason())));
                            } else {
                                s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("check-not-found")));
                            }
                        } else {
                            s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("check-not-found")));
                        }
                    }
                }
            } else {
                s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-help")));
            }
        } else {
            s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("no-permission", "evergreenproxy.check")));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();
        if(args.length == 1) {
            if(sender.hasPermission("evergreenproxy.check")) {
                String search = args[0].toLowerCase();
                for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                    if (pp.getName().toLowerCase().startsWith(search))
                        matches.add(pp.getName());
                }
                for(Ban ban : CommonModule.getInstance().getManager().getBanManager().getAllBans()) {
                    if(ban.getReadableID().toLowerCase().startsWith(search))
                        matches.add(ban.getReadableID());
                }
            }
        }
        return matches;
    }
}
