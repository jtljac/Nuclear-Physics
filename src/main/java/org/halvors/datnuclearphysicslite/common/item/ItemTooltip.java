package org.halvors.datnuclearphysicslite.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.datnuclearphysicslite.common.Reference;
import org.halvors.datnuclearphysicslite.common.type.EnumColor;
import org.halvors.datnuclearphysicslite.common.utility.LanguageUtility;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTooltip extends ItemBase {
    public ItemTooltip(final String name) {
        super(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, @Nullable final World world, final List<String> list, final ITooltipFlag flag) {
        final String tooltip = getTranslationKey(itemStack) + ".tooltip";

        if (LanguageUtility.canTranselate(tooltip)) {
            if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                list.add(LanguageUtility.transelate("tooltip." + Reference.ID + ".noShift", EnumColor.AQUA.toString(), EnumColor.GREY.toString()));
            } else {
                list.addAll(LanguageUtility.splitStringPerWord(LanguageUtility.transelate(tooltip), 5));
            }
        }
    }

    @Override
    @Nonnull
    public String getTranslationKey(final ItemStack itemStack) {
        if (itemStack.getHasSubtypes()) {
            return super.getTranslationKey(itemStack) + "." + itemStack.getItemDamage();
        }

        return super.getTranslationKey(itemStack);
    }
}
