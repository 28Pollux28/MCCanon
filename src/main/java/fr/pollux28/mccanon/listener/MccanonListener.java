package fr.pollux28.mccanon.listener;

import fr.pollux28.mccanon.Mccanon;
import fr.pollux28.mccanon.cannon.Cannon;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class MccanonListener implements Listener {

    /**
     * PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        //if block right click is trigger for cannon, then add a player to the cannon and fire the cannon
        Block clicked = event.getClickedBlock();
        if (clicked != null) {
            if (clicked.getType().equals(Material.WARPED_BUTTON)) {
                Cannon cannon = Mccanon.getCannonManager().getCannonByTrigger(clicked.getLocation());
                if (cannon != null) {
                    Player player = clicked.getWorld().getNearbyEntities(cannon.getLocation(), 1, 1, 1).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).findFirst().orElse(null);
                    if (player != null) {
                        cannon.setCannonPlayer(player);
                        cannon.fire();
                    } else {
                        event.getPlayer().sendMessage("§cNo player found near the cannon");
                    }
                }
            }
        }
    }
}
