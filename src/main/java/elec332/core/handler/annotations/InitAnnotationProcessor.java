package elec332.core.handler.annotations;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import elec332.core.api.callback.CallbackProcessor;
import elec332.core.api.callback.ICallbackProcessor;
import elec332.core.api.callback.RegisteredCallback;
import elec332.core.api.discovery.ASMDataProcessor;
import elec332.core.api.registration.HasSpecialRenderer;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.util.FMLUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.objectweb.asm.Type;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 7-3-2016.
 */

@ASMDataProcessor(LoaderState.INITIALIZATION)
public class InitAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    protected void registerProcesses() {

        List<Object> callbacks = Lists.newArrayList(), callbacks_ = Collections.unmodifiableList(callbacks);

        registerDataProcessor(RegisteredCallback.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            public void accept(ASMDataTable.ASMData asmData) {
                Class<?> clazz = loadClass(asmData, false);
                if (clazz == null){
                    return;
                }
                @SuppressWarnings("all") //Compile errors
                Object instance = instantiate(clazz, false, new Object[0]);
                if (instance == null){
                    return;
                }
                callbacks.add(instance);
            }

        });

        registerDataProcessor(CallbackProcessor.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            public void accept(ASMDataTable.ASMData asmData) {
                Object instance = instantiate(loadClass(asmData));
                if (instance instanceof ICallbackProcessor){
                    ((ICallbackProcessor) instance).getCallbacks(callbacks_);
                }
            }

        });


        registerDataProcessor(HasSpecialRenderer.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            @SuppressWarnings("all")
            public void accept(ASMDataTable.ASMData asmData) {
                try {
                    ClientRegistry.bindTileEntitySpecialRenderer((Class<TileEntity>) Class.forName(asmData.getClassName()), (TileEntitySpecialRenderer<TileEntity>) Class.forName(((Type) asmData.getAnnotationInfo().get("value")).getClassName()).newInstance());
                } catch (Exception ex) {
                    throw Throwables.propagate(ex);
                }
            }

        }, FMLCommonHandler.instance().getSide().isClient());

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
        registerDataProcessor(RegisteredTileEntity.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            @SuppressWarnings("unchecked")
            public void accept(ASMDataTable.ASMData data) {
                try {
                    String name = (String) data.getAnnotationInfo().get("value");
                    Class<? extends TileEntity> clazz = (Class<? extends TileEntity>) Class.forName(data.getClassName());
                    if (!name.contains(":")){
                        String mod = FMLUtil.getOwnerName(clazz);
                        name = mod + ":" + name;
                    }
                    GameRegistry.registerTileEntity(clazz, new ResourceLocation(name));
                } catch (Exception e){
                    logger.error("Error registering tile: "+data.getClassName());
                }
            }

        });

    }

}