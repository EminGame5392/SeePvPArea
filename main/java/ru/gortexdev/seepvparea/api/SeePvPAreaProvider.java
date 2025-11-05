package ru.gortexdev.seepvparea.api;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class SeePvPAreaProvider {
    private static SeePvPAreaAPI api;

    public static void registerAPI(JavaPlugin plugin, SeePvPAreaAPI apiInstance) {
        api = apiInstance;
        plugin.getServer().getServicesManager().register(SeePvPAreaAPI.class, apiInstance, plugin, ServicePriority.Normal);
    }

    public static void unregisterAPI(JavaPlugin plugin) {
        plugin.getServer().getServicesManager().unregisterAll(plugin);
        api = null;
    }

    public static SeePvPAreaAPI getAPI() {
        return api;
    }
}