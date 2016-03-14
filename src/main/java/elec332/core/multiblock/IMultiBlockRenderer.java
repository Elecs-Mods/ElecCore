package elec332.core.multiblock;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * Created by Elec332 on 4-3-2016.
 */
public interface IMultiBlockRenderer<M extends IMultiBlock> {

    public void renderMultiBlock(M multiblock, float partialTicks);

    public AxisAlignedBB getRenderingBoundingBox(M multiblock);

}
