package elec332.core.inventory.widget;

import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.Window;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
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
                    tag.setInteger("capacity_TANK", tank.getCapacity());
                    sendNBTChangesToPlayer((EntityPlayerMP) iCrafting, tag);
                }
            }
            this.capacity = tank.getCapacity();
            this.fluidStack = tank.getFluid() == null ? null : tank.getFluid().copy();
        }
    }

    @Override
    public ToolTip getToolTip() {
        String fluid = (fluidStack == null || fluidStack.getFluid() == null)?null:fluidStack.getFluid().getName();
        int amount = fluidStack==null?0:fluidStack.amount;
        return new ToolTip(new ToolTip.ColouredString("Fluid: "+fluid+"  Amount: "+amount));
    }

    @Override
    public void readNBTChangesFromPacket(NBTTagCompound tagCompound) {
        this.fluidStack = FluidStack.loadFluidStackFromNBT(tagCompound);
        this.capacity = tagCompound.getInteger("capacity_TANK");
    }

    @Override
    public void draw(Window gui, int guiX, int guiY, int mouseX, int mouseY) {
        if (capacity == 0)
            return;
        if (fluidStack == null || fluidStack.getFluid() == null || fluidStack.amount <= 0)
            return;
        TextureAtlasSprite fluidIcon = RenderHelper.getFluidTexture(fluidStack.getFluid(), false);
        float scale = fluidStack.amount / (float) capacity;
        bindTexture(RenderHelper.getBlocksResourceLocation());
        for (int col = 0; col < width / 16; col++) {
            for (int row = 0; row <= height / 16; row++) {
                GuiDraw.drawTexturedModalRect(guiX + x + col * 16, guiY + y + row * 16 - 1, fluidIcon, 16, 16);
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
        bindTexture(gui.getBackgroundImageLocation());
        GuiDraw.drawTexturedModalRect(guiX + x, guiY + y - 1, x, y - 1, width, height - (int) Math.floor(height * scale) + 1);
        GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, u, v, width, height);
        //gui.drawTexturedModalRect(guiX + x-1, guiY + y - 1, 0, 0, width+1, height - (int) Math.floor(height * scale) + 1);
        //gui.drawTexturedModalRect(guiX + x, guiY + y, width+2, height+2, width, height);
    }

}
