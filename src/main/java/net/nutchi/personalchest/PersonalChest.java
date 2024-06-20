package net.nutchi.personalchest;

import lombok.Getter;
import net.nutchi.personalchest.listener.BlockListener;
import net.nutchi.personalchest.listener.InventoryListener;
import net.nutchi.personalchest.listener.PlayerListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public final class PersonalChest extends JavaPlugin {
    private final PChestManager pChestManager = new PChestManager(this);
    private Storage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String mysqlHost = getConfig().getString("mysql.host");
        int mysqlPort = getConfig().getInt("mysql.port");
        String mysqlDatabase = getConfig().getString("mysql.database");
        String mysqlUsername = getConfig().getString("mysql.username");
        String mysqlPassword = getConfig().getString("mysql.password");
        String mysqlTablePrefix = getConfig().getString("mysql.table-prefix");

        if (mysqlHost == null || mysqlDatabase == null || mysqlUsername == null || mysqlPassword == null) {
            getLogger().warning("Missing configuration values. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        storage = new Storage(this, mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword, mysqlTablePrefix);

        if (!storage.init()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        register();
        startSaveTask();
    }

    private void register() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
    }

    private void startSaveTask() {
        getServer().getScheduler().runTaskTimer(this, pChestManager::saveChests, 0, 1200);
    }

    @Override
    public void onDisable() {
        pChestManager.unloadChests();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage("PersonalChest config reloaded.");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("reload").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
