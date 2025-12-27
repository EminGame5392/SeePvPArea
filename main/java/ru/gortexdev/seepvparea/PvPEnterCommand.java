package ru.gortexdev.seepvparea;

import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PvPEnterCommand implements TabExecutor {
    private final SeePvPArea plugin;

    public PvPEnterCommand(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.color("&cЭта команда только для игроков!"));
            return true;
        }
        Player player = (Player)sender;
        FileConfiguration config = plugin.getConfig();
        if (!checkRequirements(player, config)) {
            sendDeclinedMessage(player, config);
            return true;
        }
        Location location = plugin.getLocation("pvpent");
        if (location == null) {
            player.sendMessage(plugin.color("&cТочка входа на арену не установлена!"));
            return true;
        }
        player.teleport(location);
        sendAcceptedMessage(player, config);
        return true;
    }

    private boolean checkRequirements(Player player, FileConfiguration config) {
        if (config.getBoolean("settings.entrance.checks.armor_check.enable") && !hasFullArmor(player))
            return false;
        if (config.getBoolean("settings.entrance.checks.hp_check.enable") && player
                .getHealth() < config.getDouble("settings.entrance.checks.hp_check.minimal_hp"))
            return false;
        return (!config.getBoolean("settings.entrance.checks.weapon_check.enable") || hasWeapon(player));
    }

    private boolean hasFullArmor(Player player) {
        PlayerInventory inv = player.getInventory();
        return (inv.getHelmet() != null && inv.getChestplate() != null && inv
                .getLeggings() != null && inv.getBoots() != null);
    }

    private boolean hasWeapon(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                Material type = item.getType();
                if (type.toString().endsWith("_SWORD") || type.toString().endsWith("_AXE"))
                    return true;
            }
        }
        return false;
    }

    private void sendDeclinedMessage(Player player, FileConfiguration config) {
        if (config.getBoolean("settings.entrance.declined.Message.enable"))
            plugin.color(config.getStringList("settings.entrance.declined.Message.Message"))
                    .forEach(player::sendMessage);
        if (config.getBoolean("settings.entrance.declined.Title.enable"))
            player.sendTitle(plugin
                    .color(config.getString("settings.entrance.declined.Title.Title")), plugin
                    .color(config.getString("settings.entrance.declined.Title.SubTitle")), 10, 70, 20);
        if (config.getBoolean("settings.entrance.declined.BossBar.enable"))
            player.sendMessage(plugin.color(config.getString("settings.entrance.declined.BossBar.BossBar")));
    }

    private void sendAcceptedMessage(Player player, FileConfiguration config) {
        if (config.getBoolean("settings.entrance.accepted.Message.enable"))
            plugin.color(config.getStringList("settings.entrance.accepted.Message.Message"))
                    .forEach(player::sendMessage);
        if (config.getBoolean("settings.entrance.accepted.Title.enable"))
            player.sendTitle(plugin
                    .color(config.getString("settings.entrance.accepted.Title.Title")), plugin
                    .color(config.getString("settings.entrance.accepted.Title.SubTitle")), 10, 70, 20);
        if (config.getBoolean("settings.entrance.accepted.BossBar.enable"))
            player.sendMessage(plugin.color(config.getString("settings.entrance.accepted.BossBar.BossBar")));
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}