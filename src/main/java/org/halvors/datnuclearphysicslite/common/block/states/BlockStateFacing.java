package org.halvors.datnuclearphysicslite.common.block.states;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import org.halvors.datnuclearphysicslite.common.block.BlockRotatable;

public class BlockStateFacing extends BlockStateContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockStateFacing(final BlockRotatable block) {
        super(block, FACING);
    }

    public BlockStateFacing(final BlockRotatable block, final PropertyEnum typeProperty) {
        super(block, FACING, typeProperty);
    }
}
