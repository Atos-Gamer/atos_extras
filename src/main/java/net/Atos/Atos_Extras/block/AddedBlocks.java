package net.Atos.Atos_Extras.block;

import net.Atos.Atos_Extras.AtosExtras;
import net.Atos.Atos_Extras.item.AddedItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AddedBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(AtosExtras.MOD_ID);

    public static final DeferredBlock<Block> TEST_BLOCK = registerBlock("test_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .jumpFactor(15).strength(4f).requiresCorrectToolForDrops().sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> ATOS_ORE_ORE = registerBlock("atos_ore_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .jumpFactor(15).strength(4f).requiresCorrectToolForDrops().sound(SoundType.STONE)));



    public static final DeferredBlock<Block> ATOS_BLOCK = registerBlock("atos_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4f).sound(SoundType.SAND)));

       private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        AddedItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
