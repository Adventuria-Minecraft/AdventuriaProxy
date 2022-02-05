package de.thedodo24.adventuria.adventuriaproxy.common.listener;

import de.thedodo24.adventuria.adventuriaproxy.common.CommonModule;
import de.thedodo24.adventuria.adventuriaproxy.common.ban.Ban;
import de.thedodo24.adventuria.adventuriaproxy.common.player.User;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.Language;
import de.thedodo24.adventuria.adventuriaproxy.common.utils.TabList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.score.*;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.logging.Level;

public class PlayerListener implements Listener {

    private final String prefix = "§7§l| §cBann §7» ";


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(LoginEvent e) {
        User u = CommonModule.getInstance().getManager().getUserManager().getOrGenerate(e.getConnection().getUniqueId());
        u.setLastJoin(System.currentTimeMillis());
        u.setName(e.getConnection().getName());

        String actualIP = ((InetSocketAddress) e.getConnection().getSocketAddress()).getAddress().getHostAddress();
        if(!u.isSetProperty("ip")) {
            u.setIP(actualIP);
        }
        String oldIP = u.getIP();
        if(!actualIP.equalsIgnoreCase(oldIP)) {
            if(u.isIPBanned()) {
                Ban oldBan = CommonModule.getInstance().getManager().getBanManager().getBan(oldIP);
                u.setIP(actualIP);
                Ban newBan = CommonModule.getInstance().getManager().getBanManager().getOrGenerate(
                        CommonModule.getInstance().getManager().getBanManager().getHighestID() + 1,
                        key -> new Ban(
                                key,
                                oldBan.getIssuer(),
                                oldBan.getReason(),
                                oldBan.getType(),
                                actualIP,
                                oldBan.getDuration(),
                                oldBan.getTimestamp()
                        )
                );
                CommonModule.getInstance().getManager().getBanManager().save(newBan);
                CommonModule.getInstance().getPlugin().getLogger().log(Level.INFO, Language.get("ban-new-ip", oldIP, actualIP, newBan.getReadableID()));
                for(ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                    if(pp.hasPermission("evergreenproxy.ban.notify")) {
                        pp.sendMessage(TextComponent.fromLegacyText(prefix + Language.get("ban-new-ip", oldIP, actualIP, newBan.getReadableID())));
                    }
                }
                if(newBan.isTempBan()) {
                    e.setCancelReason(TextComponent.fromLegacyText(Language.get("ban-kick-message-temp")));
                } else {
                    e.setCancelReason(TextComponent.fromLegacyText(Language.get("ban-kick-message")));
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        User u = CommonModule.getInstance().getManager().getUserManager().get(p.getUniqueId());
        CommonModule.getInstance().getManager().getUserManager().save(u);
    }

}
