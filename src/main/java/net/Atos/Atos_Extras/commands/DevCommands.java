package net.Atos.Atos_Extras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Set;
import java.util.UUID;

public class DevCommands {

    private static final Set<String> ALLOWED_USERS = Set.of("Dev", "Atos_Gamer");

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // /onlydev command for Devs only
        dispatcher.register(Commands.literal("onlydev")
                .requires(source -> {
                    if (source.getEntity() != null) {
                        String name = source.getEntity().getName().getString();
                        return ALLOWED_USERS.contains(name);
                    }
                    return false;
                })
                .executes(DevCommands::runBaseDevCommand)

                // /onlydev give <itemID>
                .then(Commands.literal("give")
                        .then(Commands.argument("itemID", StringArgumentType.string())
                                .executes(ctx -> runFlexibleGiveCommand(ctx, StringArgumentType.getString(ctx, "itemID")))
                        )
                )

                // /onlydev playerdata [playerUUID]
                .then(Commands.literal("playerdata")
                        .executes(ctx -> runDebugCommand(ctx, null))
                        .then(Commands.argument("playerUUID", StringArgumentType.string())
                                .executes(ctx -> runDebugCommand(ctx, StringArgumentType.getString(ctx, "playerUUID")))
                        )
                )
        );

        // /playerdata command for anyone permission 3+
        dispatcher.register(Commands.literal("playerdata")
                .requires(source -> source.hasPermission(3))
                .executes(ctx -> runDebugCommand(ctx, null))
                .then(Commands.argument("playerUUID", StringArgumentType.string())
                        .executes(ctx -> runDebugCommand(ctx, StringArgumentType.getString(ctx, "playerUUID")))
                )
        );
    }

    private static int runBaseDevCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.literal("✅ Logged in as a trusted developer."), false);
        return 1;
    }

    // /onlydev give <itemID>
    private static int runFlexibleGiveCommand(CommandContext<CommandSourceStack> context, String itemID) {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("❌ This command must be run by a player."));
            return 0;
        }

        ResourceLocation itemRL = ResourceLocation.tryParse(itemID);
        if (itemRL == null || !net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(itemRL)) {
            source.sendFailure(Component.literal("❌ Invalid item ID: " + itemID));
            return 0;
        }

        Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemRL);

        if (item == Items.AIR) {
            source.sendFailure(Component.literal("❌ Cannot give AIR."));
            return 0;
        }

        ItemStack stack = new ItemStack(item, 64);
        boolean success = player.getInventory().add(stack);

        if (success) {
            source.sendSuccess(() -> Component.literal("✅ Gave 64x " + itemID), false);
        } else {
            source.sendFailure(Component.literal("❌ Player inventory full or item invalid."));
        }

        return 1;
    }



    // /onlydev playerdata [uuid] and /playerdata [uuid]
    private static int runDebugCommand(CommandContext<CommandSourceStack> context, String uuidStr) {
        CommandSourceStack source = context.getSource();

        ServerPlayer targetPlayer;

        if (uuidStr == null) {
            if (source.getEntity() instanceof ServerPlayer player) {
                targetPlayer = player;
            } else {
                source.sendFailure(Component.literal("❌ This command must be run by a player or with a valid UUID."));
                return 0;
            }
        } else {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                targetPlayer = source.getServer().getPlayerList().getPlayer(uuid);
                if (targetPlayer == null) {
                    source.sendFailure(Component.literal("❌ Player with UUID " + uuidStr + " not found or offline."));
                    return 0;
                }
            } catch (IllegalArgumentException ex) {
                source.sendFailure(Component.literal("❌ Invalid UUID format: " + uuidStr));
                return 0;
            }
        }

        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("Player Debug Info:\n");
        debugInfo.append("Name: ").append(targetPlayer.getName().getString()).append("\n");
        debugInfo.append("UUID: ").append(targetPlayer.getUUID().toString()).append("\n");
        debugInfo.append(String.format("Position: X=%.2f, Y=%.2f, Z=%.2f\n",
                targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ()));
        debugInfo.append("Health: ").append(targetPlayer.getHealth()).append(" / ").append(targetPlayer.getMaxHealth()).append("\n");
        debugInfo.append("Food Level: ").append(targetPlayer.getFoodData().getFoodLevel()).append("\n");
        debugInfo.append("Experience Level: ").append(targetPlayer.experienceLevel).append("\n");

        for (String line : debugInfo.toString().split("\n")) {
            source.sendSuccess(() -> Component.literal(line), false);
        }

        return 1;
    }
}
