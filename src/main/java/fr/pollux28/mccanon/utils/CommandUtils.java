package fr.pollux28.mccanon.utils;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;

public class CommandUtils {
    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.server.v1_16_R3.CommandListenerWrapper> literal(String string) {
        return net.minecraft.server.v1_16_R3.CommandDispatcher.a(string);
    }

    public static <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String name, ArgumentType<T> a) {
        return net.minecraft.server.v1_16_R3.CommandDispatcher.a(name, a);
    }
}
