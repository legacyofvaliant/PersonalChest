package com.github.e2318501.personalchest.listener;

import lombok.RequiredArgsConstructor;
import com.github.e2318501.personalchest.PersonalChest;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

@RequiredArgsConstructor
public class InventoryListener implements Listener {
    private final PersonalChest plugin;

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
            Location loc = event.getInventory().getLocation();

            if (loc != null) {
                Material type = loc.getBlock().getType();
                if ((type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST)) && isTargetWorld(loc)) {
                    player.openInventory(plugin.getPChestManager().getPlayerInventory(loc, player.getUniqueId(), event.getInventory()));
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isTargetWorld(Location loc) {
        return loc.getWorld() != null && plugin.getConfig().getStringList("worlds").contains(loc.getWorld().getName());
    }
}
