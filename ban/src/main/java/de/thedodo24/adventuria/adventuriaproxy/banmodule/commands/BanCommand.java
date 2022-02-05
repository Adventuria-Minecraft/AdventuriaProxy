package de.thedodo24.adventuria.adventuriaproxy.banmodule.commands;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.thedodo24.adventuria.adventuriaproxy.banmodule.BanModule;
import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.Ban;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.BanType;
import de.thedodo24.adventuria.adventuriaproxy.common.player.User;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.apache.commons.codec.language.bm.Lang;
import org.shanerx.mojang.Mojang;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class BanCommand extends Command implements TabExecutor {


    public BanCommand(String name) {
        super(name);

    }

    /*
        ban [Spieler] [Grund]
        ban [IP] [Grund]
        ban [Spieler] [Dauer] [Grund]
        ban [IP] [Dauer] [Grund]
        check [Spieler/IP]
        unban [Spieler/IP]

     */

    private final String prefix = "§7§l| §cBann §7» ";


    @Override
    public void execute(CommandSender s, String[] args) {
        if (args.length == 0 || args.length == 1) {
            if(s.hasPermission("evergreenproxy.ban.notify"))
                sendHelpMessage(s);
            else
                s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("no-permission", "evergreenproxy.ban.notify")));
        } else {
            if(s.hasPermission("evergreenproxy.ban.temp") || s.hasPermission("evergreenproxy.ban.perm")) {
                User user = CommonModule.getInstance().getManager().getUserManager().getByName(args[0]);
                String banable;
                BanType type = BanType.PLAYER_BAN;
                if (user == null) {
                    if (CommonModule.getInstance().getManager().getBanManager().isValidIP(args[0])) {
                        if (CommonModule.getInstance().getManager().getBanManager().isIPBanned(args[0])) {
                            s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-already-ip-banned", args[0], "Unbekannt")));
                            return;
                        }
                        banable = args[0];
                        type = BanType.IP_BAN;
                    } else {
                        String mojangUUID;
                        try {
                            mojangUUID = BanModule.getInstance().getMojangApi().getUUIDOfUsername(args[0]);
                        } catch (Exception e) {
                            s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-not-found")));
                            return;
                        }
                        if (mojangUUID != null && !mojangUUID.isEmpty()) {
                            args[0] = BanModule.getInstance().getMojangApi().getPlayerProfile(mojangUUID).getUsername();
                            banable = UUIDUtils.fromTrimmed(mojangUUID).toString();
                            user = CommonModule.getInstance().getManager().getUserManager().getOrGenerate(UUID.fromString(banable));
                            user.setName(args[0]);
                        } else {
                            s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-not-found")));
                            return;
                        }
                    }
                } else {
                    if (user.isNameBanned()) {
                        s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-already-banned", user.getName())));
                        return;
                    }
                    banable = user.getKey().toString();
                }
                long id = CommonModule.getInstance().getManager().getBanManager().getHighestID() + 1;
                User issuer = CommonModule.getInstance().getConsoleUser();
                if (s instanceof ProxiedPlayer p)
                    issuer = CommonModule.getInstance().getManager().getUserManager().get(p.getUniqueId());
                long duration = -1;
                List<String> argList = Arrays.asList(args);
                String reason = argList.stream().skip(1).collect(Collectors.joining(" "));
                if (args.length >= 3) {
                    String dur = args[1];

                    char[] durArray = dur.toCharArray();
                    int index = 0;
                    for (char x : durArray) {
                        if (Character.isLetter(x))
                            index = dur.indexOf(x);
                    }
                    String durString = dur.substring(0, index);
                    String einheit = dur.substring(index);
                    long durationLong;
                    try {
                        durationLong = Long.parseLong(durString);
                        if (!einheit.isEmpty()) {
                            switch (einheit) {
                                case "s" -> durationLong *= 1000;
                                case "m" -> durationLong *= 1000 * 60;
                                case "h" -> durationLong *= 1000 * 60 * 60;
                                case "d" -> durationLong *= 1000 * 60 * 60 * 24;
                                case "M" -> durationLong *= 1000L * 60 * 60 * 24 * 30;
                                case "y" -> durationLong *= 1000L * 60 * 60 * 24 * 365;
                                default -> throw new NumberFormatException();
                            }
                            duration = durationLong;
                            switch (type) {
                                case PLAYER_BAN -> type = BanType.PLAYER_BAN_TEMP;
                                case IP_BAN -> type = BanType.IP_BAN_TEMP;
                            }
                            reason = argList.stream().skip(2).collect(Collectors.joining(" "));
                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                User finalIssuer = issuer;
                String finalBanable = banable;
                long finalDuration = duration;
                BanType finalType = type;
                String finalReason = reason;
                if(finalDuration > 0 && !s.hasPermission("evergreenproxy.ban.perm")) {
                    s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("no-permission", "evergreenproxy.ban.perm")));
                    return;
                } else if(finalDuration < 0 && !s.hasPermission("evergreenproxy.ban.temp")) {
                    s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("no-permission", "evergreenproxy.ban.temp")));
                    return;
                }
                Ban ban = CommonModule.getInstance().getManager().getBanManager().getOrGenerate(id, (key ->
                        new Ban(key,
                                finalIssuer,
                                finalReason,
                                finalType,
                                finalBanable,
                                finalDuration,
                                System.currentTimeMillis())));
                CommonModule.getInstance().getManager().getBanManager().save(ban);
                if (!(s instanceof ProxiedPlayer))
                    s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-console-message", finalBanable)));
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    if (p.hasPermission("evergreenproxy.ban.notify"))
                        p.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-notify",
                                user != null ? user.getName() : finalBanable,
                                s.getName(),
                                reason,
                                type.getBanned(),
                                type.getDuration(),
                                (finalDuration <= 0) ? "Unbegrenzt" : CommonModule.getInstance().getManager().getBanManager().getReadableDuration(finalDuration, System.currentTimeMillis()),
                                ban.getReadableID())));
                }
                switch (type) {
                    case PLAYER_BAN, PLAYER_BAN_TEMP -> {
                        ProxiedPlayer p;
                        if ((p = ProxyServer.getInstance().getPlayer(user.getKey())) != null) {
                            if (finalDuration <= 0)
                                p.disconnect(TextComponent.fromLegacyText(Language.get("ban-kick-message", ban.getReason(),
                                        ban.getReadableID())));
                            else
                                p.disconnect(TextComponent.fromLegacyText(Language.get("ban-kick-message-temp", ban.getReason(),
                                        ban.getReadableID(),
                                        CommonModule.getInstance().getManager().getBanManager().getReadableDuration(ban.getDuration(), System.currentTimeMillis()))));
                        }
                    }
                    case IP_BAN, IP_BAN_TEMP -> ProxyServer.getInstance().getPlayers().stream()
                            .filter(p -> ((InetSocketAddress) p.getSocketAddress()).getAddress().getHostAddress().equalsIgnoreCase(finalBanable))
                            .forEach(p -> {
                                if (finalDuration <= 0)
                                    p.disconnect(TextComponent.fromLegacyText(Language.get("ban-kick-message", ban.getReason(),
                                            ban.getReadableID())));
                                else
                                    p.disconnect(TextComponent.fromLegacyText(Language.get("ban-kick-message-temp", ban.getReason(),
                                            ban.getReadableID(),
                                            CommonModule.getInstance().getManager().getBanManager().getReadableDuration(ban.getDuration(), System.currentTimeMillis()))));
                            });
                }
            } else {
                s.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("no-permission", "evergreenproxy.ban")));
            }
        }
    }

    private void sendHelpMessage(CommandSender s) {
        if (s instanceof ProxiedPlayer)
            s.sendMessage(TextComponent.fromLegacyText(Language.get("ban-help")));
        else
            s.sendMessage(TextComponent.fromLegacyText(Language.get("ban-help-console")));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("evergreenproxy.ban.notify")) {
            Set<String> matches = new HashSet<>();
            if (args.length == 1) {
                String search = args[0].toLowerCase();
                for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                    if (pp.getName().toLowerCase().startsWith(search))
                        matches.add(pp.getName());
                }
            } else if (args.length == 2) {
                String search = args[1].toLowerCase();
                if (!search.isEmpty()) {
                    boolean autocomplete = true;
                    for (char x : search.toCharArray()) {
                        if (Character.isLetter(x))
                            autocomplete = false;
                    }
                    if (autocomplete)
                        matches.addAll(Lists.newArrayList("s", "m", "h", "d", "M", "y"));
                }
            } else {
                return ImmutableSet.of();
            }
            return matches;
        }
        return ImmutableSet.of();
    }
}
