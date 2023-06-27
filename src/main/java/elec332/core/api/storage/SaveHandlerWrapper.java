package elec332.core.api.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;

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
    public void load(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tag) {
        for (IExternalSaveHandler saveHandler1 : saveHandlers) {
            saveHandler1.load(levelSave, serverInfo, tag.getCompound(saveHandler1.getName()));
        }
    }

    @Nullable
    @Override
    public CompoundNBT save(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo) {
        CompoundNBT ret = new CompoundNBT();
        for (IExternalSaveHandler saveHandler1 : saveHandlers) {
            CompoundNBT tag = saveHandler1.save(levelSave, serverInfo);
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
