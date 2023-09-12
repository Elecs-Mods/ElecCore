package elec332.core.client.util;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by Elec332 on 6-7-2020
 */
public abstract class AbstractTileEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

    public AbstractTileEntityRenderer() {
        super();
    }

}
