package elec332.core.inventory.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.window.Window;
import elec332.core.util.NBTBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * Created by Elec332 on 28-12-2019
 */
public class WidgetScrollArea extends Widget {

    public WidgetScrollArea(int x, int y, int width, int height, final int steps) {
        this(x, y, width, height, () -> steps);
    }

    public WidgetScrollArea(int x, int y, int width, int height, IntSupplier steps) {
        super(x, y, 0, 0, width, height);
        this.listeners = Lists.newArrayList();
        this.steps = steps;
        if (width != 14 || height < 20) {
            throw new UnsupportedOperationException();
        }
    }

    private final List<Runnable> listeners;
    private final IntSupplier steps;
    private final int scrollBarHeight = 16;
    private float scroll;
    private boolean dragging = false;

    public float getScroll() {
        return scroll;
    }

    public void setScroll(float scroll) {
        if (scroll < 0.0F) {
            scroll = 0.0F;
        }
        if (scroll > 1.0F) {
            scroll = 1.0F;
        }
        if (scroll != this.scroll) {
            this.scroll = scroll;
            runListeners();
            sendNBTChangesToServer(new NBTBuilder().setFloat("scroll", this.scroll).get());
        }
    }

    @Override
    public void readNBTChangesFromPacketServerSide(CompoundNBT tagCompound) {
        super.readNBTChangesFromPacketServerSide(tagCompound);
        scroll = tagCompound.getFloat("scroll");
        runListeners();
    }

    public void addListener(Runnable runnable) {
        listeners.add(runnable);
    }

    private void runListeners() {
        listeners.forEach(Runnable::run);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            setScroll((float) (mouseY - (y + scrollBarHeight / 2)) / ((float) height - scrollBarHeight));
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (dragging) {
            setScroll(scroll + (float) dragY / (height - scrollBarHeight));
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseScrolled(double scroll, double mouseX, double mouseY) {
        if (scroll != 0 && shouldInterceptScroll(mouseX, mouseY)) {
            if (scroll > 0) {
                scroll = 1;
            }
            if (scroll < 0) {
                scroll = -1;
            }
            scroll /= steps.getAsInt();
            setScroll(this.scroll - (float) scroll);
            return true;
        }
        return false;
    }

    protected boolean shouldInterceptScroll(double mouseX, double mouseY) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(Window window, @Nonnull MatrixStack matrixStack, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
        drawHollow(guiX, guiY, 0, 0, width, height);
        GuiDraw.drawTexturedModalRect(guiX + x + 1, guiY + y + 1 + (int) ((height - 17) * getScroll()), 180, 113, 12, 15);
    }

}
