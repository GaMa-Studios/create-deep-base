package gama.studios.deepbase;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.AncestorGetter;
import net.neoforged.neoforge.event.level.BlockEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Представляет собой герметичный объем (отсек) подводной базы.
 * Используется для расчета кислорода, давления и герметичности.
 */
public class BaseCompartment {
    public static final int OXYGEN_MAX_FILL_BLOCKS = 500_000; // Лимит из конфига Deep Seas

    private final UUID id;
    private final Set<BlockPos> blocks = new HashSet<>();
    private final ServerLevel level;

    private boolean sealed = false;
    private boolean ventedToAtmosphere = false;
    private int oxygen = 0;
    private int consumption = 0;
    private int plantInflow = 0;

    public BaseCompartment(ServerLevel level, BlockPos seed) {
        this.level = level;
        this.id = UUID.nameUUIDFromBytes((level.dimension().location() + ":" + seed.toShortString()).getBytes());
        // TODO: Реализовать сканирование объема (flood-fill)
        // TODO: Реализовать проверку герметичности
    }

    public UUID getId() {
        return id;
    }

    public Set<BlockPos> getBlocks() {
        return blocks;
    }

    public boolean isSealed() {
        return sealed;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public boolean isVentedToAtmosphere() {
        return ventedToAtmosphere;
    }

    public void setVentedToAtmosphere(boolean vented) {
        this.ventedToAtmosphere = vented;
    }

    public int getOxygen() {
        return oxygen;
    }

    public void addOxygen(int amount) {
        this.oxygen = Math.min(this.oxygen + amount, OXYGEN_MAX_FILL_BLOCKS);
    }

    public void consumeOxygen(int amount) {
        this.oxygen = Math.max(this.oxygen - amount, 0);
    }

    public int getConsumption() {
        return consumption;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    public int getPlantInflow() {
        return plantInflow;
    }

    public void setPlantInflow(int plantInflow) {
        this.plantInflow = plantInflow;
    }

    public int getVolume() {
        return blocks.size();
    }

    @Override
    public String toString() {
        return "BaseCompartment{" +
                "id=" + id +
                ", volume=" + blocks.size() +
                ", sealed=" + sealed +
                ", oxygen=" + oxygen +
                '}';
    }
}