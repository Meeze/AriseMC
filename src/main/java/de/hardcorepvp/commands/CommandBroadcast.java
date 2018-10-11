package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBroadcast implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }
    Player player = (Player) sender;
    if (args.length > 0) {
      String broadcastMessage = Messages.BROADCAST;
      for (int i = 0; i < args.length; i++) {
        broadcastMessage += " " + args[i];
      }
      Bukkit.broadcastMessage(broadcastMessage);
      return true;
    }
    player.sendMessage(Messages.formatMessage(Messages.TOO_LESS_ARGUMENTS));
    return true;
  }
}