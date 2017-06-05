package org.halvors.quantum.lib.explosion;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.halvors.quantum.Quantum;

import java.util.Random;

public class ReactorExplosion extends Explosion {
    private Random explosionRAND = new Random();
    private World world;

    public ReactorExplosion(World world, Entity par2Entity, double x, double y, double z, float par9) {
        super(world, par2Entity, x, y, z, par9);
        this.world = world;
        this.isFlaming = true;
    }

    @Override
    public void doExplosionB(boolean par1) {
        super.doExplosionB(par1);

        for (Object affectedBlockPosition : affectedBlockPositions) {
            ChunkPosition chunkPosition = (ChunkPosition) affectedBlockPosition;
            int x = chunkPosition.chunkPosX;
            int y = chunkPosition.chunkPosY;
            int z = chunkPosition.chunkPosZ;
            Block block = world.getBlock(x, y, z);
            Block blockUnder = world.getBlock(x, y - 1, z);

            // TODO: Check opaqueCubeLookup. Correct replacement !block.isOpaqueCube()?
            if (block == Blocks.air && !blockUnder.isOpaqueCube() && explosionRAND.nextInt(3) == 0) {
                world.setBlock(x, y, z, Quantum.blockRadioactive);
            }
        }
    }
}