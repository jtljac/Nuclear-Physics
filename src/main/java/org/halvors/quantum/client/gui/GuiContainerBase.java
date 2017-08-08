package org.halvors.quantum.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.quantum.common.container.ContainerDummy;
import org.halvors.quantum.common.utility.LanguageUtility;
import org.halvors.quantum.common.utility.ResourceUtility;
import org.halvors.quantum.common.utility.energy.UnitDisplay;
import org.halvors.quantum.common.utility.transform.vector.Rectangle;
import org.halvors.quantum.common.utility.transform.vector.Vector2;
import org.halvors.quantum.common.utility.type.ResourceType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiContainerBase extends GuiContainer {
    public static final ResourceLocation GUI_EMPTY_FILE = ResourceUtility.getResource(ResourceType.GUI, "gui_empty.png");
    public static final ResourceLocation GUI_BASE = ResourceUtility.getResource(ResourceType.GUI, "gui_base.png");
    public static final ResourceLocation GUI_COMPONENTS = ResourceUtility.getResource(ResourceType.GUI, "gui_components.png");
    public ResourceLocation baseTexture;

    public enum SlotType {
        NONE,
        BATTERY,
        LIQUID,
        GAS,
        ARR_UP,
        ARR_DOWN,
        ARR_LEFT,
        ARR_RIGHT,
        ARR_UP_RIGHT,
        ARR_UP_LEFT,
        ARR_DOWN_LEFT,
        ARR_DOWN_RIGHT
    }

    protected int meterX = 54;
    protected int meterHeight = 49;
    protected int meterWidth = 14;
    protected int meterEnd = meterX + meterWidth;

    protected static int energyType = 0;

    public String tooltip = "";
    protected HashMap<Rectangle, String> tooltips = new HashMap<Rectangle, String>();

    protected int containerWidth;
    protected int containerHeight;
    private float lastChangeFrameTime;

    public GuiContainerBase(Container container) {
        super(container);
        this.ySize = 217;
        this.baseTexture = GUI_BASE;
    }

    public GuiContainerBase() {
        this(new ContainerDummy());

        baseTexture = GUI_EMPTY_FILE;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);

        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        for (Map.Entry<Rectangle, String> entry : tooltips.entrySet()) {
            if (entry.getKey().isIn(new Vector2(mouseX - guiLeft, mouseY - guiTop))) {
                tooltip = entry.getValue();
                break;
            }
        }

        if (tooltip != null && !tooltip.equals("")) {
            drawTooltip(mouseX - guiLeft, mouseY - guiTop, LanguageUtility.splitStringPerWord(tooltip, 5).toArray(new String[] {}));
        }

        tooltip = null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        this.containerWidth = (width - xSize) / 2;
        this.containerHeight = (height - ySize) / 2;

        this.mc.renderEngine.bindTexture(baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, xSize, ySize);
    }

    protected void drawBulb(int x, int y, boolean isOn) {
        this.mc.renderEngine.bindTexture(this.baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (isOn) {
            drawTexturedModalRect(containerWidth + x, containerHeight + y, 161, 0, 6, 6);

        } else {
            drawTexturedModalRect(containerWidth + x, containerHeight + y, 161, 4, 6, 6);
        }
    }

    protected void drawSlot(int x, int y, ItemStack itemStack)
    {
        this.mc.renderEngine.bindTexture(this.baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

        this.drawItemStack(itemStack, this.containerWidth + x, this.containerHeight + y);
    }

    protected void drawItemStack(ItemStack itemStack, int x, int y) {
        x ++;
        y ++;
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);

        // drawTexturedModelRectFromIcon
        // GL11.glEnable(GL11.GL_BLEND);
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // TODO: Fix item rendering.
        //itemRenderer.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemStack, x, y);

        // GL11.glDisable(GL11.GL_BLEND);
    }

    protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY) {
        this.drawTextWithTooltip(textName, format, x, y, mouseX, mouseY, 4210752);
    }

    protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY, int color) {
        String name = LanguageUtility.transelate("gui." + textName + ".name");
        String text = format.replaceAll("%1", name);
        this.fontRendererObj.drawString(text, x, y, color);

        String tooltip = LanguageUtility.transelate("gui." + textName + ".tooltip");

        if (tooltip != null && !tooltip.equals("")) {
            if (isPointInRegion(x, y, (int) (text.length() * 4.8), 12, mouseX, mouseY)) {
                tooltip = tooltip;
            }
        }
    }

    protected void drawTextWithTooltip(String textName, int x, int y, int mouseX, int mouseY) {
        this.drawTextWithTooltip(textName, "%1", x, y, mouseX, mouseY);
    }

    protected void drawSlot(int x, int y, SlotType type, float r, float g, float b) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(r, g, b, 1.0F);

        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

        if (type != SlotType.NONE) {
            this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18 * type.ordinal(), 18, 18);
        }
    }

    protected void drawSlot(int x, int y, SlotType type) {
        this.drawSlot(x, y, type, 1, 1, 1);
    }

    protected void drawSlot(int x, int y) {
        this.drawSlot(x, y, SlotType.NONE);
    }

    protected void drawBar(int x, int y, float scale) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /** Draw background progress bar/ */
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 0, 22, 15);

        if (scale > 0)
        {
            /** Draw white color actual progress. */
            this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 15, 22 - (int) (scale * 22), 15);
        }
    }

    protected void drawForce(int x, int y, float scale) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /** Draw background progress bar/ */
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);

        if (scale > 0)
        {
            /** Draw white color actual progress. */
            this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, meterX, 11, (int) (scale * 107), 11);
        }
    }

    protected void drawElectricity(int x, int y, float scale) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /** Draw background progress bar/ */
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);

        if (scale > 0)
        {
            /** Draw white color actual progress. */
            this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 22, (int) (scale * 107), 11);
        }
    }

    protected void drawMeter(int x, int y, float scale, float r, float g, float b) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /** Draw the background meter. */
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, this.meterWidth, this.meterHeight);

        /** Draw liquid/gas inside */
        GL11.glColor4f(r, g, b, 1.0F);
        int actualScale = (int) ((this.meterHeight - 1) * scale);
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y + (this.meterHeight - 1 - actualScale), 40, 49, this.meterHeight - 1, actualScale);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        /** Draw measurement lines */
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 49 * 2, this.meterWidth, this.meterHeight);
    }

    protected void drawMeter(int x, int y, float scale, FluidStack liquidStack) {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /** Draw the background meter. */
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, meterWidth, meterHeight);

        /** Draw liquid/gas inside */
        if (liquidStack != null)
            //this.displayGauge(this.containerWidth + x, this.containerHeight + y, -10, 1, 12, (int) ((meterHeight - 1) * scale), liquidStack);

        /** Draw measurement lines */
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 49 * 2, meterWidth, meterHeight);
    }

    public void renderUniversalDisplay(int x, int y, float energy, int mouseX, int mouseY, UnitDisplay.Unit unit) {
        renderUniversalDisplay(x, y, energy, mouseX, mouseY, unit, false);
    }

    public void renderUniversalDisplay(int x, int y, float energy, float maxEnergy, int mouseX, int mouseY, UnitDisplay.Unit unit, boolean small) {
        String displaySuffix = "";

        if (unit == UnitDisplay.Unit.WATT)
        {
            displaySuffix = "/s";
        }

        String display = UnitDisplay.getDisplay(energy, unit, 2, small) + "/" + UnitDisplay.getDisplay(maxEnergy, unit, 2, small);

        // Check different energy system types.
        if (unit == UnitDisplay.Unit.WATT || unit == UnitDisplay.Unit.JOULES)
        {
            switch (energyType)
            {
                case 1:
                    //display = UnitDisplay.roundDecimals(energy * CompatibilityType.BUILDCRAFT.ratio) + " MJ" + "/" + displaySuffix + UnitDisplay.roundDecimals(maxEnergy * CompatibilityType.BUILDCRAFT.ratio) + " MJ" + displaySuffix;
                    break;
                case 2:
                    //display = UnitDisplay.roundDecimals(energy * CompatibilityType.INDUSTRIALCRAFT.ratio) + " EU" + displaySuffix + "/" + UnitDisplay.roundDecimals(maxEnergy * CompatibilityType.INDUSTRIALCRAFT.ratio) + " EU" + displaySuffix;
                    break;
                case 3:
                    //display = UnitDisplay.roundDecimals(energy * CompatibilityType.THERMAL_EXPANSION.ratio) + " RF" + displaySuffix + "/" + UnitDisplay.roundDecimals(maxEnergy * CompatibilityType.THERMAL_EXPANSION.ratio) + " RF" + displaySuffix;
                    break;
            }
        }

        if (this.isPointInRegion(x, y, display.length() * 5, 9, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && this.lastChangeFrameTime <= 0) {
                energyType = (energyType + 1) % 4;
                this.lastChangeFrameTime = 60;
            } else {
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Click to change unit.");
            }
        }

        this.lastChangeFrameTime--;

        this.fontRendererObj.drawString(display, x, y, 4210752);
    }

    public void renderUniversalDisplay(int x, int y, float energy, int mouseX, int mouseY, UnitDisplay.Unit unit, boolean small) {
        String displaySuffix = "";

        if (unit == UnitDisplay.Unit.WATT) {
            displaySuffix = "/s";
        }

        String display = UnitDisplay.getDisplay(energy, unit, 2, small);

        // Check different energy system types.
        if (unit == UnitDisplay.Unit.WATT || unit == UnitDisplay.Unit.JOULES) {
            switch (energyType) {
                case 1:
                    //display = UnitDisplay.roundDecimals(energy * CompatibilityType.BUILDCRAFT.ratio) + " MJ" + displaySuffix;
                    break;

                case 2:
                    //display = UnitDisplay.roundDecimals(energy * CompatibilityType.INDUSTRIALCRAFT.ratio) + " EU" + displaySuffix;
                    break;

                case 3:
                    //display = UnitDisplay.roundDecimals(energy * CompatibilityType.THERMAL_EXPANSION.ratio) + " RF" + displaySuffix;
                    break;
            }
        }

        if (this.isPointInRegion(x, y, display.length() * 5, 9, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && this.lastChangeFrameTime <= 0) {
                energyType = (energyType + 1) % 4;
                this.lastChangeFrameTime = 60;
            } else {
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Click to change unit.");
            }
        }

        this.lastChangeFrameTime--;

        this.fontRendererObj.drawString(display, x, y, 4210752);
    }

    public void drawTooltip(int x, int y, String... toolTips) {
        if (!GuiScreen.isShiftKeyDown()) {
            if (toolTips != null) {
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                GL11.glDisable(GL11.GL_DEPTH_TEST);

                int var5 = 0;
                int var6;
                int var7;

                for (var6 = 0; var6 < toolTips.length; ++var6) {
                    var7 = this.fontRendererObj.getStringWidth(toolTips[var6]);

                    if (var7 > var5) {
                        var5 = var7;
                    }
                }

                var6 = x + 12;
                var7 = y - 12;

                int var9 = 8;

                if (toolTips.length > 1) {
                    var9 += 2 + (toolTips.length - 1) * 10;
                }

                if (this.guiTop + var7 + var9 + 6 > this.height) {
                    var7 = this.height - var9 - this.guiTop - 6;
                }

                this.zLevel = 300;
                int var10 = -267386864;
                this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
                this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
                this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
                this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
                this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
                int var11 = 1347420415;
                int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
                this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
                this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
                this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
                this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

                for (int var13 = 0; var13 < toolTips.length; ++var13) {
                    String var14 = toolTips[var13];

                    fontRendererObj.drawStringWithShadow(var14, var6, var7, -1);
                    var7 += 10;
                }

                zLevel = 0;

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            }
        }
    }

    /*
    protected void displayGauge(int j, int k, int line, int col, int width, int squaled, FluidStack liquid) {
        squaled -= 1;

        if (liquid == null)
        {
            return;
        }

        int start = 0;

        IIcon liquidIcon = null;
        Fluid fluid = liquid.getFluid();

        if (fluid != null && fluid.getStillIcon() != null) {
            liquidIcon = fluid.getStillIcon();
        }

        RenderUtility.setSpriteTexture(fluid.getSpriteNumber());

        if (liquidIcon != null) {
            while (true) {
                int x;

                if (squaled > 16) {
                    x = 16;
                    squaled -= 16;
                } else {
                    x = squaled;
                    squaled = 0;
                }

                drawTexturedModelRectFromIcon(j + col, k + line + 58 - x - start, liquidIcon, width, 16 - (16 - x));
                start = start + 16;

                if (x == 0 || squaled == 0) {
                    break;
                }
            }
        }
    }
    */
}