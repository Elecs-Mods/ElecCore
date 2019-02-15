package elec332.core.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IInteractionObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 15-2-2019
 */
public abstract class NullInteractionObject implements IInteractionObject {

    public NullInteractionObject(ResourceLocation guiId){
        this.guiId = Preconditions.checkNotNull(guiId);
    }

    private final ResourceLocation guiId;

    @Nonnull
    @Override
    public abstract Container createContainer(@Nonnull InventoryPlayer inventoryPlayer, @Nonnull EntityPlayer entityPlayer);

    @Nonnull
    @Override
    public String getGuiID() {
        return guiId.toString();
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasCustomName() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        throw new UnsupportedOperationException();
    }

}
