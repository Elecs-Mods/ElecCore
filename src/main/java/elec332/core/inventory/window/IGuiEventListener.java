package elec332.core.inventory.window;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 28-12-2019
 */
public interface IGuiEventListener {

    @OnlyIn(Dist.CLIENT)
    public void mouseMoved(double mouseX, double mouseY);

    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button);

    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton);

    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY);

    @OnlyIn(Dist.CLIENT)
    public boolean mouseScrolled(double wheel, double translatedMouseX, double translatedMouseY);

    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int key, int scanCode, int modifiers);

    @OnlyIn(Dist.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers);

    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char typedChar, int keyCode);

}
