package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.User;
import de.hardcorepvp.manager.PunishmentManager;
import de.hardcorepvp.manager.UUIDManager;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTempmute implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("system.tempmute")) {
			player.sendMessage(Messages.NO_PERMISSIONS);
			return true;
		}
		if (args.length <= 1) {
			//10s = 10 Sekunden
			//10m = 10 Minuten
			//10h = 10 Stunden
			//10d = 10 Tage
			//10mo = 10 Monate
			//10y = 10 Jahre
			//Example 10d10m
			player.sendMessage("§cVerwendung: §b/tempmute <Spieler> <Zeit> <Grund>");
			player.sendMessage("§cZeitformat: y, mo, d, h, m, s");
			return true;
		}
		if (args.length >= 2) {
			long muteTime = Utils.parseDateDiff(args[1], true);
			if (muteTime == -1L) {
				player.sendMessage("§cUngültiges Zeitformat!");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			for (int reason = 2; reason < args.length; ++reason) {
				builder.append(args[reason]).append(" ");
			}
			String reason = (builder.length() == 0) ? "--" : builder.substring(0, builder.length() - 1);
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null) {
				if (player.equals(target)) {
					player.sendMessage("§cDu kannst dich nicht selber muten!");
					return true;
				}
				if (target.hasPermission("system.tempmute.ignore")) {
					player.sendMessage("§cDu kannst diesen Spieler nicht muten!");
					return true;
				}
				Main.getPunishmentManager().getMuteData(target.getUniqueId(), new Callback<PunishmentManager.MuteData>() {
					@Override
					public void onResult(PunishmentManager.MuteData type) {
						if (type != null) {
							player.sendMessage("§cDer Spieler ist bereits gemuted!");
							return;
						}
						PunishmentManager.MuteData muteData = new PunishmentManager.MuteData(target.getUniqueId(), reason, player.getName(), muteTime, System.currentTimeMillis());
						boolean success = Main.getPunishmentManager().setMuted(muteData);
						if (!success) {
							player.sendMessage("§cDer Spieler konnte nicht gemuted werden!");
							return;
						}
						User user = Main.getUserManager().getUser(target.getUniqueId());
						user.setMuteData(muteData);
						target.sendMessage("§b█▀▀▀▀▀▀▀▀▀▀ §6§lMute§r §b▀▀▀▀▀▀▀▀▀▀█");
						target.sendMessage("§b▌");
						target.sendMessage("§b▌ §7Du wurdest §ctemporär §7gemuted.");
						target.sendMessage("§b▌ ");
						target.sendMessage("§b▌ §6Von: §e" + muteData.getMutedBy());
						target.sendMessage("§b▌ §6Grund: §e" + muteData.getMuteReason());
						target.sendMessage("§b▌ §6Zeitpunkt: §e" + Utils.formatDate(muteData.getMuteTimestamp()));
						target.sendMessage("§b▌ §6Dein Mute läuft bis: §e" + Utils.formatDate(muteTime));
						target.sendMessage("§b▌ ");
						target.sendMessage("§b▌ §7Falls dies ein Fehlmute war, klicke §6*hier*");
						target.sendMessage("§b█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█");
						Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§9Der Spieler §7" + args[0] + " §9wurde von §e" + muteData.getMutedBy() + " §9temporär gemuted."));
						Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§9Grund: §b" + muteData.getMuteReason()));
					}

					@Override
					public void onFailure(Throwable cause) {
						player.sendMessage("§cDer Spieler konnte nicht gemuted werden!");
					}
				});
				return true;
			}
			UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
				@Override
				public void onResult(UUIDManager.ProfileHolder profile) {
					if (Main.getPunishmentManager().getMuteData(profile.getUniqueId()) != null) {
						player.sendMessage("§cDer Spieler ist bereits gemuted!");
						return;
					}
					PunishmentManager.MuteData muteData = new PunishmentManager.MuteData(profile.getUniqueId(), reason, player.getName(), muteTime, System.currentTimeMillis());
					boolean success = Main.getPunishmentManager().setMuted(muteData);
					if (!success) {
						player.sendMessage("§cDer Spieler konnte nicht gemuted werden!");
						return;
					}
					Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§9Der Spieler §7" + args[0] + " §9wurde von §e" + muteData.getMutedBy() + " §9temporär gemuted."));
					Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§9Grund: §b" + muteData.getMuteReason()));
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage("§cDieser Spieler existiert nicht!");
				}
			});
		}
		return true;
	}
}
