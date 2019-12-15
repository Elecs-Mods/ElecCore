package elec332.core.inventory.window;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.network.packets.PacketWindowData;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 28-11-2016.
 */
public final class WindowContainer extends Container {

    public WindowContainer(PlayerEntity player, Window window) {
        this.listeners = Lists.newArrayList();
        this.thePlayer = player;
        this.window = window;
        this.slotStuff = Maps.newHashMap();
        this.externalSlots = Maps.newHashMap();
        window.setContainer(windowContainerHandler = new WindowContainerHandler());
        if (!player.getEntityWorld().isRemote) {
            window.initWindow_();
        }
    }

    private final Window window;
    private final PlayerEntity thePlayer;
    private final List<IWindowListener> listeners;
    private final Map<WidgetSlot, Slot> slotStuff;
    private final Map<Slot, WidgetSlot> externalSlots;
    final WindowContainerHandler windowContainerHandler;

    private static final IInventory NULL_INVENTORY;

    public Window getWindow() {
        return window;
    }

    @Override
    @SuppressWarnings("all")
    public void addListener(@Nonnull IContainerListener listener) {
        super.addListener(listener);
        WindowListener windowListener = new WindowListener(listener);
        window.onListenerAdded(windowListener);
        if (!listeners.contains(listener)) {
            listeners.add(windowListener);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void removeListener(@Nonnull IContainerListener listener) {
        super.removeListener(listener);
        listeners.remove(listener);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        window.updateProgressBar(id, data);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        window.detectAndSendChanges(this);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return window.transferStackInSlot(playerIn, index);
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        window.onWindowClosed(player);
        super.onContainerClosed(player);
    }

    @Override
    public void putStackInSlot(int slotID, @Nonnull ItemStack stack) {
        window.putStackInSlot(slotID, stack);
    }

    @Nonnull
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        return window.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        if (slotIn instanceof WidgetLinkedSlot) {
            return window.canMergeSlot(stack, ((WidgetLinkedSlot) slotIn).widget);
        } else {
            return super.canMergeSlot(stack, slotIn);
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        window.onCraftMatrixChanged(inventoryIn);
    }

    @Override
    public boolean getCanCraft(@Nonnull PlayerEntity player) {
        return window.getCanCraft(player);
    }

    @Override
    public void setCanCraft(@Nonnull PlayerEntity player, boolean canCraft) {
        window.setCanCraft(player, canCraft);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        return window.canInteractWith(player);
    }

    @Override
    protected Slot addSlot(Slot slotIn) {
        if (!(slotIn instanceof WidgetLinkedSlot)) {
            externalSlots.put(slotIn, new WrappedWidgetSlot(slotIn));
        }
        return super.addSlot(slotIn);
    }

    private class WindowListener implements IWindowListener {

        private WindowListener(IContainerListener listener) {
            this.listener = listener;
        }

        private final IContainerListener listener;

        @Override
        public void updateCraftingInventory(List<ItemStack> itemsList) {
            if (!(itemsList instanceof NonNullList)) {
                throw new IllegalArgumentException();
            }
            listener.sendAllContents(WindowContainer.this, (NonNullList<ItemStack>) itemsList);
        }

        @Override
        public void sendSlotContents(int slotInd, ItemStack stack) {
            listener.sendSlotContents(WindowContainer.this, slotInd, stack);
        }

        @Override
        public void sendProgressBarUpdate(int id, int newValue) {
            listener.sendWindowProperty(WindowContainer.this, id, newValue);
        }

        @Override
        public Object getActualListener() {
            return listener;
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof WindowListener && ((WindowListener) obj).listener.equals(listener) || obj instanceof IContainerListener && obj.equals(listener);
        }

    }

    class WidgetLinkedSlot extends Slot {

        private WidgetLinkedSlot(WidgetSlot widget) {
            super(NULL_INVENTORY, widget.getSlotIndex(), widget.x, widget.y);
            this.widget = widget;
        }

        final WidgetSlot widget;

        @Override
        public void onSlotChange(ItemStack oldStack, ItemStack newStack) {
            widget.onSlotChange(oldStack, newStack);
        }

        @Override
        protected void onCrafting(ItemStack stack, int amount) {
            widget.onCrafting(stack, amount);
        }

        @Override
        protected void onSwapCraft(int p_190900_1_) {
            widget.onSwapCraft(p_190900_1_);
        }

        @Override
        protected void onCrafting(ItemStack stack) {
            widget.onCrafting(stack);
        }

        @Override
        @Nonnull
        public ItemStack onTake(@Nonnull PlayerEntity thePlayer, @Nonnull ItemStack stack) {
            return widget.onTake(thePlayer, stack);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return widget.isItemValid(stack);
        }

        @Nonnull
        @Override
        public ItemStack getStack() {
            return widget.getStack();
        }

        @Override
        public boolean getHasStack() {
            return widget.getHasStack();
        }

        @Override
        public void putStack(@Nonnull ItemStack stack) {
            widget.putStack(stack);
        }

        @Override
        public void onSlotChanged() {
            widget.onSlotChanged();
        }

        @Override
        public int getSlotStackLimit() {
            return widget.getSlotStackLimit();
        }

        @Override
        public int getItemStackLimit(ItemStack stack) {
            return widget.getItemStackLimit(stack);
        }

        @Nullable
        @OnlyIn(Dist.CLIENT)
        @Override
        public String getSlotTexture() {
            return widget.getSlotTexture();
        }

        @Nonnull
        @Override
        public ItemStack decrStackSize(int amount) {
            return widget.decrStackSize(amount);
        }

        @Override
        public boolean isHere(IInventory inv, int slotIn) {
            return widget.isHere(inv, slotIn);
        }

        @Override
        public boolean canTakeStack(PlayerEntity playerIn) {
            return widget.canTakeStack(playerIn);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public boolean isEnabled() {
            return widget.canBeHovered();
        }

        @Nonnull
        @OnlyIn(Dist.CLIENT)
        @Override
        public ResourceLocation getBackgroundLocation() {
            return widget.getBackgroundLocation();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void setBackgroundLocation(@Nonnull ResourceLocation texture) {
            widget.setBackgroundLocation(texture);
        }

        @Override
        public void setBackgroundName(@Nullable String name) {
            widget.setBackgroundName(name);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        //@Nonnull
        @SuppressWarnings("all")
        public TextureAtlasSprite getBackgroundSprite() {
            return widget.getBackgroundSprite();
        }

        @Nonnull
        @OnlyIn(Dist.CLIENT)
        @Override
        protected TextureMap getBackgroundMap() {
            return widget.getBackgroundMap();
        }

        @Override
        public int getSlotIndex() {
            return widget.getSlotIndex();
        }

        @Override
        public boolean isSameInventory(Slot other) {
            return widget.isSameInventory(((WidgetLinkedSlot) other).widget);
        }

    }

    class WindowContainerHandler implements IWindowContainer {

        @OnlyIn(Dist.CLIENT)
        WindowGui windowGui;

        @Override
        public WidgetSlot getSlot(int id) {
            Slot unknown = id >= 0 && id < inventorySlots.size() ? WindowContainer.this.getSlot(id) : null;
            if (unknown instanceof WidgetLinkedSlot) {
                return ((WidgetLinkedSlot) unknown).widget;
            } else {
                return Preconditions.checkNotNull(externalSlots.get(unknown));
            }
        }

        @Override
        public int getSlotListSize() {
            return inventorySlots.size();
        }

        @Nonnull
        @Override
        public <T extends WidgetSlot> T addSlotToWindow(@Nonnull T widget) {
            Slot slot = new WidgetLinkedSlot(widget);
            addSlot(slot);
            slotStuff.put(widget, slot);
            return widget;
        }

        @Override
        public void detectAndSendChanges() {
            WindowContainer.this.detectAndSendChanges();
        }

        @Override
        public PlayerEntity getPlayer() {
            return WindowContainer.this.thePlayer;
        }

        @Override
        public List<IWindowListener> getListeners() {
            return WindowContainer.this.listeners;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void handleMouseClickDefault(WidgetSlot slotIn, int slotId, int mouseButton, @Nonnull ClickType type) {
            Slot slot = slotIn == null ? null : slotStuff.get(slotIn);
            if (slotIn != null && slot == null) {
                throw new IllegalArgumentException();
            }
            windowGui.handleMouseClickDefault(slot, slotId, mouseButton, type);
        }

        @Override
        public boolean mergeItemStackDefault(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
            return WindowContainer.super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
        }

        @Override
        public ItemStack slotClickDefault(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
            return WindowContainer.super.slotClick(slotId, dragType, clickTypeIn, player);
        }

        @Override
        public void sendPacket(CompoundNBT tag) {
            PacketWindowData packet = new PacketWindowData(WindowContainer.this, tag);
            if (!thePlayer.getEntityWorld().isRemote) {
                ElecCore.networkHandler.sendTo(packet, (ServerPlayerEntity) thePlayer);
            } else {
                ElecCore.networkHandler.sendToServer(packet);
            }
        }

        @Override
        public int getWindowID() {
            return WindowContainer.this.windowId;
        }

    }

    static {
        NULL_INVENTORY = new InventoryBasic(new TextComponentString("NULL"), 0);
    }

}
