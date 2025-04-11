package net.Atos.Atos_Extras.item;

import net.Atos.Atos_Extras.AtosExtras;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddedItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AtosExtras.MOD_ID);
    public static final DeferredItem<Item> TESTITEM = ITEMS.register("test_item",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ATOSINGOT = ITEMS.register("atos_ingot",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SLIMEINGOT = ITEMS.register("slime_ingot",
            ()-> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
