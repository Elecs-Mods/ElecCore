package elec332.core.inventory.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import elec332.core.client.ClientHelper;
import elec332.core.client.RenderHelper;
import elec332.core.inventory.window.Window;
import elec332.core.util.FMLHelper;
import elec332.core.util.NBTBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 28-12-2019
 */
public class WidgetTextField extends Widget {

    public WidgetTextField(int x, int y, int width, int height, String msg) {
        super(x, y, 0, 0, width, height);
        if (FMLHelper.getLogicalSide().isClient()) {
            textField = new net.minecraft.client.gui.widget.TextFieldWidget(RenderHelper.getMCFontrenderer(), x, y, width, height, new StringTextComponent(msg));
            ClientHelper.getKeyboardListener().enableRepeatEvents(true);
        } else {
            textField = null;
        }
        listeners = Lists.newArrayList();
        text = msg;
    }

    private final List<Consumer<String>> listeners;
    private final Object textField;
    private String text;

    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    private void runListeners(String txt) {
        text = txt;
        listeners.forEach(c -> c.accept(txt));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (textField != null) {
            getTextField().setText(text);
            syncToServer();
            runListeners(text);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void setTextColor(int color) {
        if (textField != null) {
            getTextField().setTextColor(color);
        }
    }

    public void setDisabledTextColour(int color) {
        if (textField != null) {
            getTextField().setDisabledTextColour(color);
        }
    }

    public void setMaxStringLength(int length) {
        if (textField != null) {
            getTextField().setMaxStringLength(length);
        }
    }

    public void setEnableBackgroundDrawing(boolean enable) {
        if (textField != null) {
            getTextField().setEnableBackgroundDrawing(enable);
        }
    }

    @Override
    public void onWindowClosed(PlayerEntity player) {
        if (player.world.isRemote) {
            ClientHelper.getKeyboardListener().enableRepeatEvents(false);
        }
    }

    @Override
    public Widget setHidden(boolean hidden) {
        if (textField != null) {
            getTextField().setVisible(!hidden);
            getTextField().setEnabled(!hidden);
        }
        return super.setHidden(hidden);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        boolean ret = getTextField().keyPressed(key, scanCode, modifiers);
        if (ret && !getText().equals(getTextField().getText())) {
            runListeners(getTextField().getText());
            syncToServer();
        }
        return ret || ClientHelper.getMinecraft().gameSettings.keyBindInventory.matchesKey(key, scanCode);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char typedChar, int keyCode) {
        boolean ret = getTextField().charTyped(typedChar, keyCode);
        if (ret && !getText().equals(getTextField().getText())) {
            runListeners(getTextField().getText());
            syncToServer();
        }
        return ret;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return getTextField().mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void readNBTChangesFromPacketServerSide(CompoundNBT tagCompound) {
        runListeners(tagCompound.getString("txt"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(Window window, @Nonnull MatrixStack matrixStack, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(guiX, guiY, 0);
        getTextField().renderButton(matrixStack, (int) mouseX, (int) mouseX, partialTicks);
        RenderSystem.popMatrix();
    }

    @OnlyIn(Dist.CLIENT)
    private void syncToServer() {
        sendNBTChangesToServer(new NBTBuilder().setString("txt", getTextField().getText()).get());
    }

    @OnlyIn(Dist.CLIENT)
    private net.minecraft.client.gui.widget.TextFieldWidget getTextField() {
        return (net.minecraft.client.gui.widget.TextFieldWidget) textField;
    }

}
