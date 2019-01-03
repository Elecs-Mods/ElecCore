package elec332.core.inventory.widget;

import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

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

    public void readNBTChangesFromPacket(NBTTagCompound tagCompound, LogicalSide receiver);

    public boolean isMouseOver(double mouseX, double mouseY);

    public boolean mouseClicked(double mouseX, double mouseY, int button);

    public boolean keyTyped(char typedChar, int keyCode);

    @OnlyIn(Dist.CLIENT)
    public boolean handleMouseWheel(double wheel, double translatedMouseX, double translatedMouseY);

    @OnlyIn(Dist.CLIENT)
    public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY);

    public boolean isHidden();

    @Nullable
    public ToolTip getToolTip(double mouseX, double mouseY);

    public default void modifyTooltip(List<String> tooltip, int mouseX, int mouseY) {
    }

    default public void onWindowClosed(EntityPlayer player) {
    }

}
