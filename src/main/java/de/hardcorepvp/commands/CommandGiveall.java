package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGiveall implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			if (player.getItemInHand() == null) {
				player.sendMessage(Messages.formatMessage("Du musst ein Item in der Hand halten"));
				return true;
			}

			ItemStack item = player.getItemInHand();
			for (Player givePlayer : Bukkit.getOnlinePlayers()) {
				givePlayer.getInventory().addItem(item);
			}
			Bukkit.broadcastMessage(Messages.formatMessage("Jedem Spieler wurde " + item.getAmount() + "x " + item.getType() + " gegeben"));
			return true;
		}

		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}

}
