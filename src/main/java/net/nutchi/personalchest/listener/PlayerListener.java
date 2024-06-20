package net.nutchi.personalchest.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.personalchest.PersonalChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final PersonalChest plugin;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        plugin.getPChestManager().loadChest(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPChestManager().unloadChest(event.getPlayer().getUniqueId());
    }
}
