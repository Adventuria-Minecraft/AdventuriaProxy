package de.thedodo24.adventuria.adventuriaproxy.banmodule.commands;

import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.Ban;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.BanType;
import de.thedodo24.adventuria.adventuriaproxy.common.player.User;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UnbanCommand extends Command implements TabExecutor {

    public UnbanCommand(String unban) {
        super(unban);
    }

    private final String prefix = "§7§l| §cBann §7» ";

    @Override
    public void execute(CommandSender s, String[] args) {
        if(s.hasPermission("evergreenproxy.unban")) {
            if(args.length == 1) {
                User user = CommonModule.getInstance().getManager().getUserManager().getByName(args[0]);
                String banable;
                if(user != null) {
                    banable = user.getKey().toString();
                } else if(CommonModule.getInstance().getManager().getBanManager().isValidIP(args[0])) {
                    banable = args[0];
                } else {
                    long id;
                    try {
                        id = Long.parseLong(args[0]);
                    } catch (NumberFormatException e) {
                        id = CommonModule.getInstance().getManager().getBanManager().getBanByID(args[0]);
                    }
                    if(id >= 0) {
                        Ban ban = CommonModule.getInstance().getManager().getBanManager().get(id);
                        if(ban != null) {
                            ban.setActive(false);
                            String nBanable = args[0];
                            User banned = CommonModule.getInstance().getManager().getUserManager().get(UUID.fromString(ban.getBanned()));
                            if(banned != null)
                                nBanable = banned.getName();
                            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                                if(p.hasPermission("evergreenproxy.ban.notify"))
                                    p.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("unban-notify", nBanable, s.getName(), ban.getReadableID())));
                            }
                        } else {
                            s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-not-found")));
                        }
                    } else {
                        s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-not-found")));
                    }
                    return;
                }
                List<Ban> bans = CommonModule.getInstance().getManager().getBanManager().getActiveBans(banable);
                if(bans.size() > 0) {
                    String id = "";
                    for(Ban ban : bans) {
                        ban.setActive(false);
                        id = ban.getReadableID();
                    }
                    for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if(p.hasPermission("evergreenproxy.ban.notify"))
                            p.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("unban-notify", user != null ? user.getName() : banable, s.getName(), id)));
                    }
                } else {
                    s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-not-found")));
                }
            } else {
                s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-help")));
            }
        } else {
            s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("no-permission", "evergreenproxy.unban")));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();
        if(args.length == 1) {
            if(sender.hasPermission("evergreenproxy.check")) {
                String search = args[0].toLowerCase();
                for(Ban ban : CommonModule.getInstance().getManager().getBanManager().getAllBans()) {
                    if(ban.getReadableID().toLowerCase().startsWith(search) && ban.isActive())
                        matches.add(ban.getReadableID());
                    if(ban.getBanned().toLowerCase().startsWith(search) && (ban.getType().equals(BanType.IP_BAN) || ban.getType().equals(BanType.IP_BAN_TEMP)) && ban.isActive())
                        matches.add(ban.getBanned());
                    User banned;
                    if((ban.getType().equals(BanType.PLAYER_BAN) || ban.getType().equals(BanType.PLAYER_BAN_TEMP)) && (banned = CommonModule.getInstance().getManager().getUserManager().get(UUID.fromString(ban.getBanned()))) != null && ban.isActive()) {
                        if(banned.getName().toLowerCase().startsWith(search))
                            matches.add(banned.getName());
                    }
                }
            }
        }
        return matches;
    }
}
