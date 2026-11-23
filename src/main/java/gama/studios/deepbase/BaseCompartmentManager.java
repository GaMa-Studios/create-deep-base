package gama.studios.deepbase;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Управляет всеми отсеками в мире. Хранит их в SavedData, чтобы данные не терялись при перезагрузке.
 */
public class BaseCompartmentManager extends SavedData {
    // Карта: ID отсека -> Сам отсек
    private final Map<UUID, BaseCompartment> compartments = new HashMap<>();
    // Обратная карта: Позиция блока -> ID отсека (для быстрого поиска)
    private final Map<BlockPos, UUID> posToCompartment = new HashMap<>();

    private static final String DATA_NAME = "create_deepbase_compartments";

    /**
     * Получает (или создает) менеджер для конкретного мира.
     */
    public static BaseCompartmentManager get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(
                // Функция создания нового менеджера, если его нет на диске
                tag -> new BaseCompartmentManager(tag),
                // Функция создания пустого менеджера
                () -> new BaseCompartmentManager(),
                DATA_NAME
        );
    }

    // Конструктор для загрузки данных с диска
    public BaseCompartmentManager(CompoundTag tag) {
        this();
        load(tag);
    }

    // Пустой конструктор для создания нового менеджера
    public BaseCompartmentManager() {
        // Здесь можно инициализировать что-то при создании мира
    }

    /**
     * Находит отсек по координатам блока.
     */
    public BaseCompartment getCompartmentAt(BlockPos pos) {
        UUID id = posToCompartment.get(pos);
        if (id != null) {
            return compartments.get(id);
        }
        return null;
    }

    /**
     * Помечает отсек как "грязный". Это заставит Minecraft сохранить данные на диск.
     */
    public void markDirty() {
        setDirty();
    }

    //region Загрузка и сохранение (NBT)

    @Override
    public CompoundTag save(CompoundTag tag) {
        // Сохраняем все отсеки в список
        ListTag compartmentList = new ListTag();
        for (BaseCompartment comp : compartments.values()) {
            CompoundTag compTag = new CompoundTag();
            compTag.putUUID("Id", comp.getId());
            // TODO: Сохранить координаты блоков и флаги
            compartmentList.add(compTag);
        }
        tag.put("Compartments", compartmentList);
        return tag;
    }

    private void load(CompoundTag tag) {
        // Загружаем отсеки с диска
        if (tag.contains("Compartments", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Compartments", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag compTag = list.getCompound(i);
                UUID id = compTag.getUUID("Id");
                // TODO: Восстановить BaseCompartment из NBT
                // Пока просто создаем заглушку
                BaseCompartment comp = new BaseCompartment(null, BlockPos.ZERO); // TODO: Передавать реальный уровень
                comp.setId(id); // TODO: Добавить сеттер в BaseCompartment
                compartments.put(id, comp);
            }
        }
    }

    //endregion
}