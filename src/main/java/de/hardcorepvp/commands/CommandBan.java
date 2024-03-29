package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
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

public class CommandBan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("system.ban")) {
            player.sendMessage(Messages.NO_PERMISSIONS);
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("§cVerwendung: §b/ban <Spieler> <Grund>");
            return true;
        }
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (int reason = 1; reason < args.length; ++reason) {
                builder.append(args[reason]).append(" ");
            }
            String reason = (builder.length() == 0) ? "--" : builder.substring(0, builder.length() - 1);
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if (player.equals(target)) {
                    player.sendMessage("§cDu kannst dich nicht selber bannen!");
                    return true;
                }
                if (target.hasPermission("system.ban.ignore")) {
                    player.sendMessage("§cDu kannst diesen Spieler nicht bannen!");
                    return true;
                }
                Main.getPunishmentManager().getBanData(target.getUniqueId(), new Callback<PunishmentManager.BanData>() {
                    @Override
                    public void onResult(PunishmentManager.BanData type) {
                        if (type != null) {
                            player.sendMessage("§cDer Spieler ist bereits gebannt!");
                            return;
                        }
                        PunishmentManager.BanData banData = new PunishmentManager.BanData(target.getUniqueId(), reason, player.getName(), -1L, System.currentTimeMillis());
                        boolean success = Main.getPunishmentManager().setBanned(banData);
                        if (!success) {
                            player.sendMessage("§cDer Spieler konnte nicht gebannt werden!");
                            return;
                        }
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> target.kickPlayer("§b▄▆█  §6§lAriseMC.de  §b█▆▄\n\n" +
                                "§7Du wurdest §4PERMANENT §7gebannt!\n\n" +
                                "§7Von: §e" + banData.getBannedBy() + "\n\n" +
                                "§7Grund: §e" + banData.getBanReason() + "\n\n" +
                                "§7Zeitpunkt: §e" + Utils.formatDate(banData.getBanTimestamp()) + "\n\n" +
                                "§7Du kannst auf §ehttp://arisemc.de/forum §7einen Entbannungsantrag stellen!" + "\n\n"));
                        Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§cDer Spieler §7" + args[0] + " §cwurde von §e" + banData.getBannedBy() + " §cpermanent gebannt."));
                        Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§cGrund: §7" + banData.getBanReason()));
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        player.sendMessage("§cDer Spieler konnte nicht gebannt werden!");
                    }
                });
                return true;
            }
            UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
                @Override
                public void onResult(UUIDManager.ProfileHolder profile) {
                    if (Main.getPunishmentManager().getBanData(profile.getUniqueId()) != null) {
                        player.sendMessage("§cDer Spieler ist bereits gebannt!");
                        return;
                    }
                    PunishmentManager.BanData banData = new PunishmentManager.BanData(profile.getUniqueId(), reason, player.getName(), -1L, System.currentTimeMillis());
                    boolean success = Main.getPunishmentManager().setBanned(banData);
                    if (!success) {
                        player.sendMessage("§cDer Spieler konnte nicht gebannt werden!");
                        return;
                    }
                    Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§cDer Spieler §7" + args[0] + " §cwurde von §e" + banData.getBannedBy() + " §cpermanent gebannt."));
                    Bukkit.broadcastMessage(Messages.formatMessage(Messages.PREFIX + "§cGrund: §7" + banData.getBanReason()));
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
