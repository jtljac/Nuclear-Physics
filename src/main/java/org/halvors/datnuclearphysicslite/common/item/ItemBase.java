package org.halvors.datnuclearphysicslite.common.item;

import net.minecraft.item.Item;
import org.halvors.datnuclearphysicslite.common.NuclearPhysics;
import org.halvors.datnuclearphysicslite.common.Reference;

/**
 * This is a basic ItemBase that is meant to be extended by other Items.
 *
 * @author halvors
 */
public class ItemBase extends Item {
	protected final String name;

	public ItemBase(final String name) {
		this.name = name;

		setTranslationKey(Reference.ID + "." + name);
		setRegistryName(Reference.ID, name);
		setCreativeTab(NuclearPhysics.getCreativeTab());
	}

	public void registerItemModel() {
		NuclearPhysics.getProxy().registerItemRenderer(this, 0, name);
	}
}