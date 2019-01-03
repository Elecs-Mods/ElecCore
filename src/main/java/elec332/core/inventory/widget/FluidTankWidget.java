package elec332.core.inventory.widget;

import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.Window;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class FluidTankWidget extends Widget {

    public FluidTankWidget(int x, int y, int u, int v, int width, int height, IFluidTank tank) {
        super(x, y, u, v, width, height);
        this.tank = tank;
        if (height < 11) {
            throw new IllegalArgumentException();
        }
    }

    private IFluidTank tank;
    private FluidStack fluidStack;
    private int capacity;

    @Override
    public void detectAndSendChanges(Iterable<IWidgetListener> crafters) {
        if (capacity != tank.getCapacity() || fluidStack != null && !fluidStack.isFluidStackIdentical(tank.getFluid()) || tank.getFluid() != null) {
            for (IWidgetListener iCrafting : crafters) {
                if (iCrafting instanceof EntityPlayerMP) {
                    NBTTagCompound tag = new NBTTagCompound();
                    if (tank.getFluid() != null) {
                        tank.getFluid().writeToNBT(tag);
                    }
                    tag.putInt("capacity_TANK", tank.getCapacity());
                    sendNBTChangesToPlayer((EntityPlayerMP) iCrafting, tag);
                }
            }
            this.capacity = tank.getCapacity();
            this.fluidStack = tank.getFluid() == null ? null : tank.getFluid().copy();
        }
    }

    @Override
    public ToolTip getToolTip(double mouseX, double mouseY) {
        String fluid = (fluidStack == null || fluidStack.getFluid() == null) ? null : fluidStack.getFluid().getName();
        int amount = fluidStack == null ? 0 : fluidStack.amount;
        return new ToolTip(new ToolTip.ColouredString("Fluid: " + fluid + "  Amount: " + amount));
    }

    @Override
    public void readNBTChangesFromPacket(NBTTagCompound tagCompound) {
        this.fluidStack = FluidStack.loadFluidStackFromNBT(tagCompound);
        this.capacity = tagCompound.getInt("capacity_TANK");
    }

    @Override
    public void draw(Window gui, int guiX, int guiY, double mouseX, double mouseY) {
        GlStateManager.pushMatrix();
        drawFluid(guiX, guiY);
        GlStateManager.popMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        int rH = height - 11 + 1; //First pixel
        int p = rH % 6;
        float scale = fluidStack.amount / (float) capacity;
        if (p == 0) {
            GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, 180, 70, width > 12 ? 12 : width, height <= 46 ? height : 46);
            int i = height - 46;
            if (i > 0) {
                for (int j = 0; j < i / 6; j++) {
                    GuiDraw.drawTexturedModalRect(guiX + x, guiY + y + 46 + j * 6, 180, 75, width > 12 ? 12 : width, 5);
                }
            }
        } else {
            int hS = 0;
            if (height >= 46) {
                GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, 180, 70, width > 12 ? 12 : width, 46);
                hS = 46;
                //Nope, too lazy
            }
            int i = p % 2;
            if (i == 1) {
                //Fock off
            }
            //Todo: Finish
        }
        bindTexture(Window.DEFAULT_BACKGROUND);
        GuiDraw.drawTexturedModalRect(guiX + x, guiY + y - 1, x, y - 1, width, height - (int) Math.floor(height * scale) + 1);
        GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, u, v, width, height);
    }

    private void drawFluid(int guiX, int guiY) {
        if (capacity == 0) {
            return;
        }
        if (fluidStack == null || fluidStack.getFluid() == null || fluidStack.amount <= 0) {
            return;
        }
        TextureAtlasSprite fluidIcon = RenderHelper.getFluidTexture(fluidStack.getFluid(), false);

        bindTexture(RenderHelper.getBlocksResourceLocation());
        int wrT = width % 16, hrT = height % 16;
        int widthTimes = MathHelper.floor(width / 16f), heightTimes = MathHelper.floor(height / 16f);
        for (int col = 0; col < widthTimes; col++) {
            for (int row = 0; row <= heightTimes; row++) {
                GuiDraw.drawTexturedModalRect(guiX + x + col * 16, guiY + y + row * 16 - 1, fluidIcon, 16, 16);
            }
        }
        if (wrT != 0) { //KY
            for (int row = 0; row <= heightTimes; row++) {
                GuiDraw.drawTexturedModalRect(guiX + x + widthTimes * 16, guiY + y + row * 16 - 1, fluidIcon, wrT, 16);
            }
        }
        if (hrT != 0) { //KY
            for (int col = 0; col < widthTimes; col++) {
                GuiDraw.drawTexturedModalRect(guiX + x + col * 16, guiY + y + heightTimes * 16 - 1, fluidIcon, 16, hrT);
            }
        }
        if (hrT != 0 && wrT != 0) { //Rlly?
            GuiDraw.drawTexturedModalRect(guiX + x + widthTimes * 16, guiY + y + heightTimes * 16 - 1, fluidIcon, wrT, hrT);
        }
    }

}
