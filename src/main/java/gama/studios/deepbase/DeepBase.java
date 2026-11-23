package gama.studios.deepbase;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DeepBase.MODID)
public class DeepBase {
    public static final String MODID = "create_deepbase";
    // DeferredRegister для блок-сущностей
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    // Регистрация нашего типа блок-сущности
    public static final Supplier<BlockEntityType<BaseCompartmentBlockEntity>> BLOCK_ENTITY_TYPE = BLOCK_ENTITIES.register(
            "base_compartment_be",
            () -> BlockEntityType.Builder.of(BaseCompartmentBlockEntity::new, /* TODO: Здесь будут блоки */).build(null)
    );
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public DeepBase(IEventBus modEventBus) {
        LOGGER.info("Initializing Create: Deep Base...");


        // Регистрируем блоки и предметы
        Registration.register(modEventBus);
        // Регистрируем наши блок-сущности
        BLOCK_ENTITIES.register(modEventBus);
        // Регистрация событий настройки мода
        modEventBus.addListener(this::commonSetup);
        
        // Регистрация на шине NeoForge (для общих событий)
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup for Create: Deep Base complete.");
        // Здесь позже будет инициализация реестров (блоки, предметы)
    }
}