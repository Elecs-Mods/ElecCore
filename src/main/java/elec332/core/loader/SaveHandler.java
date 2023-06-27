package elec332.core.loader;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import elec332.core.ElecCore;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.storage.IExternalSaveHandler;
import elec332.core.util.FMLHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.WorldPersistenceHooks;

/**
 * Created by Elec332 on 20-7-2016.
 */
@StaticLoad
public enum SaveHandler {

    INSTANCE;

    SaveHandler() {
        this.saveHandlers = LinkedListMultimap.create();
        this.loaded = false;
        WorldPersistenceHooks.addHook(new WorldPersistenceHooks.WorldPersistenceHook() {

            @Override
            public String getModId() {
                return SaveHandler.folder;
            }

            @Override
            public CompoundNBT getDataForWriting(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo) {
                return SaveHandler.this.save(levelSave, serverInfo);
            }

            @Override
            public void readData(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tag) {
                SaveHandler.this.load(levelSave, serverInfo, tag);
            }

        });
    }

    private final ListMultimap<ModContainer, IExternalSaveHandler> saveHandlers;
    private static final String folder = "eleccore:managed_extradata";
    private boolean loaded;

    public boolean registerSaveHandler(ModContainer mc, IExternalSaveHandler saveHandler) {
        if (!FMLHelper.isInModInitialisation()) {
            return false;
        }
        saveHandlers.put(mc, saveHandler);
        return true;
    }

    private void load(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT base) {
        ElecCore.logger.info("Loading world data for: " + levelSave.func_237282_a_());
        CompoundNBT tag;
        Preconditions.checkNotNull(levelSave);
        Preconditions.checkNotNull(serverInfo);
        for (ModContainer mc : saveHandlers.keySet()) {
            tag = base.getCompound(mc.getModId());
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                Preconditions.checkNotNull(saveHandler);
                Preconditions.checkNotNull(tag);
                saveHandler.load(levelSave, serverInfo, tag.getCompound(saveHandler.getName()));
            }
        }
        this.loaded = true;
    }

    private CompoundNBT save(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo) {
        if (!this.loaded && !ElecCore.suppressSpongeIssues && serverInfo.func_230407_G_().isInitialized()) {
            ElecCore.logger.error("World is unloading before data has been loaded, skipping data saving...");
            ElecCore.logger.error("This probably happened due to a crash in EG worldgen.");
            ElecCore.logger.error("All external data will be lost.");
            return new CompoundNBT();
        }
        Preconditions.checkNotNull(levelSave);
        Preconditions.checkNotNull(serverInfo);
        CompoundNBT main = new CompoundNBT();
        CompoundNBT tag;
        for (ModContainer mc : saveHandlers.keySet()) {
            tag = new CompoundNBT();
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                CompoundNBT n = saveHandler.save(levelSave, serverInfo);
                if (n != null) {
                    tag.put(saveHandler.getName(), n);
                }
            }
            main.put(mc.getModId(), tag);
        }
        return main;
    }

    private void unLoad(IWorld world) {
        this.loaded = false;
        for (ModContainer mc : saveHandlers.keySet()) {
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                saveHandler.nullifyData();
            }
        }
    }

    private static class EventHandler {

        @SubscribeEvent
        public void worldUnload(WorldEvent.Unload event) {
            if (isOverworld(event.getWorld())) {
                INSTANCE.unLoad(event.getWorld());
            }
        }

        private boolean isOverworld(IWorld world) {
            return !world.isRemote() && WorldHelper.getDimID(world) == WorldHelper.OVERWORLD && world.getClass() == ServerWorld.class;
        }

    }

    static {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

}
