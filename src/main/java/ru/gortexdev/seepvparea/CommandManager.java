package ru.gortexdev.seepvparea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final SeePvPArea plugin;

    private final Map<String, CommandExecutor> commands = new HashMap<>();

    private final Map<String, TabCompleter> completers = new HashMap<>();

    public CommandManager(SeePvPArea plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        registerCommand("antirelog", (CommandExecutor)new AntiRelogCommand(this.plugin));
    }

    private void registerCommand(String name, CommandExecutor executor) {
        this.commands.put(name.toLowerCase(), executor);
        if (executor instanceof TabCompleter)
            this.completers.put(name.toLowerCase(), (TabCompleter)executor);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0)
            return false;
        String subCmd = args[0].toLowerCase();
        if (this.commands.containsKey(subCmd)) {
            CommandExecutor executor = this.commands.get(subCmd);
            return executor.onCommand(sender, cmd, label, args);
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1)
            return (List<String>)this.commands.keySet().stream()
                    .filter(c -> c.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        if (args.length > 1 && this.completers.containsKey(args[0].toLowerCase()))
            return ((TabCompleter)this.completers.get(args[0].toLowerCase()))
                    .onTabComplete(sender, cmd, label, args);
        return null;
    }
}
