package org.halvors.datnuclearphysicslite.common.block.states;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import org.halvors.datnuclearphysicslite.common.NuclearPhysics;
import org.halvors.datnuclearphysicslite.common.block.machine.BlockMachine;
import org.halvors.datnuclearphysicslite.common.tile.machine.TileQuantumAssembler;
import org.halvors.datnuclearphysicslite.common.tile.particle.TileParticleAccelerator;
import org.halvors.datnuclearphysicslite.common.type.EnumParticleType;

public class BlockStateMachine extends BlockStateFacing {
    public static final PropertyEnum<EnumMachine> TYPE = PropertyEnum.create("type", EnumMachine.class);

    public BlockStateMachine(final BlockMachine block) {
        super(block, TYPE);
    }

    public enum EnumMachine implements IStringSerializable {
        PARTICLE_ACCELERATOR(TileParticleAccelerator.class, EnumBlockRenderType.MODEL),
        QUANTUM_ASSEMBLER(TileQuantumAssembler.class);

        private final Class<? extends TileEntity> tileClass;
        private final EnumBlockRenderType renderType;

        private boolean particle;
        private boolean customParticle;
        private EnumParticleTypes particleType;
        private EnumParticleType customParticleType;
        private double particleSpeed;

        EnumMachine(final Class<? extends TileEntity> tileClass, final EnumBlockRenderType renderType) {
            this.tileClass = tileClass;
            this.renderType = renderType;
        }

        EnumMachine(final Class<? extends TileEntity> tileClass) {
            this(tileClass, EnumBlockRenderType.ENTITYBLOCK_ANIMATED);
        }

        EnumMachine(final Class<? extends TileEntity> tileClass, final EnumParticleTypes particleType, final double particleSpeed) {
            this(tileClass);

            this.particle = true;
            this.particleType = particleType;
            this.particleSpeed = particleSpeed;
        }

        EnumMachine(final Class<? extends TileEntity> tileClass, final EnumParticleTypes particleType) {
            this(tileClass, particleType, 0);
        }

        EnumMachine(final Class<? extends TileEntity> tileClass, final EnumParticleType customParticleType, final double particleSpeed) {
            this(tileClass);

            this.particle = true;
            this.customParticle = true;
            this.customParticleType = customParticleType;
            this.particleSpeed = particleSpeed;
        }

        EnumMachine(final Class<? extends TileEntity> tileClass, final EnumParticleType customParticleType) {
            this(tileClass, customParticleType, 0);
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public Class<? extends TileEntity> getTileClass() {
            return tileClass;
        }

        public TileEntity getTileAsInstance() {
            try {
                return tileClass.newInstance();
            } catch (Exception e) {
                NuclearPhysics.getLogger().error("Unable to indirectly create tile entity.");
                e.printStackTrace();
            }

            return null;
        }

        public EnumBlockRenderType getRenderType() {
            return renderType;
        }

        public boolean hasParticle() {
            return particle;
        }

        public boolean hasCustomParticle() {
            return customParticle;
        }

        public EnumParticleTypes getParticleType() {
            return particleType;
        }

        public EnumParticleType getCustomParticleType() {
            return customParticleType;
        }

        public double getParticleSpeed() {
            return particleSpeed;
        }
    }
}