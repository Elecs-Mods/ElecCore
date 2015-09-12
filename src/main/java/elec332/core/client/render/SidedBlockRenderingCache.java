package elec332.core.client.render;

import elec332.core.util.BlockSide;
import elec332.core.util.DirectionHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 2-9-2015.
 */
public final class SidedBlockRenderingCache {

    public SidedBlockRenderingCache(ITextureHandler textureHandler){
        this(textureHandler, 1);
    }

    public SidedBlockRenderingCache(ITextureHandler textureHandler, int types){
        this(textureHandler, types, 1);
    }

    public SidedBlockRenderingCache(ITextureHandler textureHandler, int types, int states){
        this.types = types;
        this.states = states;
        this.textureHandler = textureHandler;
        this.icons = new IIcon[types][states][6];
    }

    private final int types, states;
    private final ITextureHandler textureHandler;
    private IIcon[][][] icons;

    public final IIcon getIconForWorldRendering(IBlockAccess iba, int x, int y, int z, int side){
        final int meta = iba.getBlockMetadata(x, y, z);
        return textureHandler.getIconForWorldRendering(iba, x, y, z, side, meta, this);
    }

    public final IIcon getIconForInventoryRendering(int type, int side) {
        return getIconDirectly(type, 0, DirectionHelper.ROTATION_MATRIX_YAW[2][side]);
    }

    public final void registerTextures(IIconRegister register){
        for (int i = 0; i < types; i++) {
            for (int j = 0; j < states; j++) {
                for (BlockSide side : BlockSide.values()) {
                    icons[i][j][side.getDefaultSide()] = register.registerIcon(textureHandler.getTextureForSide(side, i, j));
                }
            }
        }
    }

    public IIcon getIconForNormalRendering(int type, int side){
        return getIconForInventoryRendering(type, side);
    }

    public IIcon getIconForBlockFacing(int requestedSide, ForgeDirection directionBlockIsFacing){
        return getIconForBlockFacing(requestedSide, directionBlockIsFacing, 0);
    }

    public IIcon getIconForBlockFacing(int requestedSide, ForgeDirection directionBlockIsFacing, int type){
        return getIconForBlockFacing(requestedSide, directionBlockIsFacing, type, 0);
    }

    public IIcon getIconForBlockFacing(int requestedSide, ForgeDirection directionBlockIsFacing, int type, int state){
        return getIconDirectly(type, state, DirectionHelper.ROTATION_MATRIX_YAW[DirectionHelper.getNumberForDirection(directionBlockIsFacing)][requestedSide]);
    }

    public IIcon getIconDirectly(int type, int state, int side){
        return icons[type][state][side];
    }

    public interface ITextureHandler{

        public String getTextureForSide(BlockSide side, int type, int state);

        public IIcon getIconForWorldRendering(IBlockAccess iba, int x, int y, int z, int side, final int meta, SidedBlockRenderingCache renderingCache);

    }

}
