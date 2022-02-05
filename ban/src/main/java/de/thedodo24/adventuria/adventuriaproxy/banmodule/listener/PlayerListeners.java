package de.thedodo24.adventuria.adventuriaproxy.banmodule.listener;

import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.Ban;
import de.thedodo24.adventuria.adventuriaproxy.common.player.User;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.logging.Level;


public class PlayerListeners implements Listener {

    private final String prefix = "§7§l| §cBan §7» ";

    @EventHandler
    public void onJoin(LoginEvent e) {
        User user = CommonModule.getInstance().getManager().getUserManager().getOrGenerate(e.getConnection().getUniqueId());


        Ban ban = null;
        if(user.isIPBanned())
            ban = CommonModule.getInstance().getManager().getBanManager().getBan(user.getIP());
        else if(user.isNameBanned())
            ban = CommonModule.getInstance().getManager().getBanManager().getBan(user.getKey().toString());
        if(ban != null) {
            if(ban.isTempBan()) {
                long end = ban.getTimestamp() + ban.getDuration();
                if(System.currentTimeMillis() >= end) {
                    boolean ipban = ban.isIPBan();
                    ban.setActive(false);
                    for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                        if(all.hasPermission("evergreenproxy.ban.notify"))
                            all.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-expired", ipban ? user.getIP() : user.getName())));
                    }
                } else {
                    e.setCancelReason(TextComponent.fromLegacyText(Language.get("ban-kick-message-temp", ban.getReason(), ban.getReadableID(),
                            CommonModule.getInstance().getManager().getBanManager().getReadableDuration(ban.getDuration(), ban.getTimestamp()))));
                    e.setCancelled(true);
                }
            } else {
                e.setCancelReason(TextComponent.fromLegacyText(Language.get("ban-kick-message", ban.getReason(), ban.getReadableID())));
                e.setCancelled(true);
            }
        }
    }

}
