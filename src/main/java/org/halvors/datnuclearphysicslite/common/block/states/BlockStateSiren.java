package org.halvors.datnuclearphysicslite.common.block.states;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import org.halvors.datnuclearphysicslite.common.block.reactor.BlockSiren;

public class BlockStateSiren extends BlockStateContainer {
    public static final PropertyInteger PITCH = PropertyInteger.create("pitch", 0, 15);

    public BlockStateSiren(final BlockSiren block) {
        super(block, PITCH);
    }
}