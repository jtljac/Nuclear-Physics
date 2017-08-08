package org.halvors.quantum.common.item;

import net.minecraft.item.Item;
import org.halvors.quantum.common.Quantum;
import org.halvors.quantum.common.Reference;

/**
 * This is a basic ItemQuantum that is meant to be extended by other Items.
 *
 * @author halvors
 */
public class ItemQuantum extends Item {
	protected final String name;

	public ItemQuantum(String name) {
		this.name = name;

		setUnlocalizedName(name);
		setRegistryName(Reference.ID, name);
		setCreativeTab(Quantum.getCreativeTab());
	}

	public void registerItemModel() {
		Quantum.getProxy().registerItemRenderer(this, 0, name);
	}
}