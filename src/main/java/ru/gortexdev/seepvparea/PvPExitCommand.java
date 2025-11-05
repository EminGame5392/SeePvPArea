package ru.gortexdev.seepvparea;

import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PvPExitCommand implements TabExecutor {
    private final SeePvPArea plugin;

    public PvPExitCommand(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.plugin.color("&cЭта команла только для игроков!"));
            return true;
        }
        Player player = (Player)sender;
        FileConfiguration config = this.plugin.getConfig();
        if (config.getBoolean("settings.exit.checks.anti_relog_check.enable") && this.plugin
                .isInCombat(player) && config
                .getBoolean("settings.exit.checks.anti_relog_check.pvp_mode")) {
            player.sendMessage(this.plugin.color(config
                    .getStringList("settings.exit.checks.anti_relog_check.error-message").get(0)));
            sendDeclinedMessage(player, config);
            return true;
        }
        Location location = this.plugin.getLocation("pvpext");
        if (location == null) {
            player.sendMessage(this.plugin.color("&cТочка выхода с арены не установлена!"));
            return true;
        }
        player.teleport(location);
        sendAcceptedMessage(player, config);
        return true;
    }

    private void sendDeclinedMessage(Player player, FileConfiguration config) {
        if (config.getBoolean("settings.exit.declined.Message.enable"))
            this.plugin.color(config.getStringList("settings.exit.declined.Message.Message"))
                    .forEach(player::sendMessage);
        if (config.getBoolean("settings.exit.declined.Title.enable"))
            player.sendTitle(this.plugin
                    .color(config.getString("settings.exit.declined.Title.Title")), this.plugin
                    .color(config.getString("settings.exit.declined.Title.SubTitle")), 10, 70, 20);
        if (config.getBoolean("settings.exit.declined.BossBar.enable"))
            player.sendMessage(this.plugin.color(config.getString("settings.exit.declined.BossBar.BossBar")));
    }

    private void sendAcceptedMessage(Player player, FileConfiguration config) {
        if (config.getBoolean("settings.exit.accepted.Message.enable"))
            this.plugin.color(config.getStringList("settings.exit.accepted.Message.Message"))
                    .forEach(player::sendMessage);
        if (config.getBoolean("settings.exit.accepted.Title.enable"))
            player.sendTitle(this.plugin
                    .color(config.getString("settings.exit.accepted.Title.Title")), this.plugin
                    .color(config.getString("settings.exit.accepted.Title.SubTitle")), 10, 70, 20);
        if (config.getBoolean("settings.exit.accepted.BossBar.enable"))
            player.sendMessage(this.plugin.color(config.getString("settings.exit.accepted.BossBar.BossBar")));
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}