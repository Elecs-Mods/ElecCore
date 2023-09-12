package elec332.core.inventory.widget;

import elec332.core.api.inventory.IHasProgressBar;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.window.Window;
import net.minecraft.resources.ResourceLocation;

/**
 * Created by Elec332 on 15-8-2015.
 */
public class WidgetProgressArrow extends Widget {

    public WidgetProgressArrow(int x, int y, IHasProgressBar iHasProgressBar, boolean right) {
        super(x, y, 0, 0, 23, 16);
        this.iHasProgressBar = iHasProgressBar;
        this.right = right;
    }

    private boolean right;
    private final IHasProgressBar iHasProgressBar;
    private int lastProgress;

    @Override
    public void detectAndSendChanges(Iterable<IWidgetListener> crafters) {
        int progress = iHasProgressBar.getProgress();
        if (lastProgress != progress) {
            sendProgressBarUpdate(crafters, progress);
            lastProgress = progress;
        }
    }

    @Override
    public void updateProgressbar(int value) {
        this.lastProgress = value;
    }

    @Override
    public void draw(Window gui, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
        float progress = iHasProgressBar.getProgressScaled(lastProgress);
        if (progress > 1) {
            progress = 1;
        }
        if (progress < 0) {
            progress = 0;
        }
        bindTexture(new ResourceLocation("eleccore", "progressbars.png"));
        int fullArrow = (int) (23 * progress);
        if (right) {
            GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, 0, 0, width, height);
            GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, 0, 16, fullArrow, height);
        } else {
            GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, 23, 16, width, height);
            GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, 23, 0, 23 - fullArrow, height);
        }
    }

}
