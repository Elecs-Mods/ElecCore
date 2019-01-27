package elec332.core.handler.annotations;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import elec332.core.api.callback.CallbackProcessor;
import elec332.core.api.callback.ICallbackProcessor;
import elec332.core.api.callback.RegisteredCallback;
import elec332.core.api.discovery.AnnotationDataProcessor;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.registration.HasSpecialRenderer;
import elec332.core.util.FMLHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.objectweb.asm.Type;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 7-3-2016.
 */

@AnnotationDataProcessor(ModLoadingStage.ENQUEUE_IMC)
public class InitAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    protected void registerProcesses() {

        List<Object> callbacks = Lists.newArrayList(), callbacks_ = Collections.unmodifiableList(callbacks);

        registerDataProcessor(RegisteredCallback.class, asmData -> {
            Class<?> clazz = loadClass(asmData, false);
            if (clazz == null) {
                return;
            }
            @SuppressWarnings("all") //Compile errors
                    Object instance = instantiate(clazz, false, new Object[0]);
            if (instance == null) {
                return;
            }
            callbacks.add(instance);
        });

        registerDataProcessor(CallbackProcessor.class, new Consumer<IAnnotationData>() {

            @Override
            public void accept(IAnnotationData asmData) {
                Object instance = instantiate(asmData.loadClass());
                if (instance instanceof ICallbackProcessor) {
                    ((ICallbackProcessor) instance).getCallbacks(callbacks_);
                }
            }

        });


        registerDataProcessor(HasSpecialRenderer.class, new Consumer<IAnnotationData>() {

            @Override
            @SuppressWarnings("all")
            public void accept(IAnnotationData asmData) {
                try {
                    ClientRegistry.bindTileEntitySpecialRenderer((Class<TileEntity>) Class.forName(asmData.getClassName()), (TileEntityRenderer<TileEntity>) Class.forName(((Type) asmData.getAnnotationInfo().get("value")).getClassName()).newInstance());
                } catch (Exception ex) {
                    throw Throwables.propagate(ex);
                }
            }

        }, FMLHelper.getDist() == Dist.CLIENT);

/*
        registerDataProcessor(RegisteredMultiPart.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            @SuppressWarnings("unchecked")
            public void accept(ASMDataTable.ASMData data) {
                try {
                    MultipartRegistry.registerPart((Class<? extends Multipart>) Class.forName(data.getClassName()), (String) data.getAnnotationInfo().get("value"));
                } catch (Exception e){
                    logger.error("Error registering multipart: "+data.getClassName());
                }
            }

        }, Loader.isModLoaded("mcmultipart"));
*/

    }

}