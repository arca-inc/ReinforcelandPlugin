package fr.arcainc.reinforcelandplugin.utils;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArmorStandUtil {

    public static List<ArmorStand> armorStands = new ArrayList<>();

    /**
     * Sends an ArmorStand hologram message to a player above a specified block.
     *
     * @param player  The player to whom the hologram will be displayed.
     * @param message The message to display in the hologram.
     * @param block   The block above which the hologram will be displayed.
     * @param plugin  The ReinforceLandPlugin instance.
     */
    public static void sendArmorStandHologram(Player player, String message, Block block, ReinforceLandPlugin plugin) {
        Location blockLocation = block.getLocation();
        ArmorStand armorStand = getArmorStandAtLocation(blockLocation);

        if (armorStand != null) {
            if (armorStand.getCustomName().equalsIgnoreCase(message)) return;

            armorStand.setCustomName(message);
        } else {
            // Calculate the direction vector from the player to the block
            Vector direction = blockLocation.toVector().subtract(player.getLocation().toVector()).normalize();

            // Determine the block face that the player is looking at
            BlockFace blockFace = getBlockFace(direction);

            // Calculate the offset based on the block face
            double offsetX = 0.0;
            double offsetY = 0.0;
            double offsetZ = 0.0;

            Location toModify = blockLocation.clone().add(0.5,0,0.5);

            // Adjust the offset based on the block face and player's facing direction
            switch (blockFace) {
                case NORTH:
                    offsetZ = -0.8;
                    break;
                case SOUTH:
                    offsetZ = 0.9;
                    break;
                case EAST:
                    offsetX = 0.9;
                    break;
                case WEST:
                    offsetX = -0.8;
                    break;
                case UP:
                    offsetY = 1.1;
                    break;
                case DOWN:
                    offsetY = -0.7;
                    break;
            }

            // Set the ArmorStand's new location
            Location finalLocation = toModify.clone().add(offsetX, offsetY, offsetZ);
            armorStand = block.getWorld().spawn(finalLocation, ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCustomName(message);
            armorStand.setCustomNameVisible(true);
            armorStand.setMarker(true);
            armorStands.add(armorStand);

        }


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.getTargetBlock(null, 5).getLocation() != block.getLocation()) {
                armorStands.remove(getArmorStandAtLocation(blockLocation));
                if(getArmorStandAtLocation(blockLocation) != null)
                    Objects.requireNonNull(getArmorStandAtLocation(blockLocation)).remove();
            }
        }, 20 * 4);
    }

    /**
     * Retrieves the ArmorStand at a specified location.
     *
     * @param location The location to check for an ArmorStand.
     * @return The ArmorStand entity if found, or null if none is found.
     */
    private static ArmorStand getArmorStandAtLocation(Location location) {
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.getLocation().distance(location) < 1.5) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }

    /**
     * Checks if there is an ArmorStand at a specified location.
     *
     * @param location The location to check for an ArmorStand.
     * @return true if an ArmorStand is found, false otherwise.
     */
    private static boolean isArmorStandAtLocation(Location location) {
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.getLocation().distance(location) < 1.5) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the BlockFace corresponding to a given direction vector.
     *
     * @param direction The direction vector to analyze.
     * @return The BlockFace representing the direction vector.
     */
    private static BlockFace getBlockFace(Vector direction) {
        double x = direction.getX();
        double y = direction.getY();
        double z = direction.getZ();

        if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
            return x > 0 ? BlockFace.WEST : BlockFace.EAST;
        } else if (Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
            return y > 0 ? BlockFace.DOWN : BlockFace.UP;
        } else {
            return z > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
        }
    }
}



