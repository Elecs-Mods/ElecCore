package elec332.core.client.util;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 6-7-2020
 */
public abstract class AbstractTileEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

    public AbstractTileEntityRenderer() {
        super();
    }

}
