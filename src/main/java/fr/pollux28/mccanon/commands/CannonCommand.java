package fr.pollux28.mccanon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.pollux28.mccanon.Mccanon;
import fr.pollux28.mccanon.cannon.Cannon;
import fr.pollux28.mccanon.utils.CommandUtils;
import fr.pollux28.mccanon.utils.Utils;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class CannonCommand {

    /*structure of the command:
    /mccannon <add> <name> <location> [power] [fuseTime] [trigger]
    /mccannon <remove> <name>
    /mccannon <list>
    /mccannon <help>
    /mccannon <info> <name>
    /mccannon <set> <name> <power|fuseTime> <value>
    /mccannon <name> fire
    /mccannon <set> <name> <trigger> <location>
    /mccannon <set> <name> <location> <location>
    /mccannon <save>
    /mccannon <load>
    /mccannon <reload>
    */

    public static final SuggestionProvider<CommandListenerWrapper> SUGGEST_CANNON_NAME = (context, builder) -> {
        List<String> cannonNames = new ArrayList<>();
        for (Cannon cannon : Mccanon.getCannonManager().getCannons()) {
            cannonNames.add(cannon.getName());
        }
        return ICompletionProvider.b(cannonNames, builder);
    };

    public static void init(CommandDispatcher<CommandListenerWrapper> dispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> command = CommandUtils.literal("mccannon").requires((executor) -> executor.hasPermission(4, "mccannon.command"));
        //ADD
        command.then(CommandUtils.literal("add").requires((executor) -> executor.hasPermission(4, "mccannon.command.add"))
                .then(CommandUtils.argument("name", StringArgumentType.word())
                        .then((CommandUtils.argument("location", ArgumentPosition.a()).executes((context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "name"), ArgumentPosition.a(context, "location"), null, null, null))))
                                .then((CommandUtils.argument("power", FloatArgumentType.floatArg(0)).executes(context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "name"), ArgumentPosition.a(context, "location"), FloatArgumentType.getFloat(context, "power"), null, null)))
                                        .then((CommandUtils.argument("fuseTime", FloatArgumentType.floatArg(0)).executes(context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "name"), ArgumentPosition.a(context, "location"), FloatArgumentType.getFloat(context, "power"), FloatArgumentType.getFloat(context, "fuseTime"), null)))
                                                .then(CommandUtils.argument("trigger", ArgumentPosition.a())
                                                        .executes(context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "name"), ArgumentPosition.a(context, "location"), FloatArgumentType.getFloat(context, "power"), FloatArgumentType.getFloat(context, "fuseTime"), ArgumentPosition.a(context, "trigger"))))
                                        )
                                )
                        )
                )
        );
        //REMOVE
        command.then(CommandUtils.literal("remove").requires((executor) -> executor.hasPermission(4, "mccannon.command.remove")).then(CommandUtils.argument("name", StringArgumentType.word()).suggests(SUGGEST_CANNON_NAME).executes(context -> executeRemove(context.getSource(), StringArgumentType.getString(context, "name")))));
        //LIST
        command.then(CommandUtils.literal("list").requires((executor) -> executor.hasPermission(4, "mccannon.command.list")).executes(context -> executeList(context.getSource())));
        //HELP
        command.then(CommandUtils.literal("help").requires((executor) -> executor.hasPermission(4, "mccannon.command.help")).executes(context -> executeHelp(context.getSource())));
        //INFO
        command.then(CommandUtils.literal("info").requires((executor) -> executor.hasPermission(4, "mccannon.command.info")).then(CommandUtils.argument("name", StringArgumentType.word()).suggests(SUGGEST_CANNON_NAME).executes(context -> executeInfo(context.getSource(), StringArgumentType.getString(context, "name")))));
        //SET
        command.then(CommandUtils.literal("set").requires((executor) -> executor.hasPermission(4, "mccannon.command.set"))
                .then(CommandUtils.argument("name", StringArgumentType.word()).suggests(SUGGEST_CANNON_NAME)
                        .then(CommandUtils.literal("power").then(CommandUtils.argument("power", FloatArgumentType.floatArg(0f)).executes(context -> executeSet(context.getSource(), StringArgumentType.getString(context, "name"), "power", FloatArgumentType.getFloat(context, "power")))))
                        .then(CommandUtils.literal("fuseTime").then(CommandUtils.argument("fuseTime", FloatArgumentType.floatArg(0f)).executes(context -> executeSet(context.getSource(), StringArgumentType.getString(context, "name"), "fuseTime", FloatArgumentType.getFloat(context, "fuseTime")))))
                        .then(CommandUtils.literal("trigger").then(CommandUtils.argument("trigger", ArgumentPosition.a()).executes(context -> executeSet(context.getSource(), StringArgumentType.getString(context, "name"), "trigger", ArgumentPosition.a(context, "trigger")))))
                        .then(CommandUtils.literal("location").then(CommandUtils.argument("location", ArgumentPosition.a()).executes(context -> executeSet(context.getSource(), StringArgumentType.getString(context, "name"), "location", ArgumentPosition.a(context, "location")))))
                )
        );
        //FIRE
        command.then(CommandUtils.literal("fire").requires((executor) -> executor.hasPermission(4, "mccannon.command.fire")).then(CommandUtils.argument("name", StringArgumentType.word()).suggests(SUGGEST_CANNON_NAME).executes(context -> executeFire(context.getSource(), StringArgumentType.getString(context, "name")))));
        //SAVE
        command.then(CommandUtils.literal("save").requires((executor) -> executor.hasPermission(4, "mccannon.command.save")).executes(context -> executeSave(context.getSource())));
        //LOAD
        command.then(CommandUtils.literal("load").requires((executor) -> executor.hasPermission(4, "mccannon.command.load")).executes(context -> executeLoad(context.getSource())));
        //RELOAD
        command.then(CommandUtils.literal("reload").requires((executor) -> executor.hasPermission(4, "mccannon.command.reload")).executes(context -> executeReload(context.getSource())));
        //Register Commands and plugin like aliases
        LiteralCommandNode<CommandListenerWrapper> nodeMCCannon = dispatcher.register(command);
        dispatcher.register(CommandUtils.literal("cannon").redirect(nodeMCCannon));
        dispatcher.register(CommandUtils.literal("mccannon:mccannon").redirect(nodeMCCannon));
        dispatcher.register(CommandUtils.literal("mccannon:cannon").redirect(nodeMCCannon));
    }

    private static int executeAdd(CommandListenerWrapper source, String name, BlockPosition position, Float power, Float fuseTime, BlockPosition triggerPos) {
        Location loc = Utils.getLocationFromBlockPos(source.getWorld().getWorld(), position);
        Location triggerLoc = null;
        Cannon cannon = new Cannon(name, loc);
        if (power != null) {
            cannon.setPower(power);
        }
        if (fuseTime != null) {
            cannon.setFuseTime(fuseTime);
        }
        if (triggerPos != null) {
            triggerLoc = Utils.getLocationFromBlockPos(source.getWorld().getWorld(), triggerPos);
            cannon.setTrigger(triggerLoc);
        }
        Mccanon.getCannonManager().addCannon(cannon);
        source.sendMessage(new ChatMessage("Cannon added with properties: " + cannon), false);
        return 1;
    }

    private static int executeRemove(CommandListenerWrapper source, String name) {
        if (Mccanon.getCannonManager().removeCannonByName(name)) {
            source.sendMessage(new ChatMessage("§aCannon " + name + " was removed"), false);
        } else {
            source.sendMessage(new ChatMessage("§cCannon not found"), false);
        }
        return 1;
    }

    private static int executeList(CommandListenerWrapper source) {
        ObjectCollection<Cannon> cannons = Mccanon.getCannonManager().getCannons();
        if (cannons.size() > 0) {
            source.sendMessage(new ChatMessage("§aCannons:"), false);
            for (Cannon cannon : cannons) {
                source.sendMessage(new ChatMessage(cannon.toString()), false);
            }
        } else {
            source.sendMessage(new ChatMessage("§cNo cannons found"), false);
        }
        return 1;
    }

    private static int executeHelp(CommandListenerWrapper source) {
        source.sendMessage(new ChatMessage("§aHelp: "), false);
        source.sendMessage(new ChatMessage("§a/mccannon add <name> <x> <y> <z> [power] [fuseTime] [triggerX] [triggerY] [triggerZ]"), false);
        source.sendMessage(new ChatMessage("§a/mccannon remove <name>"), false);
        source.sendMessage(new ChatMessage("§a/mccannon list"), false);
        source.sendMessage(new ChatMessage("§a/mccannon info <name>"), false);
        source.sendMessage(new ChatMessage("§a/mccannon set <name> <property> <value>"), false);
        source.sendMessage(new ChatMessage("§a/mccannon fire <name>"), false);
        source.sendMessage(new ChatMessage("§a/mccannon save"), false);
        source.sendMessage(new ChatMessage("§a/mccannon load"), false);
        source.sendMessage(new ChatMessage("§a/mccannon reload"), false);
        return 1;
    }

    private static int executeInfo(CommandListenerWrapper source, String name) {
        Cannon cannon = Mccanon.getCannonManager().getCannonByName(name);
        if (cannon != null) {
            source.sendMessage(new ChatMessage("§aCannon info: " + cannon.toString()), false);
        } else {
            source.sendMessage(new ChatMessage("§cCannon not found"), false);
        }
        return 1;
    }

    private static int executeSet(CommandListenerWrapper source, String name, String property, float value) {
        Cannon cannon = Mccanon.getCannonManager().getCannonByName(name);
        if (cannon != null) {
            if (property.equals("power")) {
                cannon.setPower(value);
            } else if (property.equals("fuseTime")) {
                cannon.setFuseTime(value);
            } else {
                source.sendMessage(new ChatMessage("§cInvalid property"), false);
                return 1;
            }
            source.sendMessage(new ChatMessage("§aCannon " + name + " property " + property + " was set to " + value), false);
        } else {
            source.sendMessage(new ChatMessage("§cCannon not found"), false);
        }
        return 1;
    }

    private static int executeSet(CommandListenerWrapper source, String name, String property, BlockPosition value) {
        Cannon cannon = Mccanon.getCannonManager().getCannonByName(name);
        if (cannon != null) {
            if (property.equals("trigger")) {
                cannon.setTrigger(Utils.getLocationFromBlockPos(source.getWorld().getWorld(), value));
                source.sendMessage(new ChatMessage("§aTrigger set to " + cannon.getTrigger().toString()), false);
            } else if (property.equals("location")) {
                cannon.setLocation(Utils.getLocationFromBlockPos(source.getWorld().getWorld(), value));
                source.sendMessage(new ChatMessage("§aPosition set to " + cannon.getLocation().toString()), false);
            } else {
                source.sendMessage(new ChatMessage("§cInvalid property"), false);
            }
        } else {
            source.sendMessage(new ChatMessage("§cCannon not found"), false);
        }
        return 1;
    }

    private static int executeFire(CommandListenerWrapper source, String name) {
        Cannon cannon = Mccanon.getCannonManager().getCannonByName(name);
        if (cannon != null) {
            cannon.fire();
            source.sendMessage(new ChatMessage("§aCannon " + name + " fired"), false);
            return 1;
        }
        source.sendMessage(new ChatMessage("§cCannon not found"), false);
        return 1;
    }

    private static int executeSave(CommandListenerWrapper source) {
        Mccanon.getCannonManager().saveCannons();
        source.sendMessage(new ChatMessage("§aCannons saved"), false);
        return 1;
    }

    private static int executeLoad(CommandListenerWrapper source) {
        Mccanon.getCannonManager().loadCannons();
        source.sendMessage(new ChatMessage("§aCannons loaded"), false);
        return 1;
    }

    private static int executeReload(CommandListenerWrapper source) {
        Mccanon.getCannonManager().loadCannons();
        source.sendMessage(new ChatMessage("§aCannons reloaded"), false);
        return 1;
    }
}
