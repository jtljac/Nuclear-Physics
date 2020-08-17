package org.halvors.datnuclearphysicslite.common.init;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.halvors.datnuclearphysicslite.common.item.ItemBase;
import org.halvors.datnuclearphysicslite.common.item.ItemCell;
import org.halvors.datnuclearphysicslite.common.item.armor.ItemArmorBase;
import org.halvors.datnuclearphysicslite.common.item.armor.ItemArmorHazmat;
import org.halvors.datnuclearphysicslite.common.item.particle.ItemAntimatterCell;
import org.halvors.datnuclearphysicslite.common.item.tool.ItemWrench;

import java.util.HashSet;
import java.util.Set;

public class ModItems {
    public static final Set<Item> items = new HashSet<>();

    // Basic Components
    public static final Item itemWrench = new ItemWrench();
    public static final Item itemCopperWire = new ItemBase("copper_wire");
    public static final Item itemMotor = new ItemBase("motor");

    public static final Item itemPlateBronze = new ItemBase("plate_bronze");
    public static final Item itemPlateSteel = new ItemBase("plate_steel");

    public static final Item itemCircuitBasic = new ItemBase("circuit_basic");
    public static final Item itemCircuitAdvanced = new ItemBase("circuit_advanced");
    public static final Item itemCircuitElite = new ItemBase("circuit_elite");

    // Cells
    public static final Item itemAntimatterCell = new ItemAntimatterCell();
    public static final Item itemCell = new ItemCell();
    public static final Item itemDarkMatterCell = new ItemBase("darkmatter_cell");

    // Hazmat
    public static final ItemArmor itemHazmatMask = new ItemArmorHazmat("hazmat_mask", EntityEquipmentSlot.HEAD);
    public static final ItemArmor itemHazmatBody = new ItemArmorHazmat("hazmat_body", EntityEquipmentSlot.CHEST);
    public static final ItemArmor itemHazmatLeggings = new ItemArmorHazmat("hazmat_leggings", EntityEquipmentSlot.LEGS);
    public static final ItemArmor itemHazmatBoots = new ItemArmorHazmat("hazmat_boots", EntityEquipmentSlot.FEET);

    @EventBusSubscriber
    public static class RegistrationHandler {
        /**
         * Register this mod's {@link Item}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            final Item[] registerItems = {
                    itemWrench,
                    itemCopperWire,
                    itemMotor,

                    itemPlateBronze,
                    itemPlateSteel,

                    itemCircuitBasic,
                    itemCircuitAdvanced,
                    itemCircuitElite,

                    itemAntimatterCell,
                    itemCell,
                    itemDarkMatterCell,
                    itemHazmatMask,
                    itemHazmatBody,
                    itemHazmatLeggings,
                    itemHazmatBoots
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final Item item : registerItems) {
                registry.register(item);

                if (item instanceof ItemBase) {
                    ((ItemBase) item).registerItemModel();
                } else if (item instanceof ItemArmorBase) {
                    ((ItemArmorBase) item).registerItemModel();
                }

                items.add(item);
            }

            // Basic Components
            OreDictionary.registerOre("plateBronze", itemPlateBronze);
            OreDictionary.registerOre("plateSteel", itemPlateSteel);

            OreDictionary.registerOre("circuitBasic", itemCircuitBasic);
            OreDictionary.registerOre("circuitAdvanced", itemCircuitAdvanced);
            OreDictionary.registerOre("circuitElite", itemCircuitElite);

            // Nuclear Physics
            OreDictionary.registerOre("cellEmpty", itemCell);
            OreDictionary.registerOre("cellDarkmatter", itemDarkMatterCell);

            OreDictionary.registerOre("antimatter", new ItemStack(ModItems.itemAntimatterCell, 1, ItemAntimatterCell.EnumAntimatterCell.MILLIGRAM.ordinal()));
            OreDictionary.registerOre("antimatterMilligram", new ItemStack(ModItems.itemAntimatterCell, 1, ItemAntimatterCell.EnumAntimatterCell.MILLIGRAM.ordinal()));
            OreDictionary.registerOre("antimatterGram", new ItemStack(ModItems.itemAntimatterCell, 1, ItemAntimatterCell.EnumAntimatterCell.GRAM.ordinal()));
        }
    }
}
