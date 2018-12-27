package elec332.core.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import elec332.core.ElecCore;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.util.FMLUtil;
import elec332.core.util.IOHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

/**
 * Created by Elec332 on 20-7-2016.
 * <p>
 * TODO: Move to world cap
 */
@StaticLoad
public enum SaveHandler {

    INSTANCE;

    SaveHandler() {
        this.saveHandlers = LinkedListMultimap.create();
        this.loaded = false;
    }

    private final ListMultimap<ModContainer, IExternalSaveHandler> saveHandlers;
    private static final String folder = "extradata";
    private boolean loaded;

    public boolean registerSaveHandler(ModContainer mc, IExternalSaveHandler saveHandler) {
        if (!FMLUtil.isInModInitialisation()) {
            return false;
        }
        saveHandlers.put(mc, saveHandler);
        return true;
    }

    private void load(World world) {
        NBTTagCompound tag;
        File file = new File(world.getSaveHandler().getWorldDirectory(), folder);
        IOHelper.ensureExists(file);
        for (ModContainer mc : saveHandlers.keySet()) {
            tag = IOHelper.readWithPossibleBackup(new File(file, mc.getModId() + ".dat"), IOHelper.NBT_COMPRESSED_IO);
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                Preconditions.checkNotNull(world);
                Preconditions.checkNotNull(saveHandler);
                Preconditions.checkNotNull(tag);
                saveHandler.load(world.getSaveHandler(), world.getWorldInfo(), tag.getCompoundTag(saveHandler.getName()));
            }
        }

        this.loaded = true;
    }

    private void save(World world) {
        if (!this.loaded && !ElecCore.suppressSpongeIssues) {
            ElecCore.logger.error("World is unloading before data has been loaded, skipping data saving...");
            ElecCore.logger.error("This probably happened due to a crash in EG worldgen.");
            return;
        }
        NBTTagCompound tag;
        File file = new File(world.getSaveHandler().getWorldDirectory(), folder);
        for (ModContainer mc : saveHandlers.keySet()) {
            tag = new NBTTagCompound();
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                NBTTagCompound n = saveHandler.save(world.getSaveHandler(), world.getWorldInfo());
                if (n != null) {
                    tag.setTag(saveHandler.getName(), n);
                }
            }
            IOHelper.writeWithBackup(new File(file, mc.getModId() + ".dat"), tag, IOHelper.NBT_COMPRESSED_IO);
        }
    }

    private void unLoad(World world) {
        this.loaded = false;
        for (ModContainer mc : saveHandlers.keySet()) {
            for (IExternalSaveHandler saveHandler : saveHandlers.get(mc)) {
                saveHandler.nullifyData();
            }
        }
    }

    private static class EventHandler {

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            if (isOverworld(event.getWorld())) {
                INSTANCE.load(event.getWorld());
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event) {
            if (isOverworld(event.getWorld())) {
                INSTANCE.save(event.getWorld());
            }
        }

        @SubscribeEvent
        public void worldUnload(WorldEvent.Unload event) {
            if (isOverworld(event.getWorld())) {
                INSTANCE.unLoad(event.getWorld());
            }
        }

        private boolean isOverworld(World world) {
            return !world.isRemote && WorldHelper.getDimID(world) == 0 && world.getClass() == WorldServer.class;
        }

    }

    static {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

}
