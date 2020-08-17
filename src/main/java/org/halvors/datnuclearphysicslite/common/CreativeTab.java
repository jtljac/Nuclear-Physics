package org.halvors.datnuclearphysicslite.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.datnuclearphysicslite.common.init.ModBlocks;

import javax.annotation.Nonnull;

/**
 * This is a custom creative tab used only by this mod.
 *
 * @author halvors
 */
public class CreativeTab extends CreativeTabs {
	public CreativeTab() {
		super(Reference.ID);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	@Nonnull
	public ItemStack createIcon() {
		return new ItemStack(Item.getItemFromBlock(ModBlocks.blockElectromagnet));
	}
}