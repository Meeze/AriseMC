package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.manager.PunishmentManager;
import de.hardcorepvp.manager.UUIDManager;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnban implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("system.unban")) {
			player.sendMessage(Messages.NO_PERMISSIONS);
			return true;
		}
		if (args.length == 0 || args.length > 1) {
			player.sendMessage("§cVerwendung: §b/unban <Spieler>");
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase(player.getName())) {
				player.sendMessage("§cVerwendung: §b/unban <Spieler>");
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null) {
				Main.getPunishmentManager().getBanData(target.getUniqueId(), new Callback<PunishmentManager.BanData>() {
					@Override
					public void onResult(PunishmentManager.BanData type) {
						if (type == null) {
							player.sendMessage("§cDer Spieler ist nicht gebannt.");
							return;
						}
						Main.getPunishmentManager().unban(target.getUniqueId());
						for (Player all : Bukkit.getOnlinePlayers()) {
							if (all.hasPermission("system.unban") && !all.equals(player)) {
								all.sendMessage(Messages.formatMessage(Messages.PREFIX + "§6" + player.getName() + " §chat den Spieler §e" + args[0] + " §centmuted."));
							}
						}
						player.sendMessage("§cDer Spieler wurde entbannt.");
					}

					@Override
					public void onFailure(Throwable cause) {
						player.sendMessage("§cDer Spieler konnte nicht entbannt werden!");
					}
				});
				return true;
			}
			UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
				@Override
				public void onResult(UUIDManager.ProfileHolder profile) {
					PunishmentManager.BanData banData = Main.getPunishmentManager().getBanData(profile.getUniqueId());
					if (banData == null) {
						player.sendMessage("§cDer Spieler ist nicht gebannt.");
						return;
					}
					boolean success = Main.getPunishmentManager().unban(profile.getUniqueId());
					if (!success) {
						player.sendMessage("§cDer Spieler konnte nicht entbannt werden!");
						return;
					}
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.hasPermission("system.unban") && !all.equals(player)) {
							all.sendMessage(Messages.formatMessage(Messages.PREFIX + "§6" + player.getName() + " §chat den Spieler §e" + args[0] + " §centbannt."));
						}
					}
					player.sendMessage("§cDer Spieler wurde entbannt.");
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage("§cDer Spieler konnte nicht entbannt werden.");
				}
			});
		}
		return true;
	}
}
