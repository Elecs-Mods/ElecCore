package elec332.core.inventory.tooltip;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import elec332.core.client.util.GuiDraw;
import elec332.core.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class ToolTip {

    public ToolTip() {
        this(Collections.emptyList());
    }

    public ToolTip(ITextComponent colouredString) {
        this(Lists.newArrayList(colouredString));
    }

    public ToolTip(List<ITextComponent> s) {
        this.tooltip = Lists.newArrayList(s);
    }

    public ToolTip(String s) {
        this(Lists.newArrayList(s));
    }

    public ToolTip(List<String> s, Object... o) {
        this(s.stream().map(StringTextComponent::new).collect(Collectors.toList()));
    }

    private int offsetX = 0, offsetY = 0;
    private final List<ITextComponent> tooltip;

    public List<ITextComponent> getTooltip() {
        return tooltip;
    }

    public ToolTip setMouseOffset(int xOffset, int yOffset) {
        this.offsetX = xOffset;
        this.offsetY = yOffset;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTooltip(int mouseX, int mouseY, int guiLeft, int guiTop) {
        renderTooltip(mouseX, mouseY, guiLeft, guiTop, ItemStackHelper.NULL_STACK);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTooltip(int mouseX, int mouseY, int guiLeft, int guiTop, @Nonnull ItemStack stack) {
        mouseX += offsetX;
        mouseY += offsetY;
        GuiDraw.drawHoveringText(tooltip, mouseX, mouseY, Preconditions.checkNotNull(stack).getItem().getFontRenderer(stack));
    }

}
