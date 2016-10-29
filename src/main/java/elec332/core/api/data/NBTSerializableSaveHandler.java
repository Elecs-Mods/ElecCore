package elec332.core.api.data;

import com.google.common.base.Preconditions;
import elec332.core.api.util.IClearable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 22-10-2016.
 */
public class NBTSerializableSaveHandler implements IExternalSaveHandler {

    public NBTSerializableSaveHandler(String name, INBTSerializable<NBTTagCompound> nbtSerializable){
        this.name = Preconditions.checkNotNull(name);
        this.nbtSerializable = Preconditions.checkNotNull(nbtSerializable);
        this.clearable = nbtSerializable instanceof IClearable ? (IClearable) nbtSerializable : null;
    }

    private final INBTSerializable<NBTTagCompound> nbtSerializable;
    private final IClearable clearable;
    private final String name;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void load(ISaveHandler saveHandler, WorldInfo info, NBTTagCompound tag) {
        nbtSerializable.deserializeNBT(tag);
    }

    @Nullable
    @Override
    public NBTTagCompound save(ISaveHandler saveHandler, WorldInfo info) {
        return nbtSerializable.serializeNBT();
    }

    @Override
    public void nullifyData() {
        if (clearable != null){
            clearable.clear();
        }
    }

}
