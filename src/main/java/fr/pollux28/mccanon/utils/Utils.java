package fr.pollux28.mccanon.utils;

import net.minecraft.server.v1_16_R3.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {
    public static Location getLocationFromBlockPos(World world, BlockPosition blockPos) {
        return new Location(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
