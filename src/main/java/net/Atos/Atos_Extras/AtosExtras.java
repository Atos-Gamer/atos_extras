package net.Atos.Atos_Extras;

import net.Atos.Atos_Extras.block.AddedBlocks;
import net.Atos.Atos_Extras.commands.DevCommands; // Import DevCommands
import net.Atos.Atos_Extras.item.AddedItems;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(AtosExtras.MOD_ID)
public class AtosExtras
{
    public static final String MOD_ID = "atos_extras";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AtosExtras(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(this);                        // For things like ServerStartingEvent

        // Register DevCommands so your dev-only commands work
        NeoForge.EVENT_BUS.register(DevCommands.class);

        AddedItems.register(modEventBus);                         // Your custom items
        AddedBlocks.register(modEventBus);                        // Your custom blocks
        modEventBus.addListener(this::addCreative);               // Add stuff to creative tabs
        modEventBus.addListener(this::commonSetup);               // <-- Register your common setup logic
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);  // Register the config file
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            event.accept(AddedItems.TESTITEM);
            event.accept(AddedBlocks.TEST_BLOCK);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(AddedItems.ATOSINGOT);
            event.accept(AddedItems.SLIMEINGOT);
            event.accept(AddedItems.ATOSORE);
            event.accept(AddedItems.ATOSDUST);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
