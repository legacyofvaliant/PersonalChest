package com.github.e2318501.personalchest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class BlockLocation {
    private final @Nullable String world;
    private final int x;
    private final int y;
    private final int z;

    public static BlockLocation fromLocation(Location loc) {
        String worldName = loc.getWorld() != null ? loc.getWorld().getName() : null;
        return new BlockLocation(worldName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public String toString() {
        return world + " / " + x + " / " + y + " / " + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockLocation) {
            BlockLocation bloc = (BlockLocation) obj;
            return Objects.equals(world, bloc.getWorld()) && x == bloc.getX() && y == bloc.getY() && z == bloc.getZ();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }
}
