package elec332.core.inventory.tooltip;

import com.google.common.collect.Lists;
import elec332.core.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class ToolTip{

    public ToolTip(){
        this(Collections.emptyList());
    }

    public ToolTip(ColouredString colouredString){
        this(Lists.newArrayList(colouredString));
    }

    public ToolTip(List<ColouredString> s){
        this.tooltip = s.stream().map(ColouredString::toString).collect(Collectors.toList());
    }

    public ToolTip(String s){
        this(Lists.newArrayList(s));
    }

    public ToolTip(List<String> s, Object... o){
        this.tooltip = s;
    }

    public ToolTip setWidth(int width){
        this.width = width;
        return this;
    }

    private int width = -1;
    private final List<String> tooltip;

    public List<String> getTooltip() {
        return tooltip;
    }

    @SideOnly(Side.CLIENT)
    public void renderTooltip(int mouseX, int mouseY, int guiLeft, int guiTop){
        Minecraft mc = Minecraft.getMinecraft();
        GuiUtils.drawHoveringText(null, tooltip, mouseX, mouseY, mc.displayWidth, mc.displayHeight, width, RenderHelper.getMCFontrenderer());
    }

    public static class ColouredString {

        public ColouredString(String s){
            this(TextFormatting.GRAY, s);
        }

        public ColouredString(TextFormatting colour, String s){
            this.string = colour+s;
        }

        private final String string;

        @Override
        public String toString() {
            return this.string;
        }

    }
}
