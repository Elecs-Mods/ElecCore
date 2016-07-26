package elec332.core.server;

import com.google.common.collect.Lists;
import elec332.core.api.annotations.ASMDataProcessor;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import elec332.core.main.ElecCore;
import elec332.core.world.WorldHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 20-7-2016.
 */
public enum SaveHandler {

    INSTANCE;

    SaveHandler(){
        saveHandlers = Lists.newArrayList();
    }

    private final List<IExternalSaveHandler> saveHandlers;

    public void dummyLoad(){
    }

    private void load(File worldDir){
        for (IExternalSaveHandler saveHandler : saveHandlers){
            saveHandler.load(worldDir);
        }
    }

    private void save(File worldDir){
        for (IExternalSaveHandler saveHandler : saveHandlers){
            saveHandler.save(worldDir);
        }
    }

    public static class EventHandler{

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event){
            if (ServerHelper.isServer(event.getWorld()) && WorldHelper.getDimID(event.getWorld()) == 0 && event.getWorld().getClass() == WorldServer.class){
                INSTANCE.load(event.getWorld().getSaveHandler().getWorldDirectory());
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event){
            if (ServerHelper.isServer(event.getWorld()) && WorldHelper.getDimID(event.getWorld()) == 0 && event.getWorld().getClass() == WorldServer.class){
                INSTANCE.save(event.getWorld().getSaveHandler().getWorldDirectory());
            }
        }
    }

    @ASMDataProcessor(LoaderState.POSTINITIALIZATION)
    public static class ASMLoader implements IASMDataProcessor {

        @Override
        public void processASMData(IASMDataHelper asmData, LoaderState state) {
            if (INSTANCE.saveHandlers.isEmpty()){
                ElecCore.logger.info("Initializing SaveHandlers.");
                Set<ASMDataTable.ASMData> dataSet = asmData.getAnnotationList(ExternalSaveHandler.class);
                for (ASMDataTable.ASMData data : dataSet){
                    try {
                        @SuppressWarnings("unchecked")
                        Class<?> clazz = getClass().getClassLoader().loadClass(data.getClassName());
                        if (IExternalSaveHandler.class.isAssignableFrom(clazz)) {
                            INSTANCE.saveHandlers.add((IExternalSaveHandler) clazz.newInstance());
                        }
                    } catch (ClassNotFoundException e) {
                        //;
                    } catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
                ElecCore.logger.info("Initializing SaveHandlers complete.");
            } else {
                throw new IllegalStateException();
            }
        }

    }

    static {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

}
