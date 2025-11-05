package ru.gortexdev.seepvparea;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class SeePvPAreaCommand implements TabExecutor {
    private final SeePvPArea plugin;

    public SeePvPAreaCommand(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("/seepvparea <set|reload>");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("seepvparea.admin")) {
                sender.sendMessage("&cУ вас нет прав на эту команду!");
                return true;
            }
            this.plugin.reloadConfiguration();
            sender.sendMessage("&aКонфигурация успешно перезагружена!");
            return true;
        }
        if (!args[0].equalsIgnoreCase("set")) {
            sender.sendMessage("/seepvparea <set|reload>");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("&cЭта команда только для игроков!");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("seepvparea.admin")) {
            player.sendMessage("&cУ вас нет прав на эту команду!");
            return true;
        }
        if (args.length != 2) {
            player.sendMessage("/seepvparea set <pvpent|pvpext>");
            return true;
        }
        String locationName = args[1].toLowerCase();
        if (!locationName.equals("pvpent") && !locationName.equals("pvpext")) {
            player.sendMessage("pvpent pvpext");
            return true;
        }
        Location location = player.getLocation();
        this.plugin.saveLocation(locationName, location);
        player.sendMessage("&aТочка " + locationName + " успешно установлена!");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("seepvparea.admin")) {
                completions.add("set");
                completions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set") &&
                sender.hasPermission("seepvparea.admin")) {
            completions.add("pvpent");
            completions.add("pvpext");
        }
        return completions;
    }
}
