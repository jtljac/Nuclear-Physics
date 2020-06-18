package org.halvors.nuclearphysics.common.tile.particle;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.halvors.nuclearphysics.api.tile.IElectromagnet;
import org.halvors.nuclearphysics.common.ConfigurationManager.Energy;
import org.halvors.nuclearphysics.common.ConfigurationManager.General;
import org.halvors.nuclearphysics.common.NuclearPhysics;
import org.halvors.nuclearphysics.common.block.states.BlockStateMachine.EnumMachine;
import org.halvors.nuclearphysics.common.capabilities.energy.EnergyStorage;
import org.halvors.nuclearphysics.common.entity.EntityParticle;
import org.halvors.nuclearphysics.common.init.ModItems;
import org.halvors.nuclearphysics.common.init.ModSoundEvents;
import org.halvors.nuclearphysics.common.item.particle.ItemAntimatterCell;
import org.halvors.nuclearphysics.common.network.packet.PacketTileEntity;
import org.halvors.nuclearphysics.common.tile.TileMachine;
import org.halvors.nuclearphysics.common.utility.InventoryUtility;
import org.halvors.nuclearphysics.common.utility.OreDictionaryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileParticleAccelerator extends TileMachine implements IElectromagnet {
    // NBT
    // Slots
    private static final String NBT_SLOT_INPUT_MATTER = "slotInputMatter";
    private static final String NBT_SLOT_INPUT_CELLS = "slotInputCells";
    private static final String NBT_SLOT_OUTPUT = "slotOutput";
    // Other
    private static final String NBT_TOTAL_ENERGY_CONSUMED = "totalEnergyConsumed";
    private static final String NBT_ANTIMATTER_COUNT = "antimatterCount";


    private static final int ENERGY_PER_TICK = Energy.particleAcceleratorEnergyPerTick; // 19000
    public static final float ANTIMATTER_CREATION_SPEED = 0.9F; // Speed by which a particle will turn into anitmatter.

    // Multiplier that is used to give extra anti-matter based on density (hardness) of a given ore.
    private int particleDensity = General.antimatterParticleDensity;

    // The amount of anti-matter stored within the accelerator. Measured in milligrams.
    private int antimatterCount = 0; // Synced

    // The total amount of energy consumed by this particle.
    public int totalEnergyConsumed = 0; // Synced

    private EntityParticle entityParticle = null;
    private double velocity = 0; // Synced
    private int lastSpawnTick = 0;

    // Inventory
    private IItemHandlerModifiable inventoryInMatter;
    private IItemHandlerModifiable inventoryInCells;
    private IItemHandlerModifiable inventoryOut;

    public TileParticleAccelerator() {
        this(EnumMachine.PARTICLE_ACCELERATOR);
    }

    public TileParticleAccelerator(final EnumMachine type) {
        super(type);

        energyStorage = new EnergyStorage(ENERGY_PER_TICK * 40, ENERGY_PER_TICK);
        createInventories();
    }

    private void createInventories() {
        createInputCells();
        createInputMatter();
        createOutput();
    }

    private void createInputCells() {
        inventoryInCells = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @Override
            public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
                if (!OreDictionaryHelper.isEmptyCell(stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private void createInputMatter() {
        inventoryInMatter = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }
        };
    }

    private void createOutput() {
        inventoryOut = new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @Override
            public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
                return stack;
            }
        };
    }

    public IItemHandlerModifiable getInventoryInCells(){
        return inventoryInCells;
    }

    public IItemHandlerModifiable getInventoryInMatter(){
        return inventoryInMatter;
    }

    public IItemHandlerModifiable getInventoryOut(){
        return inventoryOut;
    }

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing != null) {
                switch (facing) {
                    case UP:
                        return (T) inventoryInMatter;
                    case EAST:
                    case WEST:
                    case NORTH:
                    case SOUTH:
                        return (T) inventoryInCells;
                    case DOWN:
                        return (T) inventoryOut;
                }
            }
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);

        InventoryUtility.readFromNBT(tag, inventoryInCells);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryInCells, null, tag.getTag(NBT_SLOT_INPUT_CELLS));

        InventoryUtility.readFromNBT(tag, inventoryInMatter);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryInMatter, null, tag.getTag(NBT_SLOT_INPUT_MATTER));

        InventoryUtility.readFromNBT(tag, inventoryOut);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryOut, null, tag.getTag(NBT_SLOT_OUTPUT));

        totalEnergyConsumed = tag.getInteger(NBT_TOTAL_ENERGY_CONSUMED);
        antimatterCount = tag.getInteger(NBT_ANTIMATTER_COUNT);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setTag(NBT_SLOT_INPUT_CELLS, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryInCells, null));
        tag.setTag(NBT_SLOT_INPUT_MATTER, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryInMatter, null));
        tag.setTag(NBT_SLOT_OUTPUT, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryOut, null));

        tag.setInteger(NBT_TOTAL_ENERGY_CONSUMED, totalEnergyConsumed);
        tag.setInteger(NBT_ANTIMATTER_COUNT, antimatterCount);

        return tag;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {
            velocity = getParticleVelocity();

            outputAntimatter();

            final ItemStack matter = inventoryInMatter.getStackInSlot(0);

            if (canFunction() && energyStorage.extractEnergy(ENERGY_PER_TICK, true) >= ENERGY_PER_TICK) {
                if (entityParticle == null) {
                    // Creates an accelerated particle if one needs to exist (on world load for example or player login).
                    if (!matter.isEmpty() && lastSpawnTick >= 40) {
                        final BlockPos spawnAcceleratedParticlePos = pos.offset(facing.getOpposite());

                        // Only render the particle if container within the proper environment for it.
                        if (EntityParticle.canSpawnParticle(world, spawnAcceleratedParticlePos)) {
                            // Spawn the particle.
                            totalEnergyConsumed = 0;
                            entityParticle = new EntityParticle(world, spawnAcceleratedParticlePos, pos, facing.getOpposite());
                            world.spawnEntity(entityParticle);

                            // Grabs input block hardness if available, otherwise defaults are used.
                            calculateParticleDensity();

                            // Decrease particle we want to collide.
                            InventoryUtility.decrStackSize(inventoryInMatter, 0);
                            lastSpawnTick = 0;
                        }
                    }
                } else {
                    if (entityParticle.isDead) {
                        // On particle collision we roll the dice to see if dark-matter is generated.
                        if (entityParticle.didCollide()) {
                            if (world.rand.nextFloat() <= 1 ) {// General.darkMatterSpawnChance) {
                                ItemStack itemStack = inventoryOut.getStackInSlot(1);
                                if (!itemStack.isEmpty()) {
                                    // If the output slot is not empty we must increase stack size
                                    if (itemStack.getItem() == ModItems.itemDarkMatterCell && itemStack.getCount() < itemStack.getMaxStackSize()) {
                                        itemStack.setCount(itemStack.getCount() + 1);
                                    }
                                } else {
                                    inventoryOut.setStackInSlot(1, new ItemStack(ModItems.itemDarkMatterCell));
                                }
                            }
                        }

                        entityParticle = null;
                    } else if (velocity > ANTIMATTER_CREATION_SPEED) {
                        // Play sound of anti-matter being created.
                        world.playSound(null, pos, ModSoundEvents.ANTIMATTER, SoundCategory.BLOCKS, 2, 1 - world.rand.nextFloat() * 0.3F);

                        // Create anti-matter in the internal reserve.
                        int generatedAntimatter = 5 + world.rand.nextInt(particleDensity);
                        antimatterCount += generatedAntimatter;

                        // Reset energy consumption levels and destroy accelerated particle.
                        totalEnergyConsumed = 0;
                        entityParticle.setDead();
                        entityParticle = null;
                    }



                    // Plays sound of particle accelerating past the speed based on total velocity at the time of anti-matter creation.
                    if (entityParticle != null) {
                        world.playSound(null, pos, ModSoundEvents.ACCELERATOR, SoundCategory.BLOCKS, 1F, (float) (0.6 + (0.4 * (entityParticle.getVelocity()) / ANTIMATTER_CREATION_SPEED)));
                    }

                    energyUsed = energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                    totalEnergyConsumed += energyUsed;
                }
            } else {
                if (entityParticle != null) {
                    entityParticle.setDead();
                }

                entityParticle = null;
                reset();
            }

            if (world.getWorldTime() % 10 == 0) {
                NuclearPhysics.getPacketHandler().sendToReceivers(new PacketTileEntity(this), this);
            }

            lastSpawnTick++;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handlePacketData(final ByteBuf dataStream) {
        super.handlePacketData(dataStream);

        if (world.isRemote) {
            totalEnergyConsumed = dataStream.readInt();
            antimatterCount = dataStream.readInt();
            velocity = dataStream.readDouble();
        }
    }

    @Override
    public List<Object> getPacketData(final List<Object> objects) {
        super.getPacketData(objects);

        objects.add(totalEnergyConsumed);
        objects.add(antimatterCount);
        objects.add(velocity);

        return objects;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isRunning() {
        return true;
    }

    /**
     * Converts antimatter storage into item if the condition are meet.
     */
    private void outputAntimatter() {
        // Do we have an empty cell in slot one
        final ItemStack itemStackEmptyCell = inventoryInCells.getStackInSlot(0);

        if (OreDictionaryHelper.isEmptyCell(itemStackEmptyCell) && itemStackEmptyCell.getCount() > 0) {
            // Each cell can only hold 125mg of antimatter
            if (antimatterCount >= 125) {
                final ItemStack antiMatterOutput = inventoryOut.getStackInSlot(0);

                if (!antiMatterOutput.isEmpty()) {
                    // If the output slot is not empty we must increase stack size
                    if (antiMatterOutput.getItem() == ModItems.itemAntimatterCell) {
                        final ItemStack newStack = antiMatterOutput.copy();

                        if (newStack.getCount() < newStack.getMaxStackSize()) {
                            InventoryUtility.decrStackSize(inventoryInCells, 0);
                            antimatterCount -= 125;
                            newStack.setCount(newStack.getCount() + 1);
                            inventoryOut.setStackInSlot(0, newStack);
                        }
                    }
                } else {
                    // Remove some of the internal reserves of anti-matter and use it to craft an individual item.
                    antimatterCount -= 125;
                    InventoryUtility.decrStackSize(inventoryInCells, 0);
                    inventoryOut.setStackInSlot(0, new ItemStack(ModItems.itemAntimatterCell));
                }
            }
        }
    }

    private void calculateParticleDensity() {
        final ItemStack itemStack = inventoryInMatter.getStackInSlot(0);

        if (!itemStack.isEmpty()) {
            final Item item = itemStack.getItem();

            if (item instanceof ItemBlock) {
                final IBlockState state = Block.getBlockFromItem(item).getDefaultState();

                // Prevent negative numbers and disallow zero for density multiplier.
                // We can give any BlockPos as argument, it's not used anyway.
                particleDensity = Math.round(state.getBlockHardness(world, pos)) * General.antimatterParticleDensity;
            }

            if (particleDensity < 1) {
                particleDensity = General.antimatterParticleDensity;
            }

            if (particleDensity > 1000) {
                particleDensity = 1000 * General.antimatterParticleDensity;
            }
        }
    }

    // Get velocity for the particle and @return it as a float.
    public double getParticleVelocity() {
        if (entityParticle != null) {
            return entityParticle.getVelocity();
        }

        return 0;
    }

    public EntityParticle getEntityParticle() {
        return entityParticle;
    }

    public void setEntityParticle(final EntityParticle entityParticle) {
        this.entityParticle = entityParticle;
    }

    public int getAntimatterCount() {
        return antimatterCount;
    }

    public double getVelocity() {
        return velocity;
    }
}