package org.halvors.quantum.common.event;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.halvors.quantum.Quantum;
import org.halvors.quantum.api.tile.IElectromagnet;
import org.halvors.quantum.common.event.ThermalEvent.ThermalUpdateEvent;
import org.halvors.quantum.common.grid.IUpdate;
import org.halvors.quantum.common.grid.UpdateTicker;
import org.halvors.quantum.common.thermal.IBoilHandler;
import org.halvors.quantum.common.thermal.ThermalPhysics;
import org.halvors.quantum.common.tile.reactor.fusion.TilePlasma;
import org.halvors.quantum.common.utility.transform.vector.Vector3;
import org.halvors.quantum.common.utility.transform.vector.VectorWorld;

public class ThermalEventHandler {
    @SubscribeEvent
    public void onBoilEvent(BoilEvent event) {
        Vector3 position = event.getPosition();
        Block block = position.getBlock(event.getWorld());

        for (int height = 1; height <= event.getMaxSpread(); height++) {
            TileEntity tileEntity = event.getWorld().getTileEntity(new BlockPos(position.intX(), position.intY() + height, position.intZ()));

            if (tileEntity instanceof IBoilHandler) {
                IBoilHandler handler = (IBoilHandler) tileEntity;
                FluidStack fluid = event.getRemainForSpread(height);

                if (fluid.amount > 0) {
                    if (handler.canFill(EnumFacing.DOWN, fluid.getFluid())) {
                        fluid.amount -= handler.fill(EnumFacing.DOWN, fluid, true);
                    }
                }
            }
        }

        /*
        // Reactors will not actually remove water source blocks, however weapons will.
        if ((block == Blocks.water ||block == Blocks.flowing_water) && position.getBlockMetadata(event.world) == 0 && !event.isReactor) {
            position.setBlock(event.world, Blocks.air);
        }
        */

        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public void onPlasmaSpawnEvent(PlasmaEvent.PlasmaSpawnEvent event) {
        Vector3 position = new Vector3(event.x, event.y, event.z);
        Block block = position.getBlock(event.getWorld());

        if (block != null) {
            TileEntity tile = position.getTileEntity(event.getWorld());

            if (block == Blocks.BEDROCK || block == Blocks.IRON_BLOCK) {
                return;
            }

            if (tile instanceof TilePlasma) {
                ((TilePlasma) tile).setTemperature(event.temperature);

                return;
            }

            if (tile instanceof IElectromagnet) {
                return;
            }
        }

        position.setBlock(event.getWorld(), Quantum.blockPlasma);

        TileEntity tile = position.getTileEntity(event.getWorld());

        if (tile instanceof TilePlasma) {
            ((TilePlasma) tile).setTemperature(event.temperature);
        }
    }

    @SubscribeEvent
    public void onThermalUpdateEvent(ThermalUpdateEvent event) {
        final VectorWorld position = event.position;
        final World world = position.getWorld();
        Block block = position.getBlockS();

        if (block == Quantum.blockElectromagnet) {
            event.heatLoss = event.deltaTemperature * 0.6F;
        }

        // TODO: Synchronized maybe not reqiured for all the following code?
        synchronized (world) {
            if (block.getMaterial() == Material.AIR) {
                event.heatLoss = 0.15F;
            }

            if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                if (event.temperature >= ThermalPhysics.waterBoilTemperature) {
                    Fluid fluidSteam = FluidRegistry.getFluid("steam");

                    if (fluidSteam != null) {
                        // TODO: INCORRECT!
                        int steamMultiplier = 1; // Add this as configuration option?
                        int volume = (int) (FluidContainerRegistry.BUCKET_VOLUME * (event.temperature / ThermalPhysics.waterBoilTemperature) * steamMultiplier);

                        MinecraftForge.EVENT_BUS.post(new BoilEvent(world, position, new FluidStack(FluidRegistry.WATER, volume), new FluidStack(fluidSteam, volume), 2, event.isReactor));
                    }

                    event.heatLoss = 0.2F;
                }
            }

            if (block == Blocks.ICE || block == Blocks.PACKED_ICE) {
                if (event.temperature >= ThermalPhysics.iceMeltTemperature) {
                    UpdateTicker.addNetwork(new IUpdate() {
                        @Override
                        public void update() {
                            position.setBlock(Blocks.FLOWING_WATER);
                        }

                        @Override
                        public boolean canUpdate() {
                            return true;
                        }

                        @Override
                        public boolean continueUpdate() {
                            return false;
                        }
                    });
                }

                event.heatLoss = 0.4F;
            }

            if (block == Blocks.SNOW || block == Blocks.SNOW_LAYER) {
                if (event.temperature >= ThermalPhysics.iceMeltTemperature) {
                    UpdateTicker.addNetwork(new IUpdate() {
                        @Override
                        public void update() {
                            position.setBlock(Blocks.AIR);
                        }

                        @Override
                        public boolean canUpdate() {
                            return true;
                        }

                        @Override
                        public boolean continueUpdate() {
                            return false;
                        }
                    });
                }

                event.heatLoss = 0.4F;
            }
        }
    }
}
