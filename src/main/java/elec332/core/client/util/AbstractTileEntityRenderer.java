package elec332.core.client.util;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 29-6-2020
 */
public abstract class AbstractTileEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

    public AbstractTileEntityRenderer() {
        super(TileEntityRendererDispatcher.instance);
    }

    public AbstractTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

}
