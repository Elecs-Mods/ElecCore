package elec332.core.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import elec332.core.ElecCore;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.util.FMLHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
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
            public CompoundNBT getDataForWriting(net.minecraft.world.storage.SaveHandler handler, WorldInfo info) {
                return SaveHandler.this.save(handler, info);
            }

            @Override
            public void readData(net.minecraft.world.storage.SaveHandler handler, WorldInfo info, CompoundNBT tag) {
                SaveHandler.this.load(handler, info, tag);
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

    private void load(net.minecraft.world.storage.SaveHandler save, WorldInfo worldInfo, CompoundNBT base) {
        ElecCore.logger.info("Loading world data for: " + worldInfo.getWorldName());
        CompoundNBT tag;
        Preconditions.checkNotNull(save);
        Preconditions.checkNotNull(worldInfo);
        for (ModContainer mc : saveHandlers.keySet()) {
            tag = base.getCompound(mc.getModId());
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                Preconditions.checkNotNull(saveHandler);
                Preconditions.checkNotNull(tag);
                saveHandler.load(save, worldInfo, tag.getCompound(saveHandler.getName()));
            }
        }
        this.loaded = true;
    }

    private CompoundNBT save(net.minecraft.world.storage.SaveHandler save, WorldInfo worldInfo) {
        if (!this.loaded && !ElecCore.suppressSpongeIssues) {
            ElecCore.logger.error("World is unloading before data has been loaded, skipping data saving...");
            ElecCore.logger.error("This probably happened due to a crash in EG worldgen.");
            ElecCore.logger.error("All external data will be lost.");
            return new CompoundNBT();
        }
        Preconditions.checkNotNull(save);
        Preconditions.checkNotNull(worldInfo);
        CompoundNBT main = new CompoundNBT();
        CompoundNBT tag;
        for (ModContainer mc : saveHandlers.keySet()) {
            tag = new CompoundNBT();
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                CompoundNBT n = saveHandler.save(save, worldInfo);
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
            return !world.isRemote() && WorldHelper.getDimID(world) == DimensionType.OVERWORLD && world.getClass() == ServerWorld.class;
        }

    }

    static {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

}
