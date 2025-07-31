package com.github.e2318501.personalchest;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class PChestManager {
    private final PersonalChest plugin;
    private final Map<BlockLocation, Map<UUID, Inventory>> chests = new HashMap<>();

    public void loadChest(UUID uuid) {
        plugin.getStorage().loadPlayerInventories(uuid).forEach((loc, inv) -> {
            if (chests.containsKey(loc)) {
                chests.get(loc).put(uuid, inv);
            } else {
                Map<UUID, Inventory> invs = new HashMap<>();
                invs.put(uuid, inv);
                chests.put(loc, invs);
            }
        });
    }

    public void unloadChests() {
        plugin.getServer().getOnlinePlayers().forEach(p -> unloadChest(p.getUniqueId()));
    }

    public void unloadChest(UUID uuid) {
        chests.forEach((loc, invs) -> {
            if (invs.containsKey(uuid)) {
                plugin.getStorage().savePlayerInventories(uuid, loc, invs.get(uuid));
                invs.remove(uuid);
            }
        });
    }

    public void saveChests() {
        plugin.getServer().getOnlinePlayers().forEach(p -> saveChest(p.getUniqueId()));
    }

    private void saveChest(UUID uuid) {
        chests.forEach((loc, invs) -> {
            if (invs.containsKey(uuid)) {
                plugin.getStorage().savePlayerInventories(uuid, loc, invs.get(uuid));
            }
        });
    }

    public void removeChest(Location loc) {
        chests.remove(BlockLocation.fromLocation(loc));
    }

    public Inventory getPlayerInventory(Location loc, UUID uuid, Inventory defaultInventory) {
        BlockLocation bloc = BlockLocation.fromLocation(loc);
        if (chests.containsKey(bloc)) {
            if (chests.get(bloc).containsKey(uuid)) {
                return chests.get(bloc).get(uuid);
            } else {
                Inventory inv = createPlayerInventory(defaultInventory);
                chests.get(bloc).put(uuid, inv);

                return inv;
            }
        } else {
            Inventory inv = createPlayerInventory(defaultInventory);

            Map<UUID, Inventory> invs = new HashMap<>();
            invs.put(uuid, inv);
            chests.put(bloc, invs);

            return inv;
        }
    }

    private Inventory createPlayerInventory(Inventory defaultInventory) {
        Inventory inv = plugin.getServer().createInventory(null, defaultInventory.getSize());
        inv.setContents(defaultInventory.getContents());
        return inv;
    }
}
