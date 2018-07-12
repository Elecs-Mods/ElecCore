package elec332.core.abstraction.builder.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.abstraction.builder.IBlockBuilder;
import elec332.core.abstraction.builder.IBlockStateSerializer;
import elec332.core.abstraction.builder.IPropertySerializer;
import elec332.core.abstraction.builder.ISerializableProperty;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 22-3-2018.
 */
public class DefaultBlockBuilder implements IBlockBuilder {

    public DefaultBlockBuilder(){
        this.unlistedProperties = Lists.newArrayList();
        this.properties = Maps.newHashMap();
        this.stateSerializer = null;
    }

    private ResourceLocation name;
    private List<IUnlistedProperty> unlistedProperties;
    private Map<IProperty, IPropertySerializer> properties;
    private IBlockStateSerializer stateSerializer = noStateSerializer;
    private Function<Block, Item> itemBuilder = null;
    private BiFunction<World, Integer, TileEntity> tileCreator = null;
    @SideOnly(Side.CLIENT)
    private ModelResourceLocation modelName = null;
    @SideOnly(Side.CLIENT)
    private TileEntitySpecialRenderer<?> tesr = null;
    @SideOnly(Side.CLIENT)
    private IBakedModel model = null;


    private static final IBlockStateSerializer noStateSerializer;
    private static final Map<Class<?>, IPropertySerializer> defaultPropSerializers;

    @Override
    public DefaultBlockBuilder setName(@Nonnull ResourceLocation name) {
        this.name = Preconditions.checkNotNull(name);
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public <P extends Comparable<P>> DefaultBlockBuilder addProperty(@Nonnull IProperty<P> property, IPropertySerializer<P> serializer) {
        if (serializer == null){
            IPropertySerializer serializer_ = null;
            int i = 0;
            while (serializer_ == null){
                Class<?> type = null;
                switch (i){
                    case 0:
                        type = property.getClass();
                        break;
                    case 1:
                        type = property.getValueClass();
                        break;
                    default:
                        if (property.getValueClass().isEnum()){
                            type = Enum.class;
                        }
                }
                if (type == null){
                    break;
                }
                serializer_ = defaultPropSerializers.get(type);
                i++;
            }
            if (serializer_ == null){
                throw new IllegalArgumentException();
            }
            serializer = serializer_;
        }
        addProperty_(Preconditions.checkNotNull(property), Preconditions.checkNotNull(serializer));
        return this;
    }

    public <P extends Comparable<P>> void addProperty_(IProperty<P> property, IPropertySerializer<P> serializer) {
        properties.put(property, serializer);
    }

    @Override
    public <P extends Comparable<P>> IBlockBuilder addProperty(@Nonnull ISerializableProperty<P> property) {
        Preconditions.checkNotNull(property);
        addProperty_(property, new IPropertySerializer<P>() {

            @Override
            public <P1 extends IProperty<P>> int getMeta(P v, P1 p) {
                return property.getMeta(v);
            }

            @Override
            public <P1 extends IProperty<P>> P getValueFromMeta(int meta, P1 p) {
                return property.getValueFromMeta(meta);
            }

            @Override
            public <P1 extends IProperty<P>> int getPossibleMetaStates(P1 p) {
                return property.getPossibleMetaStates();
            }

        });
        return this;
    }

    @Override
    public IBlockBuilder addUnlistedProperty(@Nonnull IUnlistedProperty<?> property) {
        unlistedProperties.add(Preconditions.checkNotNull(property));
        return this;
    }

    @Override
    public IBlockBuilder setBlockStateSerializer(@Nonnull IBlockStateSerializer serializer) {
        this.stateSerializer = Preconditions.checkNotNull(serializer);
        return this;
    }

    @Override
    public IBlockBuilder withItem() {
        return withItem(ItemBlock::new);
    }

    @Override
    public IBlockBuilder withItem(@Nonnull Function<Block, Item> itemBuilder) {
        this.itemBuilder = Preconditions.checkNotNull(itemBuilder);
        return this;
    }

    @Override
    public IBlockBuilder withTile(@Nonnull BiFunction<World, Integer, TileEntity> tileCreator) {
        this.tileCreator = Preconditions.checkNotNull(tileCreator);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockBuilder setModelLocation(@Nonnull ModelResourceLocation name) {
        this.modelName = Preconditions.checkNotNull(name);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockBuilder withModel(@Nonnull IBakedModel model) {
        this.model = Preconditions.checkNotNull(model);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockBuilder withTESR(@Nonnull TileEntitySpecialRenderer<?> tesr) {
        this.tesr = Preconditions.checkNotNull(tesr);
        return this;
    }

    @Override
    public Block build() {
        Triple<IBlockStateSerializer, IProperty[], IUnlistedProperty[]> properties = checkMetaStuff();
        Block b = new BlockBuilderBlock(null) {

            @Nonnull
            @Override
            public BlockStateContainer createBlockState() {
                return new ExtendedBlockState(this, properties.getMiddle(), properties.getRight());
            }

        };

        return null;
    }

    @SideOnly(Side.CLIENT)
    private void checkClientStuff(Block block){

    }

    private Triple<IBlockStateSerializer, IProperty[], IUnlistedProperty[]> checkMetaStuff(){
        int bits = 0;
        List<IProperty> pl = Lists.newArrayList(this.properties.keySet());
        pl.sort(Comparator.comparing(IProperty::getName));
        List<Pair<IProperty, Integer>> metaProps = Lists.newArrayList();
        for (IProperty prop : pl){
            int i = this.properties.get(prop).getPossibleMetaStates(prop);
            if (i > 1){
                if (i % 2 == 1){
                    i++;
                }
                i = net.minecraft.util.math.MathHelper.log2(i);
                bits += i;
                metaProps.add(Pair.of(prop, i));
            }
        }
        if (bits > 4){
            throw new IllegalStateException("Meta cannot contain more than 4 bits");
        }
        IProperty[] properties = pl.toArray(new IProperty[pl.size()]);
        IUnlistedProperty[] unlistedProperties = this.unlistedProperties.toArray(new IUnlistedProperty[this.unlistedProperties.size()]);
        if (bits == 0 || stateSerializer != noStateSerializer){
            return Triple.of(stateSerializer, properties, unlistedProperties);
        }
        if (metaProps.size() == 1){
            return Triple.of(new IBlockStateSerializer() {

                private final IProperty prop = metaProps.get(0).getLeft();
                private final IPropertySerializer serializer = Preconditions.checkNotNull(DefaultBlockBuilder.this.properties.get(prop));

                @Override
                @SuppressWarnings("all")
                public int getMeta(IBlockState state) {
                    return serializer.getMeta(state.getValue(prop), prop);
                }

                @Override
                @SuppressWarnings("all")
                public IBlockState getState(int meta, Block block) {
                    return block.getDefaultState().withProperty(prop, serializer.getValueFromMeta(meta, prop));
                }

            }, properties, unlistedProperties);
        }
        return Triple.of(new IBlockStateSerializer() {

            {
                int i = metaProps.size();
                bitz = new int[i];
                props = new IProperty[i];
                metaProps.forEach(new Consumer<Pair<IProperty, Integer>>() {

                    int j = 0;

                    @Override
                    public void accept(Pair<IProperty, Integer> p) {
                        props[j] = p.getLeft();
                        bitz[j] = net.minecraft.util.math.MathHelper.log2(p.getRight());
                        j++;
                    }

                });
                serializers = metaProps.stream().map(prop -> Preconditions.checkNotNull(DefaultBlockBuilder.this.properties.get(prop.getLeft()))).collect(Collectors.toList()).toArray(new IPropertySerializer[0]);
            }

            private static final byte bits = 4;
            private final int[] bitz;
            private final IProperty[] props;
            private final IPropertySerializer[] serializers;

            @Override
            @SuppressWarnings("all")
            public int getMeta(IBlockState state) {
                int meta = 0;
                int totBits = 0;
                for (int i = 0; i < bitz.length; i++) {
                    int bits = bitz[i];
                    IProperty prop = props[i];
                    int meta_ = serializers[i].getMeta(state.getValue(prop), prop);
                    if (Integer.numberOfTrailingZeros(meta_) > 32 - bits){
                        throw new RuntimeException();
                    }
                    meta += meta_ >> totBits;
                    totBits += bits;
                }
                if (meta >= 16){
                    throw new RuntimeException();
                }
                return meta;
            }

            @Override
            @SuppressWarnings("all")
            public IBlockState getState(int meta, Block block) {
                int totBits = 0;
                int[] bitExtract = new int[bits];
                IBlockState state = block.getDefaultState();
                for (int i = 0; i < bits; i++) {
                    bitExtract[i] = meta & (int) Math.pow(2, i);
                }
                for (int i = 0; i < bitz.length; i++) {
                    int meta_ = 0;
                    for (int j = totBits; j < totBits + bitz[i]; j++) {
                        meta_ += bitExtract[j];
                    }
                    IProperty prop = props[i];
                    Comparable value = serializers[i].getValueFromMeta(meta_, prop);
                    state = state.withProperty(prop, value);
                }
                return state;
            }

        }, properties, unlistedProperties);
    }

    static {
        defaultPropSerializers = Maps.newHashMap();
        defaultPropSerializers.put(Enum.class, new EnumSerializer());
        defaultPropSerializers.put(Integer.class, new IPropertySerializer<Integer>() {

            @Override
            public <P extends IProperty<Integer>> int getMeta(Integer value, P property) {
                return value;
            }

            @Override
            public <P extends IProperty<Integer>> Integer getValueFromMeta(int meta, P property) {
                return meta;
            }

        });
        defaultPropSerializers.put(Boolean.class, new IPropertySerializer<Boolean>() {

            @Override
            public <P extends IProperty<Boolean>> int getMeta(Boolean value, P property) {
                return value ? 1 : 0;
            }

            @Override
            public <P extends IProperty<Boolean>> Boolean getValueFromMeta(int meta, P property) {
                return meta == 1;
            }

        });
        noStateSerializer = new IBlockStateSerializer() {

            @Override
            public int getMeta(IBlockState state) {
                return 0;
            }

            @Override
            public IBlockState getState(int meta, Block block) {
                return block.getDefaultState();
            }

        };

    }

    private static class EnumSerializer<P extends Enum<P> & Comparable<P>> implements IPropertySerializer<P> {

        @Override
        public <P1 extends IProperty<P>> int getMeta(P value, P1 property) {
            return value.ordinal();
        }

        @Override
        public <P1 extends IProperty<P>> P getValueFromMeta(int meta, P1 property) {
            return property.getValueClass().getEnumConstants()[meta];
        }

    }

}
