package ru.gortexdev.seepvparea.api;

import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import ru.gortexdev.seepvparea.SeePvPArea;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemDelayManagerImpl implements ItemDelayManager {
    private final SeePvPArea plugin;
    private final Map<UUID, Map<Material, Long>> playerDelays = new ConcurrentHashMap<>();

    public ItemDelayManagerImpl(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    @Override
    public long getItemUseDelay(Player player, Material material) {
        UUID playerId = player.getUniqueId();
        Map<Material, Long> delays = playerDelays.get(playerId);
        if (delays == null) return 0;

        Long lastUse = delays.get(material);
        if (lastUse == null) return 0;

        long currentTime = System.currentTimeMillis();
        int delaySeconds = getDelayForMaterial(material);
        long timePassed = (currentTime - lastUse) / 1000;

        return Math.max(0, delaySeconds - timePassed);
    }

    @Override
    public boolean canUseItem(Player player, Material material) {
        return getItemUseDelay(player, material) <= 0;
    }

    @Override
    public void setItemUseTime(Player player, Material material) {
        UUID playerId = player.getUniqueId();
        Map<Material, Long> delays = playerDelays.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        delays.put(material, System.currentTimeMillis());
    }

    @Override
    public int getDelayForMaterial(Material material) {
        FileConfiguration config = plugin.getConfig();
        String materialName = material.name().toLowerCase();

        if (materialName.contains("golden_apple")) {
            if (materialName.contains("enchanted")) {
                return config.getInt("settings.anti_relog.delays.enchanted_golden_apple", 10);
            }
            return config.getInt("settings.anti_relog.delays.golden_apple", 10);
        } else if (materialName.contains("chorus")) {
            return config.getInt("settings.anti_relog.delays.chorus_fruit", 10);
        } else if (materialName.contains("ender_pearl")) {
            return config.getInt("settings.anti_relog.delays.ender_pearl", 10);
        }
        return 0;
    }
}