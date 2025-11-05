package ru.gortexdev.seepvparea.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface SeePvPAreaAPI {
    boolean isInCombat(Player player);
    void setPlayersInCombat(Player player1, Player player2);
    void removeFromCombat(Player player);
    int getAntiRelogDuration();
    CombatManager getCombatManager();
    ItemDelayManager getItemDelayManager();
}