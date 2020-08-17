package org.halvors.datnuclearphysicslite.common.tile.particle;

import net.minecraft.tileentity.TileEntity;
import org.halvors.datnuclearphysicslite.api.tile.IElectromagnet;

public class TileElectromagnet extends TileEntity implements IElectromagnet {
    public TileElectromagnet() {

    }

    @Override
    public boolean isRunning() {
        return true;
    }
}