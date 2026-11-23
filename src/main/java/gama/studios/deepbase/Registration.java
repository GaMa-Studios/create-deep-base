package gama.studios.deepbase;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.function.Supplier;

/**
 * Центральный реестр всех блоков и предметов мода.
 */
public class Registration {
    // Создаем DeferredRegister для блоков (привязываем к нашему MODID)
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DeepBase.MODID);
    // Создаем DeferredRegister для предметов
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DeepBase.MODID);

    // === НАШ БЛОК ===
    // Создаем "поставщика" (Supplier) для нашего блока
    public static final Supplier<Block> BASE_COMPARTMENT_BLOCK = BLOCKS.register("base_compartment",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5f))
    );

    // === ПРЕДМЕТ ДЛЯ БЛОКА ===
    // Чтобы блок можно было взять в руку и положить в креатив, нужен Item
    public static final Supplier<Item> BASE_COMPARTMENT_ITEM = ITEMS.register("base_compartment",
            () -> new BlockItem(BASE_COMPARTMENT_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    );

    // === РЕГИСТРАЦИЯ ===
    /**
     * Вызывается из главного класса для регистрации всех DeferredRegister.
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}