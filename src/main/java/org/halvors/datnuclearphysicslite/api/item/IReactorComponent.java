package org.halvors.datnuclearphysicslite.api.item;

import net.minecraft.item.ItemStack;
import org.halvors.datnuclearphysicslite.api.tile.IReactor;

/**
 * All items that act as components in the reactor cell implements this method.
 */
public interface IReactorComponent {
    void onReact(ItemStack itemStack, IReactor reactor);
}
