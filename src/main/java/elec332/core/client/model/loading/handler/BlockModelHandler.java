package elec332.core.client.model.loading.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.api.client.model.loading.IBlockModelHandler;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.model.RenderingRegistry;
import elec332.core.main.ElecCore;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Elec332 on 18-9-2016.
 */
@ModelHandler
public class BlockModelHandler implements IModelHandler {

    public BlockModelHandler(){
        blockModelHandlers = Lists.newArrayList();
        blockResourceLocations = Maps.newHashMap();
    }

    private List<IBlockModelHandler> blockModelHandlers;
    private Map<IBlockState, ModelResourceLocation> blockResourceLocations;

    @Override
    public void getModelHandlers(List<?> list) {
        for (Object o : list){
            if (o instanceof IBlockModelHandler){
                blockModelHandlers.add((IBlockModelHandler) o);
            }
        }
    }

    @Override
    public void registerModels() {
        ElecCore.logger.info("Prehandling Block Models");
        ModelManager modelManager = Minecraft.getMinecraft().modelManager;
        for (Block block : RenderingRegistry.instance().getAllValidBlocks()){
            for (final IBlockModelHandler handler : blockModelHandlers) {
                if (handler.handleBlock(block)) {
                    modelManager.getBlockModelShapes().getBlockStateMapper().registerBlockStateMapper(block, new StateMapperBase() {

                        @Override
                        @Nonnull
                        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
                            ModelResourceLocation mrl = new ModelResourceLocation(state.getBlock().getRegistryName().toString() + "#" + handler.getIdentifier(state));
                            blockResourceLocations.put(state, mrl);
                            return mrl;
                        }

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
        for (Map.Entry<IBlockState, ModelResourceLocation> entry : blockResourceLocations.entrySet()){
            ModelResourceLocation mrl = entry.getValue();
            for (IBlockModelHandler handler : blockModelHandlers){
                if (handler.handleBlock(entry.getKey().getBlock())){
                    ret.put(mrl, handler.getModelFor(entry.getKey(), mrl.getVariant(), mrl));
                    break;
                }
            }
        }
        return ret;
    }

}
