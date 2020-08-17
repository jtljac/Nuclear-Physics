package org.halvors.datnuclearphysicslite.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.halvors.datnuclearphysicslite.common.Reference;
import org.halvors.datnuclearphysicslite.common.block.BlockBase;
import org.halvors.datnuclearphysicslite.common.block.machine.BlockMachine;
import org.halvors.datnuclearphysicslite.common.block.particle.BlockFulminationGenerator;
import org.halvors.datnuclearphysicslite.common.block.reactor.BlockSiren;
import org.halvors.datnuclearphysicslite.common.block.reactor.fission.BlockRadioactiveGrass;
import org.halvors.datnuclearphysicslite.common.block.reactor.fusion.BlockElectromagnet;
import org.halvors.datnuclearphysicslite.common.block.states.BlockStateMachine.EnumMachine;
import org.halvors.datnuclearphysicslite.common.item.block.ItemBlockMetadata;
import org.halvors.datnuclearphysicslite.common.item.block.ItemBlockTooltip;
import org.halvors.datnuclearphysicslite.common.tile.particle.TileElectromagnet;
import org.halvors.datnuclearphysicslite.common.tile.particle.TileFulminationGenerator;
import org.halvors.datnuclearphysicslite.common.tile.reactor.TileSiren;

import java.util.HashSet;
import java.util.Set;

public class ModBlocks {
    public static final Set<ItemBlock> itemBlocks = new HashSet<>();

    public static final Block blockElectromagnet = new BlockElectromagnet();
    public static final Block blockFulmination = new BlockFulminationGenerator();
    public static final Block blockMachine = new BlockMachine();
    public static final Block blockSiren = new BlockSiren();
    public static final Block blockRadioactiveGrass = new BlockRadioactiveGrass();

    @EventBusSubscriber
    public static class RegistrationHandler {
        /**
         * Register this mod's {@link Block}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerBlocks(final Register<Block> event) {
            final IForgeRegistry<Block> registry = event.getRegistry();

            final Block[] registerBlocks = {
                    blockElectromagnet,
                    blockFulmination,
                    blockMachine,
                    blockSiren,
                    blockRadioactiveGrass
            };

            registry.registerAll(registerBlocks);

            registerTileEntities();
        }

        /**
         * Register this mod's {@link ItemBlock}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItemBlocks(final Register<Item> event) {
            final ItemBlock[] registerItems = {
                    new ItemBlockMetadata(blockElectromagnet),
                    new ItemBlockTooltip(blockFulmination),
                    new ItemBlockMetadata(blockMachine),
                    new ItemBlockTooltip(blockSiren),
                    new ItemBlockTooltip(blockRadioactiveGrass),
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : registerItems) {
                final BlockBase block = (BlockBase) item.getBlock();
                //final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
                //registry.register(item.setRegistryName(registryName));
                registry.register(item);

                block.registerItemModel(item);
                block.registerBlockModel();

                itemBlocks.add(item);
            }

            OreDictionary.registerOre("blockRadioactiveGrass", blockRadioactiveGrass);
        }
    }

    private static void registerTileEntities() {
        for (EnumMachine type : EnumMachine.values()) {
            registerTile(type.getTileClass());
        }

        registerTile(TileElectromagnet.class);
        registerTile(TileFulminationGenerator.class);
        registerTile(TileSiren.class);
    }

    private static void registerTile(final Class<? extends TileEntity> tileClass) {
        final String name = tileClass.getSimpleName().replaceAll("(.)(\\p{Lu})", "$1_$2").toLowerCase();

        GameRegistry.registerTileEntity(tileClass, Reference.PREFIX + name);
    }
}
