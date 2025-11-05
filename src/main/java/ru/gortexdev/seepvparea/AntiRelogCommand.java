package ru.gortexdev.seepvparea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class AntiRelogCommand {
    private final SeePvPArea plugin;

    public AntiRelogCommand(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("seepvparea.admin")) {
            sender.sendMessage(this.plugin.color("&cУ вас нет прав на эту команду!"));
            return true;
        }
        if (args.length != 3 || !args[0].equalsIgnoreCase("enable")) {
            sender.sendMessage(this.plugin.color("&cИспользование: /seepvparea antirelog enable <игрок1> <игрок2>"));
            return true;
        }
        Player player1 = Bukkit.getPlayer(args[1]);
        Player player2 = Bukkit.getPlayer(args[2]);
        if (player1 == null || player2 == null) {
            sender.sendMessage(this.plugin.color("&cОдин из игроков не найден!"));
            return true;
        }
        this.plugin.setPlayersInCombat(player1, player2);
        sender.sendMessage(this.plugin.color(String.format("&aАнтирелог установлен между %s и %s на %d секунд", new Object[] { player1
                .getName(), player2
                .getName(),
                Integer.valueOf(this.plugin.getAntiRelogDuration()) })));
        return true;
    }
}
