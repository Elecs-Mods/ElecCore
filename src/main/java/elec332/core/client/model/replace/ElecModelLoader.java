package elec332.core.client.model.replace;

import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Collection;

/**
 * Created by Elec332 on 18-1-2016.
 */
public class ElecModelLoader extends ModelLoader {

    public ElecModelLoader(IResourceManager manager, TextureMap map, BlockModelShapes shapes) {
        super(manager, map, shapes);
    }

    @Override
    public void loadVariants(Collection<ModelResourceLocation> collection) {
        MinecraftForge.EVENT_BUS.post(new LoadVariantsEvent(this, collection));
    }

    public void actuallyLoadVariants(Collection<ModelResourceLocation> collection){
        super.loadVariants(collection);
    }

    public class LoadVariantsEvent extends Event {

        public LoadVariantsEvent(ElecModelLoader modelLoader, Collection<ModelResourceLocation> collection){
            this.modelLoader = modelLoader;
            this.collection = collection;
        }

        public final ElecModelLoader modelLoader;
        public final Collection<ModelResourceLocation> collection;

    }

}
