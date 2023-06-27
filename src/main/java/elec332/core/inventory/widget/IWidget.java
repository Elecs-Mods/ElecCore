package elec332.core.inventory.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.IGuiEventListener;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 28-11-2016.
 */
public interface IWidget extends IGuiEventListener {

    IWidget setContainer(IWidgetContainer container);

    IWidget setID(int id);

    void initWidget(IWidgetListener iCrafting);

    void detectAndSendChanges(Iterable<IWidgetListener> crafters);

    void updateProgressbar(int value);

    void readNBTChangesFromPacket(CompoundNBT tagCompound, LogicalSide receiver);

    @OnlyIn(Dist.CLIENT)
    boolean isMouseOver(double mouseX, double mouseY);

    @Override
    @OnlyIn(Dist.CLIENT)
    void mouseMoved(double mouseX, double mouseY);

    @Override
    @OnlyIn(Dist.CLIENT)
    boolean mouseClicked(double mouseX, double mouseY, int button);

    @Override
    @OnlyIn(Dist.CLIENT)
    boolean mouseReleased(double mouseX, double mouseY, int mouseButton);

    @Override
    @OnlyIn(Dist.CLIENT)
    boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY);

    @Override
    @OnlyIn(Dist.CLIENT)
    boolean mouseScrolled(double wheel, double translatedMouseX, double translatedMouseY);

    @Override
    @OnlyIn(Dist.CLIENT)
    boolean keyPressed(int key, int scanCode, int modifiers);

    @Override
    @OnlyIn(Dist.CLIENT)
    boolean keyReleased(int keyCode, int scanCode, int modifiers);

    @Override
    @OnlyIn(Dist.CLIENT)
    boolean charTyped(char typedChar, int keyCode);

    @OnlyIn(Dist.CLIENT)
    void draw(Window window, @Nonnull MatrixStack matrixStack, int guiX, int guiY, double mouseX, double mouseY, float partialTicks);

    boolean isHidden();

    @Nullable
    ToolTip getToolTip(double mouseX, double mouseY);

    default void modifyTooltip(List<ITextComponent> tooltip, int mouseX, int mouseY) {
    }

    default void onWindowClosed(PlayerEntity player) {
    }

}
