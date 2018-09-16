package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.ShopItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSell implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			int moneyGained = this.sellInventory(player);
			if (moneyGained == 0) {
				player.sendMessage("In deinem Inventar befinden sich keine Items, die verkauft werden können.");
				return true;
			}
			player.sendMessage(Messages.formatMessage("Du hast dein Inventar verkauft und " + moneyGained + " bekommen."));
			return true;
		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}

	private int sellInventory(Player player) {
		int amount = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			ShopItems shopItems = ShopItems.valueOf(item.getType().name());
			if (shopItems != null) {
				amount += item.getAmount() * shopItems.getSellPrice();
				player.getInventory().remove(item);
			}
		}
		return amount;
	}
}