package ru.gortexdev.seepvparea.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.gortexdev.seepvparea.SeePvPArea;

public class SeePvPAreaAPIImpl implements SeePvPAreaAPI {
    private final SeePvPArea plugin;
    private final CombatManager combatManager;
    private final ItemDelayManager itemDelayManager;

    public SeePvPAreaAPIImpl(SeePvPArea plugin) {
        this.plugin = plugin;
        this.combatManager = new CombatManagerImpl(plugin);
        this.itemDelayManager = new ItemDelayManagerImpl(plugin);
    }

    @Override
    public boolean isInCombat(Player player) {
        return plugin.isInCombat(player);
    }

    @Override
    public void setPlayersInCombat(Player player1, Player player2) {
        plugin.setPlayersInCombat(player1, player2);
    }

    @Override
    public void removeFromCombat(Player player) {
        combatManager.removeCombat(player);
    }

    @Override
    public int getAntiRelogDuration() {
        return plugin.getAntiRelogDuration();
    }

    @Override
    public CombatManager getCombatManager() {
        return combatManager;
    }

    @Override
    public ItemDelayManager getItemDelayManager() {
        return itemDelayManager;
    }
}