package elec332.core.inventory.widget;

import com.google.common.collect.Lists;
import elec332.core.util.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

/**
 * Created by Elec332 on 21-8-2015.
 */
public class WidgetButton extends Widget {

    public WidgetButton(int x, int y, int u, int v, int width, int height) {
        super(x, y, u, v, width, height);
        this.buttonEvents = Lists.newArrayList();
        this.active = true;
    }

    public WidgetButton(int x, int y, int u, int v, int width, int height, IButtonEvent... events){
        this(x, y, u, v, width, height);
        Collections.addAll(buttonEvents, events);
    }

    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    private List<IButtonEvent> buttonEvents;
    private boolean active;
    protected String displayString;

    public WidgetButton setDisplayString(String s){
        this.displayString = s;
        return this;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public final boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && active){
            onButtonClicked();
            sendNBTChangesToServer(new NBTHelper().addToTag(1, "id").toNBT());
            return true;
        }
        return false;
    }

    @Override
    public void readNBTChangesFromPacketServerSide(NBTTagCompound tagCompound) {
        if (tagCompound.getInteger("id") == 1){
            onButtonClicked();
        }
    }

    public void onButtonClicked(){
        sendButtonEvents();
    }

    @Override
    public void draw(Gui gui, int guiX, int guiY, int mouseX, int mouseY) {
        if (!isHidden()) {
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
            bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hovering = isMouseOver(mouseX, mouseY);
            int k = this.getHoverState(hovering);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, 0, 46 + k * 20, this.width / 2, this.height);
            gui.drawTexturedModalRect(guiX + this.x + this.width / 2, guiY + this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            int l = 14737632;
            if (this.isHidden()) {
                l = 10526880;
            } else if (hovering) {
                l = 16777120;
            }
            gui.drawCenteredString(fontrenderer, this.displayString, guiX + this.x + this.width / 2, guiY + this.y + (this.height - 8) / 2, l);
        }
    }

    protected int getHoverState(boolean hovering) {
        byte b = 1;
        if (isHidden()) {
            b = 0;
        } else if (hovering) {
            b = 2;
        }
        return b;
    }

    public void sendButtonEvents(){
        for (IButtonEvent event : buttonEvents)
            event.onButtonClicked(this);
    }

    public WidgetButton addButtonEvent(IButtonEvent event){
        if (!buttonEvents.contains(event))
            buttonEvents.add(event);
        return this;
    }

    public void removeButtonEvent(IButtonEvent event){
        buttonEvents.remove(event);
    }

    public void clearEvents(){
        buttonEvents.clear();
    }

    public static interface IButtonEvent{

        public void onButtonClicked(WidgetButton button);

    }
}
