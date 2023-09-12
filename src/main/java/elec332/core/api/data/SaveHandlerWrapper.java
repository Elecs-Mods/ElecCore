package elec332.core.api.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 22-10-2016.
 */
public class SaveHandlerWrapper implements IExternalSaveHandler {

    public static SaveHandlerWrapper wrap(String name, IExternalSaveHandler... handlers) {
        return new SaveHandlerWrapper(name, handlers);
    }

    private SaveHandlerWrapper(String name, IExternalSaveHandler[] saveHandlers) {
        this.saveHandlers = saveHandlers;
        this.name = name;
    }

    private final IExternalSaveHandler[] saveHandlers;
    private final String name;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void load(SaveHandler saveHandler, WorldInfo info, CompoundTag tag) {
        for (IExternalSaveHandler saveHandler1 : saveHandlers) {
            saveHandler1.load(saveHandler, info, tag.getCompound(saveHandler1.getName()));
        }
    }

    @Nullable
    @Override
    public CompoundTag save(SaveHandler saveHandler, WorldInfo info) {
        CompoundTag ret = new CompoundTag();
        for (IExternalSaveHandler saveHandler1 : saveHandlers) {
            CompoundTag tag = saveHandler1.save(saveHandler, info);
            if (tag != null) {
                ret.put(saveHandler1.getName(), tag);
            }
        }
        return ret;
    }

    @Override
    public void nullifyData() {
        for (IExternalSaveHandler saveHandler : saveHandlers) {
            saveHandler.nullifyData();
        }
    }

}
