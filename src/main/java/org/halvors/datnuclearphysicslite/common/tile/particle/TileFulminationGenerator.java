package org.halvors.datnuclearphysicslite.common.tile.particle;

import net.minecraft.util.EnumFacing;
import org.halvors.datnuclearphysicslite.common.capabilities.energy.EnergyStorage;
import org.halvors.datnuclearphysicslite.common.event.handler.FulminationEventHandler;
import org.halvors.datnuclearphysicslite.common.tile.TileGenerator;

import java.util.EnumSet;

public class TileFulminationGenerator extends TileGenerator {
    public TileFulminationGenerator() {
        energyStorage = new EnergyStorage(40000);
    }

    @Override
    public void validate() {
        super.validate();

        FulminationEventHandler.register(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        FulminationEventHandler.unregister(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {
            // Slowly lose energy.
            energyStorage.extractEnergy(1, false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public EnumSet<EnumFacing> getExtractingDirections() {
        return EnumSet.allOf(EnumFacing.class);
    }
}
