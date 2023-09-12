package elec332.core.inventory.window;

import elec332.core.inventory.widget.slot.WidgetSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 17-12-2016.
 */
public interface IWindowContainer {

    public WidgetSlot getSlot(int id);

    public int getSlotListSize();

    @Nonnull
    public <T extends WidgetSlot> T addSlotToWindow(@Nonnull T widget);

    public void detectAndSendChanges();

    public PlayerEntity getPlayer();

    public List<IWindowListener> getListeners();

    @OnlyIn(Dist.CLIENT)
    public void handleSlotClickDefault(WidgetSlot slotIn, int slotId, int mouseButton, @Nonnull ClickType type);

    @OnlyIn(Dist.CLIENT)
    public boolean mouseDraggedDefault(double mouseX, double mouseY, int mouseButton, double dragX, double dragY);

    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleasedDefault(double mouseX, double mouseY, int mouseButton);

    public boolean mergeItemStackDefault(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);

    public ItemStack slotClickDefault(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player);

    public void sendPacket(CompoundTag tag);

    public int getWindowID();

}
