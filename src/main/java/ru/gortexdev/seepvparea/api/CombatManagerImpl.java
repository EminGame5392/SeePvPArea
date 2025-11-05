package ru.gortexdev.seepvparea.api;

import org.bukkit.entity.Player;
import ru.gortexdev.seepvparea.SeePvPArea;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManagerImpl implements CombatManager {
    private final SeePvPArea plugin;
    private final Map<UUID, Map<UUID, Long>> combatLogs = new ConcurrentHashMap<>();

    public CombatManagerImpl(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isInCombat(Player player) {
        UUID playerId = player.getUniqueId();
        if (!combatLogs.containsKey(playerId)) return false;

        long currentTime = System.currentTimeMillis();
        Map<UUID, Long> opponents = combatLogs.get(playerId);
        opponents.entrySet().removeIf(entry -> entry.getValue() < currentTime);

        return !opponents.isEmpty();
    }

    @Override
    public boolean arePlayersInCombat(Player player1, Player player2) {
        UUID id1 = player1.getUniqueId();
        UUID id2 = player2.getUniqueId();

        if (!combatLogs.containsKey(id1)) return false;

        Map<UUID, Long> opponents = combatLogs.get(id1);
        Long endTime = opponents.get(id2);

        if (endTime == null) return false;
        if (endTime < System.currentTimeMillis()) {
            opponents.remove(id2);
            return false;
        }

        return true;
    }

    @Override
    public void setCombat(Player player1, Player player2) {
        UUID id1 = player1.getUniqueId();
        UUID id2 = player2.getUniqueId();
        long endTime = System.currentTimeMillis() + plugin.getAntiRelogDuration() * 1000L;

        combatLogs.computeIfAbsent(id1, k -> new ConcurrentHashMap<>()).put(id2, endTime);
        combatLogs.computeIfAbsent(id2, k -> new ConcurrentHashMap<>()).put(id1, endTime);
    }

    @Override
    public void removeCombat(Player player) {
        UUID playerId = player.getUniqueId();

        Map<UUID, Long> opponents = combatLogs.remove(playerId);
        if (opponents != null) {
            for (UUID opponentId : opponents.keySet()) {
                Map<UUID, Long> opponentLogs = combatLogs.get(opponentId);
                if (opponentLogs != null) {
                    opponentLogs.remove(playerId);
                    if (opponentLogs.isEmpty()) {
                        combatLogs.remove(opponentId);
                    }
                }
            }
        }
    }

    @Override
    public Set<UUID> getCombatOpponents(Player player) {
        UUID playerId = player.getUniqueId();
        if (!combatLogs.containsKey(playerId)) return Collections.emptySet();

        long currentTime = System.currentTimeMillis();
        Map<UUID, Long> opponents = combatLogs.get(playerId);
        opponents.entrySet().removeIf(entry -> entry.getValue() < currentTime);

        return opponents.keySet();
    }

    @Override
    public long getCombatTimeLeft(Player player1, Player player2) {
        UUID id1 = player1.getUniqueId();
        UUID id2 = player2.getUniqueId();

        if (!combatLogs.containsKey(id1)) return 0;

        Map<UUID, Long> opponents = combatLogs.get(id1);
        Long endTime = opponents.get(id2);

        if (endTime == null) return 0;
        long timeLeft = endTime - System.currentTimeMillis();
        return Math.max(0, timeLeft / 1000);
    }
}