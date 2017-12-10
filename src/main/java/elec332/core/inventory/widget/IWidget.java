package elec332.core.inventory.widget;

import elec332.core.inventory.IWidgetContainer;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.Window;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 28-11-2016.
 */
public interface IWidget {

    public IWidget setContainer(IWidgetContainer container);

    public IWidget setID(int id);

    public void initWidget(IWidgetListener iCrafting);

    public void detectAndSendChanges(Iterable<IWidgetListener> crafters);

    public void updateProgressbar(int value);

    public void readNBTChangesFromPacket(NBTTagCompound tagCompound, Side receiver);

    public boolean isMouseOver(int mouseX, int mouseY);

    public boolean mouseClicked(int mouseX, int mouseY, int button);

    public boolean keyTyped(char typedChar, int keyCode);

    @SideOnly(Side.CLIENT)
    public boolean handleMouseWheel(int wheel, int translatedMouseX, int translatedMouseY);

    @SideOnly(Side.CLIENT)
    public void draw(Window window, int guiX, int guiY, int mouseX, int mouseY);

    public boolean isHidden();

    @Nullable
    public ToolTip getToolTip(int mouseX, int mouseY);

    public default void modifyTooltip(List<String> tooltip, int mouseX, int mouseY){
    }

    default public void onWindowClosed(EntityPlayer player){
    }

}
