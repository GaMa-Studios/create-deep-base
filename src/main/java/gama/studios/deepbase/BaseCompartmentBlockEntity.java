package gama.studios.deepbase;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Блок-сущность для базовых блоков (например, диффузора).
 * Отвечает за запуск сканирования и регистрацию отсека.
 */
public class BaseCompartmentBlockEntity extends BlockEntity {

    public BaseCompartmentBlockEntity(BlockPos pos, BlockState state) {
        super(DeepBase.BLOCK_ENTITY_TYPE.get(), pos, state); // TODO: Создать BLOCK_ENTITY_TYPE в DeepBase.java
    }

    /**
     * Вызывается, когда блок поставлен в мир.
     */
    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            // Запускаем сканирование при загрузке блока
            scanAndRegister(serverLevel);
        }
    }

    /**
     * Основной метод: сканирует объем и регистрирует его в менеджере.
     */
    public void scanAndRegister(ServerLevel level) {
        // 1. Запускаем сканирование от позиции этого блока
        BaseCompartment compartment = BaseCompartmentScanner.scan(level, getBlockPos());

        if (compartment != null) {
            // 2. Получаем менеджер для этого мира
            BaseCompartmentManager manager = BaseCompartmentManager.get(level);

            // 3. Добавляем отсек в менеджер
            manager.compartments.put(compartment.getId(), compartment);
            for (BlockPos pos : compartment.blocks) {
                manager.posToCompartment.put(pos, compartment.getId());
            }

            // 4. Помечаем менеджер как "грязный" (требует сохранения)
            manager.markDirty();

            DeepBase.LOGGER.info("Registered new compartment {} with volume {}", compartment.getId(), compartment.getVolume());
        } else {
            DeepBase.LOGGER.warn("Failed to create compartment at {}", getBlockPos());
        }
    }

    /**
     * Вызывается, когда блок сломан.
     */
    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level instanceof ServerLevel serverLevel) {
            // TODO: Удалить отсек из менеджера при разрушении блока
            // (Для простоты пока оставим отсеки в мире навсегда)
        }
    }

    //region Ticker (Статический метод для регистрации в реестре)

    /**
     * Этот метод будет вызываться каждый тик для всех блоков этого типа.
     */
    public static void tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull BaseCompartmentBlockEntity be) {
        // Здесь будет логика расхода кислорода (tickServer)
        // TODO: Реализовать потребление кислорода игроками
    }

    //endregion
}