package org.halvors.datnuclearphysicslite.api.tile;

import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * Applied to TileEntities to render a tag above them.
 */

public interface ITagRender {
    /** Gets the list of strings to render above the object.
     * @param player The player this list will display for
     * @param list A List of structs containing the text and colour in order from the bottom
     * @return The HEIGHT in which the render should happen.
     */
    float addInformation(List<TagRenderStruct> list, EntityPlayer player);
}

