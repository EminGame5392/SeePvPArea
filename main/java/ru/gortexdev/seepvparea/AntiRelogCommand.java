package ru.gortexdev.seepvparea;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class AntiRelogCommand implements TabExecutor {
    private final SeePvPArea plugin;

    public AntiRelogCommand(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("seepvparea.admin")) {
            sender.sendMessage(plugin.color("&cУ вас нет прав на эту команду!"));
            return true;
        }

        if (args.length != 3 || !args[0].equalsIgnoreCase("enable")) {
            sender.sendMessage(plugin.color("&cИспользование: /seepvparea antirelog enable <игрок1> <игрок2>"));
            return true;
        }

        Player player1 = Bukkit.getPlayer(args[1]);
        Player player2 = Bukkit.getPlayer(args[2]);

        if (player1 == null || player2 == null) {
            sender.sendMessage(plugin.color("&cОдин из игроков не найден!"));
            return true;
        }

        plugin.setPlayersInCombat(player1, player2);
        sender.sendMessage(plugin.color(String.format("&aАнтирелог установлен между %s и %s на %d секунд",
                player1.getName(), player2.getName(), plugin.getAntiRelogDuration())));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("enable");
        } else if (args.length == 2 || args.length == 3) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}