package elec332.core.inventory.window;

import elec332.core.inventory.widget.slot.WidgetSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
    public void handleMouseClickDefault(WidgetSlot slotIn, int slotId, int mouseButton, @Nonnull ClickType type);

    public boolean mergeItemStackDefault(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);

    public ItemStack slotClickDefault(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player);

    public void sendPacket(CompoundNBT tag);

    public int getWindowID();

}
