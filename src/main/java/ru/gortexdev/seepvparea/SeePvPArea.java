package ru.gortexdev.seepvparea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.gortexdev.seepvparea.api.SeePvPAreaAPI;
import ru.gortexdev.seepvparea.api.SeePvPAreaAPIImpl;
import ru.gortexdev.seepvparea.api.SeePvPAreaProvider;
import ru.gortexdev.seepvparea.utils.ColorUtils;

public class SeePvPArea extends JavaPlugin implements Listener {
    private Connection connection;
    private FileConfiguration config;
    private SeePvPAreaAPI api;
    private final Map<UUID, Long> lastHitTimes = new HashMap<>();
    private final Map<UUID, Set<UUID>> combatPlayers = new HashMap<>();
    private final Map<UUID, Long> goldenAppleDelays = new HashMap<>();
    private final Map<UUID, Long> enchantedGoldenAppleDelays = new HashMap<>();
    private final Map<UUID, Long> chorusFruitDelays = new HashMap<>();
    private final Map<UUID, Long> enderPearlDelays = new HashMap<>();
    private final Map<UUID, Map<UUID, Long>> combatLogs = new HashMap<>();

    public int getAntiRelogDuration() {
        return getConfig().getInt("settings.anti_relog.duration", 30);
    }

    public void setPlayersInCombat(Player player1, Player player2) {
        UUID id1 = player1.getUniqueId();
        UUID id2 = player2.getUniqueId();
        long endTime = System.currentTimeMillis() + getAntiRelogDuration() * 1000L;
        combatLogs.computeIfAbsent(id1, k -> new HashMap<>()).put(id2, endTime);
        combatLogs.computeIfAbsent(id2, k -> new HashMap<>()).put(id1, endTime);
        sendRelogEnterMessage(player1);
        sendRelogEnterMessage(player2);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = getConfig();
        setupDatabase();
        getServer().getPluginManager().registerEvents(this, this);

        registerCommand("seepvparea", new SeePvPAreaCommand(this));
        registerCommand("pvpent", new PvPEnterCommand(this));
        registerCommand("pvpext", new PvPExitCommand(this));

        PluginCommand seepvpareaCmd = getCommand("seepvparea");
        if (seepvpareaCmd != null) {
            CommandManager commandManager = new CommandManager(this);
            commandManager.registerCommands();
            seepvpareaCmd.setExecutor(commandManager);
            seepvpareaCmd.setTabCompleter(commandManager);
        }

        this.api = new SeePvPAreaAPIImpl(this);
        SeePvPAreaProvider.registerAPI(this, this.api);

        getLogger().info("SeePvPArea v1.2.1 успешно запущен!");
    }

    public SeePvPAreaAPI getAPI() {
        return api;
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            if (executor instanceof TabCompleter)
                command.setTabCompleter((TabCompleter)executor);
        } else {
            getLogger().warning("&cНе удалось зарегистрировать команду: " + commandName + ". Проверьте plugin.yml!");
        }
    }

    public String color(String message) {
        return ColorUtils.color(message);
    }

    public List<String> color(List<String> messages) {
        return ColorUtils.color(messages);
    }

    public FileConfiguration getConfiguration() {
        return this.config;
    }

    public void reloadConfiguration() {
        reloadConfig();
        this.config = getConfig();
        getLogger().info("Конфигурация перезагружена!");
    }

    private void setupDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/locations.db");
            Statement stmt = this.connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS locations (name TEXT PRIMARY KEY, world TEXT, x REAL, y REAL, z REAL, yaw REAL, pitch REAL)");
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLocation(String name, Location location) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("INSERT OR REPLACE INTO locations (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, location.getWorld().getName());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setFloat(6, location.getYaw());
            ps.setFloat(7, location.getPitch());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation(String name) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM locations WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                World world = Bukkit.getWorld(rs.getString("world"));
                if (world == null)
                    return null;
                return new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;
        Player attacker = (Player)event.getDamager();
        Player victim = (Player)event.getEntity();
        setPlayersInCombat(attacker, victim);
        this.lastHitTimes.put(attacker.getUniqueId(), System.currentTimeMillis());
        this.lastHitTimes.put(victim.getUniqueId(), System.currentTimeMillis());
    }

    private void enterCombat(Player player1, Player player2) {
        combatPlayers.computeIfAbsent(player1.getUniqueId(), k -> new HashSet()).add(player2.getUniqueId());
        sendRelogEnterMessage(player1);
        startCombatTimer(player1.getUniqueId());
    }

    private void sendRelogEnterMessage(Player player) {
        if (this.config.getBoolean("settings.anti_relog.relog_entrance.Title.enable"))
            player.sendTitle(color(this.config.getString("settings.anti_relog.relog_entrance.Title.Title")),
                    color(this.config.getString("settings.anti_relog.relog_entrance.Title.SubTitle")), 10, 70, 20);
        if (this.config.getBoolean("settings.anti_relog.relog_entrance.Message.enable"))
            for (String line : color(this.config.getStringList("settings.anti_relog.relog_entrance.Message.Message")))
                player.sendMessage(line);
    }

    private void startCombatTimer(final UUID playerId) {
        (new BukkitRunnable() {
            public void run() {
                if (System.currentTimeMillis() - lastHitTimes.getOrDefault(playerId, 0L) > 30000L) {
                    combatPlayers.remove(playerId);
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null)
                        sendRelogExitMessage(player);
                    cancel();
                }
            }
        }).runTaskTimer(this, 20L, 20L);
    }

    private void sendRelogExitMessage(Player player) {
        if (this.config.getBoolean("settings.anti_relog.relog_exit.Title.enable"))
            player.sendTitle(color(this.config.getString("settings.anti_relog.relog_exit.Title.Title")),
                    color(this.config.getString("settings.anti_relog.relog_exit.Title.SubTitle")), 10, 70, 20);
        if (this.config.getBoolean("settings.anti_relog.relog_exit.BossBar.enable"))
            player.sendMessage(color(this.config.getString("settings.anti_relog.relog_exit.BossBar.BossBar")));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isInCombat(player)) {
            player.setHealth(0.0D);
            removeCombatData(player);
        }
    }

    private void removeCombatData(Player player) {
        UUID playerId = player.getUniqueId();
        combatPlayers.remove(playerId);
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isInCombat(player))
            return;

        Material material = event.getMaterial();
        String materialName = material.name().toLowerCase();

        if (materialName.contains("golden_apple")) {
            if (materialName.contains("enchanted")) {
                checkAndCancel(event, enchantedGoldenAppleDelays, config.getInt("settings.anti_relog.delays.enchanted_golden_apple", 10));
            } else {
                checkAndCancel(event, goldenAppleDelays, config.getInt("settings.anti_relog.delays.golden_apple", 10));
            }
        } else if (materialName.contains("chorus")) {
            checkAndCancel(event, chorusFruitDelays, config.getInt("settings.anti_relog.delays.chorus_fruit", 10));
        } else if (materialName.contains("ender_pearl")) {
            checkAndCancel(event, enderPearlDelays, config.getInt("settings.anti_relog.delays.ender_pearl", 10));
        }
    }

    private void checkAndCancel(PlayerInteractEvent event, Map<UUID, Long> delayMap, int delaySeconds) {
        Player player = event.getPlayer();
        long currentTime = System.currentTimeMillis();
        if (delayMap.containsKey(player.getUniqueId())) {
            long lastUse = delayMap.get(player.getUniqueId());
            if (currentTime - lastUse < delaySeconds * 1000L) {
                event.setCancelled(true);
                player.sendMessage(color("&cВы можете использовать это только раз в " + delaySeconds + " секунд в бою!"));
                return;
            }
        }
        delayMap.put(player.getUniqueId(), currentTime);
    }

    public boolean isInCombat(Player player) {
        UUID playerId = player.getUniqueId();
        if (!this.combatLogs.containsKey(playerId))
            return false;
        long currentTime = System.currentTimeMillis();
        Map<UUID, Long> opponents = this.combatLogs.get(playerId);
        opponents.entrySet().removeIf(entry -> (entry.getValue() < currentTime));
        return !opponents.isEmpty();
    }

    public FileConfiguration getPluginConfig() {
        return this.config;
    }

    @Override
    public void onDisable() {
        SeePvPAreaProvider.unregisterAPI(this);
        try {
            if (this.connection != null)
                this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}