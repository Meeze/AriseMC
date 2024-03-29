package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.UserCurrency;
import de.hardcorepvp.manager.UUIDManager;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandPay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 2) {
            player.sendMessage("/pay <Spieler> <Amount>");
            return true;
        }


        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            this.transferMoney(player, target.getUniqueId(), target.getName(), Long.parseLong(args[1]));
            return true;
        }

        UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
            @Override
            public void onResult(UUIDManager.ProfileHolder profile) {
                transferMoney(player, profile.getUniqueId(), profile.getName(), Long.parseLong(args[1]));
            }

            @Override
            public void onFailure(Throwable cause) {
                player.sendMessage(Messages.PLAYER_DOESNT_EXIST);
            }
        });

        return true;
    }

    private void transferMoney(Player executor, UUID targetUniqueId, String targetName, long amount) {
        Main.getCurrencyManager().getUserCurrency(targetUniqueId, new Callback<UserCurrency>() {
            @Override
            public void onResult(UserCurrency currency) {
                if (amount > 100) {
                    executor.sendMessage("Mindestens 100!");
                    return;
                }
                if (targetUniqueId == executor.getUniqueId()) {
                    executor.sendMessage("Nicht zu dir selbst");
                    return;
                }
                Main.getCurrencyManager().getUserCurrency(executor.getUniqueId(), new Callback<UserCurrency>() {
                    @Override
                    public void onResult(UserCurrency currencyTarget) {
                        currency.setMoney(currency.getMoney() + amount);
                        currencyTarget.setMoney(currencyTarget.getMoney() - amount);
                        executor.sendMessage("Du hast " + targetName + " " + amount + " gesendet!");
                        if (Bukkit.getPlayer(targetName) != null) {
                            Bukkit.getPlayer(targetName).sendMessage("Du hast von " + executor.getName() + " " + amount + " erhalten!");
                        }
                    }

                    @Override
                    public void onFailure(Throwable cause) {

                    }
                });
            }

            @Override
            public void onFailure(Throwable cause) {
                System.out.println("error");
            }
        });
    }
}
