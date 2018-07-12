package elec332.core.abstraction.builder;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Elec332 on 22-3-2018.
 */
public interface IBlockBuilder {

    public IBlockBuilder setName(@Nonnull ResourceLocation name);

    public <P extends Comparable<P>> IBlockBuilder addProperty(@Nonnull IProperty<P> property, IPropertySerializer<P> serializer);

    public <P extends Comparable<P>> IBlockBuilder addProperty(@Nonnull ISerializableProperty<P> property);

    public IBlockBuilder addUnlistedProperty(@Nonnull IUnlistedProperty<?> property);

    public IBlockBuilder setBlockStateSerializer(@Nonnull IBlockStateSerializer serializer);

    public default IBlockBuilder withTile(@Nonnull Class<? extends TileEntity> tileClass){
        return withTile((world, integer) -> {
            try {
                return tileClass.newInstance();
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        });
    }

    public IBlockBuilder withItem();

    public IBlockBuilder withItem(@Nonnull Function<Block, Item> itemBuilder);

    public IBlockBuilder withTile(@Nonnull BiFunction<World, Integer, TileEntity> tileCreator);

    @SideOnly(Side.CLIENT)
    public IBlockBuilder setModelLocation(@Nonnull ModelResourceLocation name);

    @SideOnly(Side.CLIENT)
    public IBlockBuilder withModel(@Nonnull IBakedModel model);

    @SideOnly(Side.CLIENT)
    public IBlockBuilder withTESR(@Nonnull TileEntitySpecialRenderer<?> tesr);

    public Block build();

}
