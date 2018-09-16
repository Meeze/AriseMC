package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.model.Ranking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandRanking implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			Main.getRankingManager().getRanking(Ranking.KILLS, new Callback<Map<String, Long>>() {
				@Override
				public void onResult(Map<String, Long> type) {
					int place = 1;
					player.sendMessage("Platz | Spieler | Kills");
					for (Map.Entry<String, Long> entry : type.entrySet()) {
						player.sendMessage(place + ". " + entry.getKey() + " | " + entry.getValue());
						place++;
					}
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage("Beim laden der Statistiken ist ein Fehler aufgetreten!");
				}
			});
		}
		if (args.length == 1) {
			Ranking ranking = Ranking.valueOf(args[0].toUpperCase());
			if (ranking == null) {
				player.sendMessage("/ranking kills, deaths, money");
				return true;
			}
			Main.getRankingManager().getRanking(ranking, new Callback<Map<String, Long>>() {
				@Override
				public void onResult(Map<String, Long> type) {
					int place = 1;
					player.sendMessage(String.format("Platz | Spieler | %s", ranking.getName()));
					for (Map.Entry<String, Long> entry : type.entrySet()) {
						player.sendMessage(place + ". " + entry.getKey() + " | " + entry.getValue());
						place++;
					}
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage("Beim laden der Statistiken ist ein Fehler aufgetreten!");
				}
			});
		}
		return true;
	}
}
