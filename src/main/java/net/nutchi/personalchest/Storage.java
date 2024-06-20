package net.nutchi.personalchest;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class Storage {
    private final PersonalChest plugin;

    private final Gson gson = new Gson();

    private final String mysqlHost;
    private final int mysqlPort;
    private final String mysqlDatabase;
    private final String mysqlUsername;
    private final String mysqlPassword;
    private final String mysqlTablePrefix;

    public boolean init() {
        try {
            Connection connection = getConnection();

            String sql = String.format("CREATE TABLE IF NOT EXISTS %spersonal_chests (" +
                    "location text NOT NULL," +
                    "player_uuid varchar(36) NOT NULL," +
                    "inventory text NOT NULL," +
                    "UNIQUE(location, player_uuid)" +
                    ")", mysqlTablePrefix);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            statement.close();
            connection.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    public Map<BlockLocation, Inventory> loadPlayerInventories(UUID uuid) {
        Map<BlockLocation, Inventory> invs = new HashMap<>();

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(String.format("SELECT * FROM %spersonal_chests WHERE player_uuid = ?", mysqlTablePrefix));
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Inventory inv = fromBase64(resultSet.getString("inventory"));
                if (inv != null) {
                    invs.put(gson.fromJson(resultSet.getString("location"), BlockLocation.class), inv);
                }
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invs;
    }

    public void savePlayerInventories(UUID uuid, BlockLocation loc, Inventory inv) {
        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(String.format("REPLACE INTO %spersonal_chests (location, player_uuid, inventory) VALUES (?, ?, ?)", mysqlTablePrefix));

            String invData = toBase64(inv);
            if (invData != null) {
                statement.setString(1, gson.toJson(loc));
                statement.setString(2, uuid.toString());
                statement.setString(3, invData);

                statement.executeUpdate();
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String toBase64(Inventory inv) {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream)
        ) {

            bukkitOutputStream.writeInt(inv.getSize());

            for (int i = 0; i < inv.getSize(); i++) {
                bukkitOutputStream.writeObject(inv.getItem(i));
            }

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private @Nullable Inventory fromBase64(String data) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream)
        ) {
            Inventory inv = plugin.getServer().createInventory(null, bukkitInputStream.readInt());

            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, (ItemStack) bukkitInputStream.readObject());
            }

            return inv;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteChests(Location loc) {
        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(String.format("DELETE FROM %spersonal_chests WHERE location = ?", mysqlTablePrefix));
            statement.setString(1, gson.toJson(BlockLocation.fromLocation(loc)));
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDatabase;
        return DriverManager.getConnection(url, mysqlUsername, mysqlPassword);
    }
}
