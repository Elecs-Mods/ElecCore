package elec332.core.handler;

import com.google.common.collect.Lists;
import elec332.core.api.callback.CallbackProcessor;
import elec332.core.api.callback.ICallbackProcessor;
import elec332.core.api.callback.RegisteredCallback;
import elec332.core.api.discovery.ASMDataProcessor;
import elec332.core.api.util.StaticProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 24-9-2016.
 */
@SuppressWarnings("unused")
@ASMDataProcessor(LoaderState.PREINITIALIZATION)
public class PreInitAnnotationProcessor extends AbstractAnnotationProcessor {

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

        registerDataProcessor(StaticProxy.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            public void accept(ASMDataTable.ASMData asmData) {
                try {
                    Class<?> clazz = loadClass(asmData, false);
                    if (clazz == null){
                        logger.error("Error injecting proxy: "+asmData.getClassName());
                        return;
                    }
                    Field f = clazz.getDeclaredField(asmData.getObjectName());
                    f.setAccessible(true);
                    //The class is already loaded anyways...
                    StaticProxy proxyA = f.getAnnotation(StaticProxy.class);
                    Side side = FMLCommonHandler.instance().getSide();
                    String proxyClazzName = side.isClient() ? proxyA.clientSide() : proxyA.serverSide();
                    if(proxyClazzName.equals("")) {
                        proxyClazzName = asmData.getClassName() + (side.isClient() ? "$ClientProxy" : "$ServerProxy");
                    }
                    Object proxy = Class.forName(proxyClazzName).newInstance();
                    if ((f.getModifiers() & Modifier.STATIC) != 0){
                        logger.error("Field "+f.getName()+" in class "+clazz.getCanonicalName()+" is not static, skipping proxy injection...");
                        return;
                    }
                    if (!f.getType().isAssignableFrom(proxy.getClass())) {
                        throw new IllegalArgumentException();
                    }
                    f.set(null, proxy);
                } catch (Exception e){
                    logger.error("Error injecting proxy: ");
                    logger.error(e);
                }
            }

        });

    }

}
