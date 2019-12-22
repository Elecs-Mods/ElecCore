package elec332.core.inventory.window;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import elec332.core.ElecCore;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.widget.IWidget;
import elec332.core.inventory.widget.IWidgetListener;
import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.inventory.widget.slot.WidgetSlotOutput;
import elec332.core.util.ItemStackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Elec332 on 28-11-2016.
 */
public class Window implements IWidgetContainer {

    public Window() {
        this(-1, -1);
    }

    public Window(int xSize, int ySize, IWindowModifier... modifiers) {
        hasInit = false;
        this.widgets = Lists.newArrayList();
        this.widgets_ = Collections.unmodifiableList(widgets);
        this.playerList = Sets.newHashSet();
        this.xSize = xSize == -1 ? defaultX : xSize;
        this.ySize = ySize == -1 ? defaultY : ySize;
        this.map = Maps.newHashMap();
        this.modifiers = modifiers == null ? Lists.newArrayList() : Lists.newArrayList(modifiers);
        this.normalSize = this.xSize == defaultX && this.ySize == defaultY;
        this.tooBig = this.xSize < defaultX || this.xSize > (defaultX - 6) * 2 || this.ySize < defaultY || this.ySize > (defaultY - 6) * 2;
    }

    final void setContainer(IWindowContainer windowContainer) {
        this.windowContainer = windowContainer;
    }

    final void initWindow_() {
        if (modifiers != null) {
            for (IWindowModifier modifier : modifiers) {
                modifier.modifyWindow(this);
            }
        }
        initWindow();
        hasInit = true;
    }

    protected void initWindow() {

    }

    public Window addModifier(IWindowModifier modifier) {
        if (!hasInit) {
            modifiers.add(modifier);
        } else {
            modifier.modifyWindow(this);
        }
        return this;
    }

    private static final int defaultX = 176, defaultY = 166;
    private final List<IWindowModifier> modifiers;
    private boolean hasInit;
    private final boolean normalSize, tooBig;

    private final List<IWidget> widgets, widgets_;
    private final Map<IWidget, Integer> map;
    private final Set<PlayerEntity> playerList;
    private IWindowContainer windowContainer;
    private ResourceLocation background;
    private int offset;
    private int playerInvIndexStart = 0;
    private int playerInvIndexStop = 0;

    /**
     * The X size of the inventory window in pixels.
     */
    public final int xSize;
    /**
     * The Y size of the inventory window in pixels.
     */
    public final int ySize;
    /**
     * The width of the screen object.
     */
    @OnlyIn(Dist.CLIENT)
    protected int width;
    /**
     * The height of the screen object.
     */
    @OnlyIn(Dist.CLIENT)
    protected int height;
    /**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    @OnlyIn(Dist.CLIENT)
    public int guiLeft;
    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    @OnlyIn(Dist.CLIENT)
    public int guiTop;

    @Override
    public final List<IWidget> getWidgets() {
        return widgets_;
    }

    @Override
    public <W extends IWidget> W addWidget(W widget) {
        widget.setContainer(this);
        widget.setID(widgets.size());
        WidgetListener wl = new WidgetListener();
        wl.setWidget(widget);
        for (IWindowListener obj : getListeners()) {
            wl.setListener(obj);
            widget.initWidget(wl);
        }
        this.widgets.add(widget);
        if (widget instanceof WidgetSlot) {
            windowContainer.addSlotToWindow((WidgetSlot) widget);
        }
        return widget;
    }

    public Window addPlayerInventoryToContainer() {
        this.playerInvIndexStart = windowContainer.getSlotListSize();
        IItemHandler playerInv = new PlayerMainInvWrapper(getPlayer().inventory);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addWidget(new WidgetSlot(playerInv, j + i * 9 + 9, 8 + j * 18, (84 + this.offset) + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addWidget(new WidgetSlot(playerInv, i, 8 + i * 18, 142 + this.offset));
        }
        this.playerInvIndexStop = windowContainer.getSlotListSize();
        return this;
    }

    @Nonnull
    protected ItemStack transferStackInSlot(PlayerEntity player, int slotID) {
        ItemStack itemstack = ItemStackHelper.NULL_STACK;
        WidgetSlot slot = windowContainer.getSlot(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (slot instanceof WidgetSlotOutput) {
                if (!this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop, true)) {
                    return ItemStackHelper.NULL_STACK;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (slotID >= playerInvIndexStart && slotID <= playerInvIndexStop) {
                for (int i = 0; i < playerInvIndexStart; i++) {
                    WidgetSlot slot1 = windowContainer.getSlot(i);
                    if (slot1.isItemValid(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
                            if (itemstack1.getCount() < 1)
                                putStackInSlot(slotID, ItemStackHelper.NULL_STACK);  //Workaround for ghost itemstacks
                            return ItemStackHelper.NULL_STACK;
                        }
                    }
                }
                if (slotID >= playerInvIndexStart && slotID < playerInvIndexStop - 9) {
                    if (!this.mergeItemStack(itemstack1, playerInvIndexStop - 9, playerInvIndexStop, false)) {
                        if (itemstack1.getCount() < 1)
                            putStackInSlot(slotID, ItemStackHelper.NULL_STACK);  //Workaround for ghost itemstacks
                        return ItemStackHelper.NULL_STACK;
                    }
                } else if (slotID >= playerInvIndexStop - 9 && slotID < playerInvIndexStop && !this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop - 9, false)) {
                    if (itemstack1.getCount() < 1)
                        putStackInSlot(slotID, ItemStackHelper.NULL_STACK);  //Workaround for ghost itemstacks
                    return ItemStackHelper.NULL_STACK;
                }
            } else if (!this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop, false)) {
                return ItemStackHelper.NULL_STACK;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStackHelper.NULL_STACK);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStackHelper.NULL_STACK;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Nonnull
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        return slotClickDefault(slotId, dragType, clickTypeIn, player);
    }

    public boolean canMergeSlot(ItemStack stack, WidgetSlot slot) {
        return slot.canMergeSlot(stack);
    }

    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return windowContainer.mergeItemStackDefault(stack, startIndex, endIndex, reverseDirection);
    }

    /**
     * gets whether or not the player can craft in this inventory or not
     */
    public boolean getCanCraft(@Nonnull PlayerEntity player) {
        return !this.playerList.contains(player);
    }

    /**
     * sets whether the player can craft in this inventory or not
     */
    public void setCanCraft(@Nonnull PlayerEntity player, boolean canCraft) {
        if (canCraft) {
            this.playerList.remove(player);
        } else {
            this.playerList.add(player);
        }
    }


    public void onListenerAdded(IWindowListener listener) {
        WidgetListener wl = new WidgetListener();
        wl.setListener(listener);
        for (IWidget widget : widgets) {
            wl.setWidget(widget);
            widget.initWidget(wl);
        }
    }

    public final Window setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public Window setBackground(ResourceLocation background) {
        this.background = background;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public void updateProgressBar(int id, int data) {
        widgets.get(id).updateProgressbar(data);
    }

    public void detectAndSendChanges(Container from) {
        final WidgetListener wl = new WidgetListener();
        for (IWidget widget : widgets) {
            wl.setWidget(widget);
            widget.detectAndSendChanges(new Iterable<IWidgetListener>() {

                @Nonnull
                @Override
                public Iterator<IWidgetListener> iterator() {

                    return new Iterator<IWidgetListener>() {

                        private int index = 0;

                        @Override
                        public boolean hasNext() {
                            return index < getListeners().size();
                        }

                        @Override
                        public IWidgetListener next() {
                            wl.setListener(getListeners().get(index));
                            index++;
                            return wl;
                        }

                    };

                }

            });
        }
    }

    public void onWindowClosed(PlayerEntity playerIn) {
        for (IWidget widget : widgets_) {
            widget.onWindowClosed(playerIn);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.detectAndSendChanges();
    }

    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }

    /**
     * Puts an ItemStack in a slot.
     */
    public void putStackInSlot(int slotID, @Nonnull ItemStack stack) {
        windowContainer.getSlot(slotID).putStack(stack);
    }

    public void onPacket(CompoundNBT tag, LogicalSide receiver) {
    }

    public void modifyTooltip(List<String> tooltip, WidgetSlot slot, ItemStack stack, int x, int y) {
        modifyTooltip(tooltip, slot, x, y);
    }

    public void modifyTooltip(List<String> tooltip, IWidget widget, int mouseX, int mouseY) {
        widget.modifyTooltip(tooltip, mouseX, mouseY);
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleMouseClick(@Nullable WidgetSlot slotIn, int slotId, int mouseButton, @Nonnull ClickType type) {
        windowContainer.handleMouseClickDefault(slotIn, slotId, mouseButton, type);
    }

    @OnlyIn(Dist.CLIENT)
    protected boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IWidget widget : getWidgets()) {
            if (!widget.isHidden() && widget.isMouseOver(translatedMouseX(mouseX), translatedMouseY(mouseY)) && widget.mouseClicked(translatedMouseX(mouseX), translatedMouseY(mouseY), button)) {
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    protected boolean keyTyped(char typedChar, int keyCode) {
        for (IWidget widget : getWidgets()) {
            if (!widget.isHidden() && widget.keyTyped(typedChar, keyCode)) {
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    protected boolean handleMouseWheel(double wheel, double translatedMouseX, double translatedMouseY) {
        for (IWidget widget : widgets) {
            if (widget.handleMouseWheel(wheel, translatedMouseX, translatedMouseY)) {
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

    }

    @OnlyIn(Dist.CLIENT)
    protected void drawScreenPre(int mouseX, int mouseY, float partialTicks) {
        GuiDraw.drawDefaultBackground();
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawScreenPost(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        for (IWidget widget : getWidgets()) {
            if (widget.isHidden()) {
                continue;
            }
            if (widget.isMouseOver(translatedMouseX(mouseX), translatedMouseY(mouseY))) {
                ToolTip toolTip = widget.getToolTip(translatedMouseX(mouseX), translatedMouseY(mouseY));
                if (toolTip != null) {
                    if (widget instanceof WidgetSlot) {
                        modifyTooltip(toolTip.getTooltip(), (WidgetSlot) widget, ((WidgetSlot) widget).getStack(), mouseX, mouseY);
                    } else {
                        modifyTooltip(toolTip.getTooltip(), widget, mouseX, mouseY);
                    }
                    toolTip.renderTooltip(mouseX, mouseY, this.guiLeft, this.guiTop);
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepthTest();
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation background = getBackgroundImageLocation();
        if (background != null) {
            RenderHelper.bindTexture(background);
            GuiDraw.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        } else {
            RenderHelper.bindTexture(DEFAULT_BACKGROUND);
            if (normalSize) {
                GuiDraw.drawTexturedModalRect(k, l, 0, 0, defaultX, defaultY);
            } else {
                if (!tooBig) {
                    int xS = defaultX - 6;
                    int xY = defaultY - 6;
                    GuiDraw.drawTexturedModalRect(k, l, 0, 0, xS, xY);
                    GuiDraw.drawTexturedModalRect(k + xSize - xS, l, 6, 0, xS, xY);
                    GuiDraw.drawTexturedModalRect(k + xSize - xS, l + ySize - xY, 6, 6, xS, xY);
                    GuiDraw.drawTexturedModalRect(k, l + ySize - xY, 0, 6, xS, xY);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        drawWidgets(k, l, mouseX, mouseY);
        GlStateManager.popMatrix();
    }

    protected void drawWidgets(int k, int l, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        for (IWidget widget : getWidgets()) {
            if (!widget.isHidden()) {
                widget.draw(this, k, l, translatedMouseX(mouseX), translatedMouseY(mouseY));
            }
        }
        GlStateManager.popMatrix();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean doesWindowPauseGame() {
        return false;
    }

    //Direct link-through

    public WidgetSlot getSlot(int id) {
        return windowContainer.getSlot(id);
    }

    public int getSlotListSize() {
        return windowContainer.getSlotListSize();
    }

    public final void detectAndSendChanges() {
        windowContainer.detectAndSendChanges();
    }

    public final PlayerEntity getPlayer() {
        return windowContainer == null ? WindowManager.currentOpeningPlayer.get() : windowContainer.getPlayer();
    }

    public final List<IWindowListener> getListeners() {
        return windowContainer.getListeners();
    }

    public boolean mergeItemStackDefault(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return windowContainer.mergeItemStackDefault(stack, startIndex, endIndex, reverseDirection);
    }

    @Nonnull
    protected final ItemStack slotClickDefault(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        return windowContainer.slotClickDefault(slotId, dragType, clickTypeIn, player);
    }

    protected final void sendPacket(CompoundNBT tag) {
        windowContainer.sendPacket(tag);
    }

    protected double translatedMouseX(double mouseX) {
        return mouseX - this.guiLeft;
    }

    protected double translatedMouseY(double mouseY) {
        return mouseY - this.guiTop;
    }

    public ResourceLocation getBackgroundImageLocation() {
        return background;
    }

    public int getWindowID() {
        return windowContainer.getWindowID();
    }

    private class WidgetListener implements IWidgetListener {

        private WidgetListener() {
        }

        private void setListener(IWindowListener listener) {
            this.listener = listener;
        }

        private void setWidget(IWidget widget) {
            this.widget = widget;
        }

        private IWindowListener listener;
        private IWidget widget;

        @Override
        public void updateCraftingInventory(List<ItemStack> itemsList) {
            listener.updateCraftingInventory(itemsList);
        }

        @Override
        public void sendSlotContents(int slotInd, ItemStack stack) {
            listener.sendSlotContents(slotInd, stack);
        }

        @Override
        public void sendProgressBarUpdate(int newValue) {
            listener.sendProgressBarUpdate(map.get(widget), newValue);
        }

        @Override
        public IWindowListener getWindowListener() {
            return listener;
        }

    }

    public static final ResourceLocation DEFAULT_BACKGROUND;

    static {
        DEFAULT_BACKGROUND = new ResourceLocation(ElecCore.MODID, "default.png");
    }

}
