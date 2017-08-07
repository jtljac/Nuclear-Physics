package org.halvors.quantum.atomic.common.tile.particle;

import net.minecraft.util.EnumFacing;
import org.halvors.quantum.atomic.common.tile.TileGenerator;

import java.util.EnumSet;

public class TileFulmination extends TileGenerator {
    public TileFulmination() {
        super((int) 10000000000000L);
    }

    @Override
    public void invalidate() {
        FulminationHandler.INSTANCE.unregister(this);
    }

    @Override
    public void update() {
        super.update();

        if (world.getWorldTime() == 0) {
            FulminationHandler.INSTANCE.register(this);
        }

        generateEnergy();

        // Slowly lose energy.
        energyStorage.extractEnergy(1, false);
    }

    @Override
    public EnumSet<EnumFacing> getExtractingDirections() {
        return EnumSet.allOf(EnumFacing.class);
    }
}