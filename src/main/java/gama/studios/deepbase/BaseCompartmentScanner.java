package gama.studios.deepbase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;

/**
 * Сканирует объем воздуха и определяет герметичность отсека.
 * Использует алгоритм flood-fill (поиск в ширину).
 */
public class BaseCompartmentScanner {

    /**
     * Сканирует объем, начиная с seed-позиции.
     * @param level Уровень (мир)
     * @param seed Начальная позиция (например, блок диффузора)
     * @return Новый BaseCompartment с заполненным списком блоков.
     */
    public static BaseCompartment scan(ServerLevel level, BlockPos seed) {
        BaseCompartment compartment = new BaseCompartment(level, seed);

        // Проверяем, не является ли стартовый блок водой
        if (isWater(level, seed)) {
            return null; // Нельзя начать сканирование из воды
        }

        Deque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new java.util.HashSet<>();

        queue.add(seed);
        visited.add(seed);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            compartment.blocks.add(current);

            // Проверяем 6 соседей (上下左右前后)
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);

                // Если уже посещали - пропускаем
                if (!visited.add(neighbor)) {
                    continue;
                }

                // Если это воздух или пустота - добавляем в очередь
                if (isAirOrEmpty(level, neighbor)) {
                    queue.add(neighbor);
                }
                // Если это вода/лава - проверяем, не пробой ли это
                else if (isWaterOrLava(level, neighbor)) {
                    // TODO: Логика проверки "протечки" (связь с океаном)
                    // Пока просто игнорируем, считаем, что блок внутри базы
                }
            }

            // Защита от лагов: если отсек слишком огромный, прерываем скан
            if (compartment.blocks.size() > BaseCompartment.OXYGEN_MAX_FILL_BLOCKS) {
                BaseCompartment.LOGGER.warn("Compartment scan aborted: exceeded max fill blocks ({})", BaseCompartment.OXYGEN_MAX_FILL_BLOCKS);
                return null; // Или помечаем как INVALID
            }
        }

        // После сканирования проверяем герметичность
        compartment.setSealed(isSealed(level, compartment));
        return compartment;
    }

    //region Helper methods (Checks)

    private static boolean isAirOrEmpty(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // Проверяем на воздух или заменители воздуха (например, стекло, если оно в теге)
        return state.isAir() || state.getBlock() == Blocks.CAVE_AIR || state.getBlock() == Blocks.VOID_AIR;
        // TODO: Добавить поддержку тегов (например, #create:air)
    }

    private static boolean isWater(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).isSourceOfType(Fluids.WATER);
    }

    private static boolean isLava(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).isSourceOfType(Fluids.LAVA);
    }

    private static boolean isWaterOrLava(ServerLevel level, BlockPos pos) {
        return isWater(level, pos) || isLava(level, pos);
    }

    /**
     * Проверяет, герметичен ли отсек.
     * Отсек герметичен, если ни один его блок не граничит с "внешней" водой.
     */
    private static boolean isSealed(ServerLevel level, BaseCompartment compartment) {
        for (BlockPos pos : compartment.blocks) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                // Если соседний блок - вода, и она ведет в океан (не является частью другого отсека)
                if (isWater(level, neighbor)) {
                    // TODO: Проверить, является ли эта вода "пробоем" (connected to open sea)
                    // Для простоты сейчас: если есть вода снаружи - не герметичен
                    return false;
                }
            }
        }
        return true;
    }

    //endregion
}