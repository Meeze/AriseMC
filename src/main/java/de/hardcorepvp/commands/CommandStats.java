package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.UserStats;
import de.hardcorepvp.manager.UUIDManager;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandStats implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (args.length > 2) {
			player.sendMessage("/stats <Spieler>");
			return true;
		}
		if (args.length == 0) {
			this.sendStats(player, player.getUniqueId(), player.getName());
			return true;
		}
		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null) {
				this.sendStats(player, target.getUniqueId(), target.getName());
				return true;
			}
			UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
				@Override
				public void onResult(UUIDManager.ProfileHolder profile) {
					sendStats(player, profile.getUniqueId(), profile.getName());
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage(Messages.PLAYER_DOESNT_EXIST);
				}
			});
		}
		return true;
	}

	private void sendStats(Player executor, UUID targetUniqueId, String targetName) {
		Main.getStatsManager().getUserStats(targetUniqueId, new Callback<UserStats>() {
			@Override
			public void onResult(UserStats stats) {
				executor.sendMessage("Stats von " + targetName);
				executor.sendMessage("Kills " + stats.getKills());
				executor.sendMessage("Deaths " + stats.getDeaths());
				executor.sendMessage("K/D " + stats.getKD());
			}

			@Override
			public void onFailure(Throwable cause) {

			}
		});
	}
}
