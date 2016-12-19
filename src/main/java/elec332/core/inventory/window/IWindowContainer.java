package elec332.core.inventory.window;

import elec332.core.inventory.widget.slot.WidgetSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public EntityPlayer getPlayer();

    public List<IWindowListener> getListeners();

    @SideOnly(Side.CLIENT)
    public void handleMouseClickDefault(WidgetSlot slotIn, int slotId, int mouseButton, @Nonnull ClickType type);

    public boolean mergeItemStackDefault(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);

    public ItemStack slotClickDefault(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player);

    public void sendPacket(NBTTagCompound tag);

    public int getWindowID();

}
