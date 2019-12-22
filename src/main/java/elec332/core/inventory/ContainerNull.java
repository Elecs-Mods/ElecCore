package elec332.core.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 11-10-2015.
 * <p>
 * Null/fake container
 */
public final class ContainerNull extends Container {

    protected ContainerNull() {
        super(null, -1);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        return false;
    }

}
