package elec332.core.inventory.widget;

import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.IGuiEventListener;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 28-11-2016.
 */
public interface IWidget extends IGuiEventListener {

    public IWidget setContainer(IWidgetContainer container);

    public IWidget setID(int id);

    public void initWidget(IWidgetListener iCrafting);

    public void detectAndSendChanges(Iterable<IWidgetListener> crafters);

    public void updateProgressbar(int value);

    public void readNBTChangesFromPacket(CompoundNBT tagCompound, LogicalSide receiver);

    @OnlyIn(Dist.CLIENT)
    public boolean isMouseOver(double mouseX, double mouseY);

    @Override
    @OnlyIn(Dist.CLIENT)
    public void mouseMoved(double mouseX, double mouseY);

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button);

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton);

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY);

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseScrolled(double wheel, double translatedMouseX, double translatedMouseY);

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int key, int scanCode, int modifiers);

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers);

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char typedChar, int keyCode);

    @OnlyIn(Dist.CLIENT)
    public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY, float partialTicks);

    public boolean isHidden();

    @Nullable
    public ToolTip getToolTip(double mouseX, double mouseY);

    public default void modifyTooltip(List<String> tooltip, int mouseX, int mouseY) {
    }

    default public void onWindowClosed(PlayerEntity player) {
    }

}
