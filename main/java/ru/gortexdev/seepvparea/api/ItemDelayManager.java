package ru.gortexdev.seepvparea.api;

import org.bukkit.entity.Player;
import org.bukkit.Material;

public interface ItemDelayManager {
    long getItemUseDelay(Player player, Material material);
    boolean canUseItem(Player player, Material material);
    void setItemUseTime(Player player, Material material);
    int getDelayForMaterial(Material material);
}