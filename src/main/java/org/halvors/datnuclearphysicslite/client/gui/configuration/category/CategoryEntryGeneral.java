package org.halvors.datnuclearphysicslite.client.gui.configuration.category;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.datnuclearphysicslite.common.NuclearPhysics;
import org.halvors.datnuclearphysicslite.common.Reference;
import org.halvors.datnuclearphysicslite.common.utility.LanguageUtility;

@SideOnly(Side.CLIENT)
public class CategoryEntryGeneral extends GuiConfigEntries.CategoryEntry {
    public CategoryEntryGeneral(final GuiConfig guiConfig, final GuiConfigEntries guiConfigEntries, final IConfigElement configElement) {
        super(guiConfig, guiConfigEntries, configElement);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected GuiScreen buildChildScreen() {
        final String category = Configuration.CATEGORY_GENERAL;

        return new GuiConfig(owningScreen,
                new ConfigElement(NuclearPhysics.getConfiguration().getCategory(category)).getChildElements(),
                owningScreen.modID,
                category,
                false,
                false,
                Reference.NAME + " - " + LanguageUtility.transelate("gui.configuration.category." + category));
    }
}