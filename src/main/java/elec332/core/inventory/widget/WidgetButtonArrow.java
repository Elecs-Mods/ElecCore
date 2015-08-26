package elec332.core.inventory.widget;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 21-8-2015.
 */
public class WidgetButtonArrow extends WidgetButton {

    public WidgetButtonArrow(int x, int y, Direction direction) {
        super(x, y, direction.u, direction.v, direction.width, direction.height);
        this.direction = direction;
    }

    private final Direction direction;

    @Override
    public void draw(Gui gui, int guiX, int guiY, int mouseX, int mouseY) {
        bindTexture(new ResourceLocation("eleccore", "buttons.png"));
        int u = this.u;
        int v = this.v;
        if (!isActive()){
            u += direction.tw*2;
            v += direction.th*2;
        } else if (isMouseOver(mouseX, mouseY)){
            u += direction.tw;
            v += direction.th;
        }
        gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, width, height);
    }

    @Override
    public WidgetButtonArrow addButtonEvent(IButtonEvent event) {
        super.addButtonEvent(event);
        return this;
    }

    public enum Direction{
        UP(36, 0, 19, 12, 0, 12), DOWN(55, 0, 19, 12, 0, 12), LEFT(0, 19, 12, 19, 12, 0), RIGHT(0, 0, 12, 19, 12, 0);

        private Direction(int u, int v, int width, int height, int tw, int th){
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
            this.tw = tw;
            this.th = th;
        }

        protected final int u, v, width, height, tw, th;
    }
}
