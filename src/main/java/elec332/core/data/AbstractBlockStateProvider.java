package elec332.core.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.util.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 14-7-2020
 */
public abstract class AbstractBlockStateProvider extends BlockStateProvider {

    public AbstractBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected final void registerStatesAndModels() {
        registerBlockStatesAndModels();
    }

    protected abstract void registerBlockStatesAndModels();

    public void simpleBlock(Supplier<? extends Block> block) {
        simpleBlock(block.get());
    }

    public void simpleBlock(Supplier<? extends Block> block, ModelFile model) {
        simpleBlock(block.get(), model);
    }

    public void simpleSide(Supplier<? extends Block> blockSupplier, ResourceLocation sides) {
        Block block = blockSupplier.get();
        simpleBlock(block, models().cubeBottomTop(Preconditions.checkNotNull(block.getRegistryName()).getPath(), sides, getDefaultBottomLocation(), getDefaultTopLocation()));
    }

    public void simpleSide(Supplier<? extends Block> blockSupplier, Function<BlockState, ResourceLocation> sides) {
        Block block = blockSupplier.get();
        getVariantBuilder(block).forAllStates(state -> {
            ResourceLocation texture = Preconditions.checkNotNull(sides.apply(state));
            return ConfiguredModel.builder()
                    .modelFile(models().cubeBottomTop(texture.getPath(), texture, getDefaultBottomLocation(), getDefaultTopLocation()))
                    .build();
        });
    }

    public void simpleFacingModel(Supplier<? extends Block> blockSupplier, BiFunction<BlockState, Function<ResourceLocation, ModelFile>, ModelFile> front) {
        simpleFacingModel(blockSupplier.get(), front);
    }

    public void simpleFront(Supplier<? extends Block> blockSupplier) {
        Block block = blockSupplier.get();
        simpleFront(block, blockTexture(block));
    }

    public void simpleFront(Supplier<? extends Block> blockSupplier, ResourceLocation front) {
        simpleFront(blockSupplier.get(), front);
    }

    public void simpleFront(Block block, ResourceLocation front) {
        ModelFile model = simpleFront(Preconditions.checkNotNull(block.getRegistryName()).getPath(), front);
        simpleFacingModel(block, state -> model);
    }

    public void simpleFacingModel(Supplier<? extends Block> blockSupplier, ModelFile model) {
        simpleFacingModel(blockSupplier.get(), s -> model);
    }

    public void simpleFacingModel(Supplier<? extends Block> blockSupplier, BooleanProperty prop) {
        simpleFacingModel(blockSupplier.get(), prop);
    }

    public void simpleFacingModel(Block block, BooleanProperty prop) {
        ResourceLocation base = Preconditions.checkNotNull(blockTexture(block));
        simpleFacingModel(block, (state, generator) -> state.get(prop) ? generator.apply(new ResourceLocation(base.getNamespace(), base.getPath() + "_" + prop.getName())) : generator.apply(base));
    }

    public void simpleFacingModel(Supplier<? extends Block> blockSupplier, BooleanProperty prop, ResourceLocation yes, ResourceLocation no) {
        simpleFacingModel(blockSupplier.get(), prop, yes, no);
    }

    public void simpleFacingModel(Block block, BooleanProperty prop, ResourceLocation yes, ResourceLocation no) {
        simpleFacingModel(block, (state, generator) -> state.get(prop) ? generator.apply(yes) : generator.apply(no));
    }

    public void simpleFacingModel(Supplier<? extends Block> blockSupplier, Function<BlockState, ModelFile> front) {
        simpleFacingModel(blockSupplier.get(), front);
    }

    public void simpleFacingModel(Block block, BiFunction<BlockState, Function<ResourceLocation, ModelFile>, ModelFile> front) {
        Map<String, ModelFile> map = Maps.newHashMap();
        simpleFacingModel(block, state -> front.apply(state, rl -> {
            String path = rl.getPath();
            int idx = path.lastIndexOf("/");
            if (idx >= 0) {
                path = path.substring(idx + 1);
            }
            return map.computeIfAbsent(path, name -> simpleFront(name, rl));
        }));
    }

    public void simpleFacingModel(Block block, Function<BlockState, ModelFile> front) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(front.apply(state))
                .rotationY(((int) state.get(BlockProperties.FACING_HORIZONTAL).getHorizontalAngle() + 180) % 360)
                .build());
    }

    public BlockModelBuilder simpleFront(String name, ResourceLocation front) {
        return simpleFront(name, front, getDefaultSideLocation());
    }

    public BlockModelBuilder simpleFront(String name, ResourceLocation front, ResourceLocation back) {
        return models().cube(Preconditions.checkNotNull(name), getDefaultBottomLocation(), getDefaultTopLocation(), front, back, getDefaultSideLocation(), getDefaultSideLocation());
    }

    protected ResourceLocation getDefaultSideLocation() {
        throw new UnsupportedOperationException();
    }

    protected ResourceLocation getDefaultTopLocation() {
        throw new UnsupportedOperationException();
    }

    protected ResourceLocation getDefaultBottomLocation() {
        return getDefaultTopLocation();
    }

}
