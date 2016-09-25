package elec332.core.handler;

import elec332.core.api.annotations.ASMDataProcessor;
import elec332.core.api.annotations.StaticProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 24-9-2016.
 */
@SuppressWarnings("unused")
@ASMDataProcessor(LoaderState.PREINITIALIZATION)
public class PreInitAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    protected void registerProcesses() {

        registerDataProcessor(StaticProxy.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            public void accept(ASMDataTable.ASMData asmData) {
                try {
                    Class<?> clazz = Class.forName(asmData.getClassName(), true, Loader.instance().getModClassLoader());
                    Field f = clazz.getDeclaredField(asmData.getObjectName());
                    f.setAccessible(true);
                    //The class is already loaded anyways...
                    StaticProxy proxyA = clazz.getAnnotation(StaticProxy.class);
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
                    if (!f.getClass().isAssignableFrom(proxy.getClass())) {
                        throw new IllegalArgumentException();
                    }
                    f.set(null, proxy);
                } catch (Exception e){
                    logger.error("Error injecting proxy:");
                    logger.error(e);
                }
            }

        });

    }

}
