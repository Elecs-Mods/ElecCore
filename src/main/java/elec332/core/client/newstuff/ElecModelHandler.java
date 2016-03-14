package elec332.core.client.newstuff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.api.annotations.ASMDataProcessor;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import elec332.core.java.ReflectionHelper;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 11-3-2016.
 */
@SideOnly(Side.CLIENT)
@ASMDataProcessor(LoaderState.PREINITIALIZATION)
public class ElecModelHandler implements IASMDataProcessor {

    private static List<IBlockModelHandler> blockModelHandlers;
    private static List<IItemModelHandler> itemModelHandlers;
    private static Map<ModelResourceLocation, IBakedModel> models;
    private static Map<Item, ModelResourceLocation> itemResourceLocations;
    private static Map<IBlockState, ModelResourceLocation> blockResourceLocations;

    @Override
    public void processASMData(IASMDataHelper asmData, LoaderState state) {
        for (ASMDataTable.ASMData data : asmData.getAnnotationList(ModelHandler.class)){
            String s = data.getClassName();
            try {
                Object instance = Class.forName(s).newInstance();
                if (instance instanceof IBlockModelHandler){
                    blockModelHandlers.add((IBlockModelHandler) instance);
                    System.out.println("Added BMH: "+instance);
                }
                if (instance instanceof IItemModelHandler){
                    itemModelHandlers.add((IItemModelHandler) instance);
                    System.out.println("Added IMH: "+instance);
                }
            } catch (Exception e){
                throw new RuntimeException("Error registering ModelHandler class: "+s, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerItemModels(RenderItem renderItem){
        System.out.println("Prehandling Items");
        IdentityHashMap<Item, TIntObjectHashMap<IBakedModel>> models = null;
        try {
            models = (IdentityHashMap<Item, TIntObjectHashMap<IBakedModel>>) ReflectionHelper.makeFieldAccessible(ItemModelMesherForge.class.getDeclaredField("models")).get(renderItem.getItemModelMesher());
        } catch (Exception e){
            e.printStackTrace();
        }
        if (models == null){
            throw new RuntimeException();
        }
        for (Item item : GameData.getItemRegistry().typeSafeIterable()){
            for (IItemModelHandler handler : itemModelHandlers){
                if (handler.handleItem(item)){
                    models.put(item, new InternalItemMap<IBakedModel>());
                    String s = handler.getIdentifier(item);
                    ModelResourceLocation mr = new ModelResourceLocation(item.delegate.getResourceName().toString(), s);
                    renderItem.getItemModelMesher().register(item, 0, mr);
                    itemResourceLocations.put(item, mr);
                    break;
                }
            }
        }
    }

    public static void registerBlockModels(ModelManager modelManager){
        System.out.println("Prehandling blocks");
        for (Block block : GameData.getBlockRegistry().typeSafeIterable()){
            for (final IBlockModelHandler handler : blockModelHandlers) {
                if (handler.handleBlock(block)) {
                    modelManager.getBlockModelShapes().getBlockStateMapper().registerBlockStateMapper(block, new StateMapperBase() {
                        @Override
                        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                            ModelResourceLocation mrl = new ModelResourceLocation(state.getBlock().delegate.getResourceName().toString() + "#" + handler.getIdentifier(state));
                            blockResourceLocations.put(state, mrl);
                            return mrl;
                        }
                    });
                    break;
                }
            }
        }
    }

    public static Set<ModelResourceLocation> registerBakedModels(IRegistry<ModelResourceLocation, IBakedModel> registry){
        System.out.println("handling models");
        Set<ModelResourceLocation> ret = Sets.newHashSet();
        List<ModelResourceLocation> o = Lists.newArrayList();
        IBakedModel missingModel = registry.getObject(new ModelResourceLocation("builtin/missing", "missing"));
        for (Map.Entry<IBlockState, ModelResourceLocation> entry : blockResourceLocations.entrySet()){
            ModelResourceLocation mrl = entry.getValue();
            for (IBlockModelHandler handler : blockModelHandlers){
                if (handler.handleBlock(entry.getKey().getBlock())){
                    IBakedModel model = handler.getModelFor(entry.getKey(), mrl.getVariant(), mrl);
                    if (model == null){
                        if (models.get(mrl) == null){
                            o.add(mrl);
                        }
                        break;
                    }
                    models.put(mrl, model);
                    ret.add(mrl);
                    break;
                }
            }
        }
        for (Map.Entry<Item, ModelResourceLocation> entry : itemResourceLocations.entrySet()){
            ModelResourceLocation mrl = entry.getValue();
            for (IItemModelHandler handler : itemModelHandlers){
                if (handler.handleItem(entry.getKey())){
                    IBakedModel model = handler.getModelFor(entry.getKey(), mrl.getVariant(), mrl);
                    if (model == null){
                        if (models.get(mrl) == null){
                            o.add(mrl);
                        }
                        break;
                    }
                    models.put(mrl, model);
                    ret.add(mrl);
                    break;
                }
            }
        }
        for (ModelResourceLocation mrl : o){
            if (models.get(mrl) == null){
                models.put(mrl, missingModel);
            }
        }
        for (Map.Entry<ModelResourceLocation, IBakedModel> entry : models.entrySet()){
            registry.putObject(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    private static class InternalItemMap<M> extends TIntObjectHashMap<M> {

        public InternalItemMap(){
            super();
        }

        @Override
        public M put(int key, M value) {
            return super.put(0, value);
        }

        @Override
        public M get(int key) {
            return super.get(0);
        }

    }

    static {
        blockModelHandlers = Lists.newArrayList();
        itemModelHandlers = Lists.newArrayList();
        models = Maps.newHashMap();
        itemResourceLocations = Maps.newHashMap();
        blockResourceLocations = Maps.newHashMap();
    }

}
