package org.halvors.datnuclearphysicslite.common.block.particle;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.halvors.datnuclearphysicslite.common.block.BlockConnectedTexture;
import org.halvors.datnuclearphysicslite.common.tile.particle.TileFulminationGenerator;

import javax.annotation.Nonnull;

public class BlockFulminationGenerator extends BlockConnectedTexture {
    public BlockFulminationGenerator() {
        super("fulmination_generator", Material.IRON);

        setHardness(10);
        setResistance(25000);
    }

    @Override
    public TileEntity createTileEntity(@Nonnull final World world, @Nonnull final IBlockState state) {
        return new TileFulminationGenerator();
    }
}
