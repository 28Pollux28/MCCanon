package fr.pollux28.mccanon;


import com.mojang.brigadier.CommandDispatcher;
import fr.pollux28.mccanon.cannon.CannonManager;
import fr.pollux28.mccanon.commands.CannonCommand;
import fr.pollux28.mccanon.listener.MccanonListener;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Mccanon extends JavaPlugin {
    public static final Logger logger = LoggerFactory.getLogger("Mccanon");
    private static CannonManager cannonManager;

    public static CannonManager getCannonManager() {
        return cannonManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        cannonManager = new CannonManager();
        Bukkit.getPluginManager().registerEvents(new MccanonListener(), this);
        loadCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    void loadCommands() {
        DedicatedServer server = (DedicatedServer) DedicatedServer.getServer();
        CommandDispatcher<CommandListenerWrapper> dispatcher = server.getCommandDispatcher().a();
        CannonCommand.init(dispatcher);
    }
}
