package elec332.core.client.model.loading.handler;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.api.client.model.loading.IItemModelHandler;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.loader.client.RenderingRegistry;
import elec332.core.util.FieldPointer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 18-9-2016.
 */
@ModelHandler
public class ItemModelHandler implements IModelHandler {

    public ItemModelHandler() {
        itemModelHandlers = Lists.newArrayList();
        itemResourceLocations = Maps.newHashMap();
    }

    private List<IItemModelHandler> itemModelHandlers;
    private Map<Item, ModelResourceLocation> itemResourceLocations;

    //Temp until Forge restores this functionality
    private static final FieldPointer<ModelLoader, Map<Pair<IRegistryDelegate<Item>, Integer>, ModelResourceLocation>> field = new FieldPointer<>(ModelLoader.class, "customModels");
    private static final BiConsumer<Item, ModelResourceLocation> itemModelRegistry = (item, modelResourceLocation) -> field.get(null).put(Pair.of(item.delegate, 0), modelResourceLocation);

    @Override
    public void getModelHandlers(List<?> list) {
        for (Object o : list) {
            if (o instanceof IItemModelHandler) {
                itemModelHandlers.add((IItemModelHandler) o);
            }
        }
    }

    @Override
    public void preHandleModels() {
        ElecCore.logger.info("Prehandling Item Models");
        for (Item item : RenderingRegistry.instance().getAllValidItems()) {
            for (IItemModelHandler handler : itemModelHandlers) {
                if (handler.handleItem(item)) {
                    String s = handler.getIdentifier(item);
                    final ModelResourceLocation mr = new ModelResourceLocation(Preconditions.checkNotNull(item.getRegistryName()).toString(), s);
                    itemModelRegistry.accept(item, mr);
                    itemResourceLocations.put(item, mr);
                    break;
                }
            }
        }
    }

    @Override
    @Nonnull
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> modelGetter, ModelLoader modelLoader) {
        Map<ModelResourceLocation, IBakedModel> ret = Maps.newHashMap();
        for (Map.Entry<Item, ModelResourceLocation> entry : itemResourceLocations.entrySet()) {
            ModelResourceLocation mrl = entry.getValue();
            for (IItemModelHandler handler : itemModelHandlers) {
                if (handler.handleItem(entry.getKey())) {
                    ret.put(mrl, handler.getModelFor(entry.getKey(), mrl.getVariant(), mrl));
                    break;
                }
            }
        }
        return ret;
    }

}
