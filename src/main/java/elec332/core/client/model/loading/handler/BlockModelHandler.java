package elec332.core.client.model.loading.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.client.model.loading.IBlockModelHandler;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.loader.client.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Elec332 on 18-9-2016.
 */
@ModelHandler
public class BlockModelHandler implements IModelHandler {

    public BlockModelHandler() {
        blockModelHandlers = Lists.newArrayList();
        blockResourceLocations = Maps.newHashMap();
        handledBlocks = Sets.newHashSet();
    }

    private List<IBlockModelHandler> blockModelHandlers;
    private Map<IBlockState, ModelResourceLocation> blockResourceLocations;
    private Set<ResourceLocation> handledBlocks;

    @Override
    public void getModelHandlers(List<?> list) {
        for (Object o : list) {
            if (o instanceof IBlockModelHandler) {
                blockModelHandlers.add((IBlockModelHandler) o);
            }
        }
    }

    @Override
    public void preHandleModels() {
        ElecCore.logger.info("Prehandling Block Models");
        for (Block block : RenderingRegistry.instance().getAllValidBlocks()) {
            for (final IBlockModelHandler handler : blockModelHandlers) {
                if (handler.handleBlock(block)) {
                    handledBlocks.add(block.getRegistryName());
                    block.getStateContainer().getValidStates().forEach(ibs -> {
                        ModelResourceLocation mrl = BlockModelShapes.getModelLocation(ibs);
                        handler.notifyModelLocation(ibs, mrl);
                        blockResourceLocations.put(ibs, mrl);
                    });
                    break;
                }
            }
        }
    }

    @Override
    @Nonnull
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> modelGetter) {
        Map<ModelResourceLocation, IBakedModel> ret = Maps.newHashMap();
        for (Map.Entry<IBlockState, ModelResourceLocation> entry : blockResourceLocations.entrySet()) {
            ModelResourceLocation mrl = entry.getValue();
            for (IBlockModelHandler handler : blockModelHandlers) {
                if (handler.handleBlock(entry.getKey().getBlock())) {
                    ret.put(mrl, handler.getModelFor(entry.getKey(), mrl.getVariant(), mrl));
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public void cleanExceptions(Map<ResourceLocation, Exception> loaderExceptions) {
        handledBlocks.forEach(loaderExceptions::remove);
    }

}
