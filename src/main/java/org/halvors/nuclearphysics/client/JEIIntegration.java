package org.halvors.nuclearphysics.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

import javax.annotation.Nonnull;

@JEIPlugin
public class JEIIntegration implements IModPlugin {
    @Override
    public void register(@Nonnull IModRegistry modRegistry) {
        // modRegistry.addRecipeCategoryCraftingItem();
    }
}
