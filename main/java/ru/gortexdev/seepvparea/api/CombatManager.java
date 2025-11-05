package ru.gortexdev.seepvparea.api;

import org.bukkit.entity.Player;
import java.util.Set;
import java.util.UUID;

public interface CombatManager {
    boolean isInCombat(Player player);
    boolean arePlayersInCombat(Player player1, Player player2);
    void setCombat(Player player1, Player player2);
    void removeCombat(Player player);
    Set<UUID> getCombatOpponents(Player player);
    long getCombatTimeLeft(Player player1, Player player2);
}