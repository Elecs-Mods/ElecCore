package elec332.core.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.helper.ArrayHelper;
import elec332.core.helper.StringHelper;
import elec332.core.util.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ElecPlayer {

    protected ElecPlayer(UUID uuid){
        this.uuid = uuid;
        this.online = false;
    }

    protected final void setExtendedProperties(Map<String, Class<? extends ExtendedProperties>> extendedProperties){
        if (this.extendedProperties != null)
            throw new RuntimeException("You cannot set ExtendedProperties twice!");
        Map<String, ExtendedProperties> toHandle = Maps.newHashMap();
        for (Map.Entry<String, Class<? extends ExtendedProperties>> entry : extendedProperties.entrySet()){
            try {
                ExtendedProperties prop = entry.getValue().newInstance();
                prop.init(this);
                toHandle.put(entry.getKey(), prop);
            } catch (Exception e){
                throw new IllegalArgumentException("Error adding ExtendedProperties to player: "+entry.getValue().getName());
            }
        }
        this.extendedProperties = toHandle;
    }

    private Map<String, ExtendedProperties> extendedProperties;
    private boolean online;
    private NBTHelper data;
    private UUID uuid;

    public ExtendedProperties getExtendedProperty(String s){
        return extendedProperties.get(s);
    }

    public EntityPlayer getRealPlayer(){
        return ServerHelper.instance.getRealPlayer(uuid);
    }

    public UUID getPlayerUUID(){
        return this.uuid;
    }

    public void readFromNBT(NBTTagCompound tag){
        this.data = new NBTHelper(tag.getCompoundTag("main"));
        NBTTagList tagList = tag.getTagList("props", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            String name = tagCompound.getString("name");
            NBTTagCompound data = tagCompound.getCompoundTag("data");
            if (data == null)
                data = new NBTTagCompound();
            ExtendedProperties properties = extendedProperties.get(name);
            if (properties != null){
                properties.readFromNBT(data);
            }
        }
    }

    public NBTTagCompound saveToNBT(){
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("main", data.toNBT());
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<String, ExtendedProperties> entry : extendedProperties.entrySet()){
            NBTTagCompound toAdd = new NBTTagCompound();
            toAdd.setString("name", entry.getKey());
            NBTTagCompound data = new NBTTagCompound();
            entry.getValue().writeToNBT(data);
            toAdd.setTag("data", data);
            tagList.appendTag(toAdd);
        }
        tag.setTag("props", tagList);
        return tag;
    }

    public boolean isOnline(){
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public NBTHelper getData() {
        return data;
    }

    public abstract static class ExtendedProperties{

        public EntityPlayer getPlayer(){
            return ServerHelper.instance.getRealPlayer(uuid);
        }

        public boolean isPlayerOnline(){
            return ServerHelper.instance.isPlayerOnline(uuid);
        }

        public UUID getPlayerUUID(){
            return uuid;
        }

        private UUID uuid;

        public abstract void readFromNBT(NBTTagCompound tag);

        public abstract void writeToNBT(NBTTagCompound tag);

        private void init(ElecPlayer player){
            this.uuid = player.getPlayerUUID();
        }

    }
}
