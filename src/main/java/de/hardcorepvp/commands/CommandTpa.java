package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTpa implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;

        }

        Player player = (Player) sender;

        int cooldown = Utils.tpaCooldown;
        if (handleCooldown(player, cooldown)) return true;
        if (args.length == 0) {
            player.sendMessage(Messages.TOO_LESS_ARGUMENTS);
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            long keepAlive = 60 * 20L;
            if (target == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if (target == player) {
                player.sendMessage("Nicht zu dir selbst");
                return true;
            }
            Main.getManager().sendTpaRequest(player, target);
            player.sendMessage(Messages.formatMessage("Du hast " + target.getName() + " eine TPA Anfrage gesendet"));
            target.sendMessage(Messages.formatMessage(player.getName() + " hat dir eine TPA Anfrage gesendet"));
            Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> Main.getManager().killTpaRequest(target.getName()), keepAlive);
            Main.getManager().getTpaCooldown().put(player.getName(), System.currentTimeMillis());
            return true;
        }
        player.sendMessage(Messages.TOO_MANY_ARGUMENTS);
        return true;
    }

    static boolean handleCooldown(Player player, int cooldown) {
        if (Main.getManager().getTpaCooldown().containsKey(player.getName())) {
            long diff = (System.currentTimeMillis() - (Main.getManager().getTpaCooldown().get(player.getName()))) / 1000L;
            if (diff < cooldown) {
                player.sendMessage("Du musst " + cooldown + "Sekunden zwischen Tpas warten!");
                return true;
            }
        }
        return false;
    }

}
