package net.nutchi.personalchest.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.personalchest.PersonalChest;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public class BlockListener implements Listener {
    private final PersonalChest plugin;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST)) {
            plugin.getPChestManager().removeChest(event.getBlock().getLocation());
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getStorage().deleteChests(event.getBlock().getLocation()));
        }
    }
}
