package de.hardcorepvp.listener;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import de.hardcorepvp.clan.ClanMember;
import de.hardcorepvp.clan.ClanRank;
import de.hardcorepvp.data.User;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		User user = Main.getUserManager().getUser(player.getUniqueId());
		String message = event.getMessage();
		//TODO Prefix, Clan, Suffix
		//%1$s -> Spieler
		//%2$s -> Nachricht
		if (user.getMuteData() != null) {
			if (user.getMuteData().getMuteTime() != -1L && System.currentTimeMillis() >= user.getMuteData().getMuteTime()) {
				Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> Main.getPunishmentManager().unmute(player.getUniqueId()));
				user.setMuteData(null);
			}
		}
		if (Main.getClanManager().hasClan(player.getUniqueId())) {
			Clan clan = Main.getClanManager().getClan(player.getUniqueId());
			ClanMember member = Main.getClanManager().getMember(player.getUniqueId());
			if (message.startsWith("#")) {
				event.setCancelled(true);
				message = message.substring(1).trim();
				if (member.getRank() == ClanRank.OWNER) {
					clan.broadcast("§6[Clan-Chat] §c" + player.getName() + "§7: §o" + message);
					return;
				}
				if (member.getRank() == ClanRank.TRUSTED) {
					clan.broadcast("§6[Clan-Chat] §3" + player.getName() + "§7: §o" + message);
					return;
				}
				clan.broadcast("§6[Clan-Chat] §7" + player.getName() + "§7: §o" + message);
				return;
			}
			if (user.getMuteData() == null) {
				event.setFormat(ChatColor.translateAlternateColorCodes('&', user.getGroup().getPrefix()) + " " + clan.getTagColor() + clan.getTag() + "§7*" + "%1$s§7: §e%2$s");
				return;
			}
			this.sendMuteMessage(player, user);
			event.setCancelled(true);
			return;
		}
		if (user.getMuteData() == null) {
			event.setFormat(ChatColor.translateAlternateColorCodes('&', user.getGroup().getPrefix()) + " %1$s§7: §e%2$s");
			return;
		}
		this.sendMuteMessage(player, user);
		event.setCancelled(true);
	}

	private void sendMuteMessage(Player player, User user) {
		//TODO link einbauen
		player.sendMessage("§b█▀▀▀▀▀▀▀▀▀▀ §6§lMute§r §b▀▀▀▀▀▀▀▀▀▀█");
		player.sendMessage("§b▌");
		player.sendMessage("§b▌ §7Du wurdest " + (user.getMuteData().isMuted() ? "§4permanent" : "§ctemporär") + " §7gemuted.");
		player.sendMessage("§b▌ ");
		player.sendMessage("§b▌ §6Von: §e" + user.getMuteData().getMutedBy());
		player.sendMessage("§b▌ §6Grund: §e" + user.getMuteData().getMuteReason());
		player.sendMessage("§b▌ §6Zeitpunkt: §e" + Utils.formatDate(user.getMuteData().getMuteTimestamp()));
		if (user.getMuteData().isTemporarilyMuted()) {
			player.sendMessage("§b▌ §6Dein Mute läuft bis: §e" + Utils.formatDate(user.getMuteData().getMuteTime()));
		}
		player.sendMessage("§b▌");
		player.sendMessage("§b▌ §7Falls dies ein Fehlmute war, klicke §6*hier*");
		player.sendMessage("§b█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█");
	}
}
