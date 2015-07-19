package elec332.core.server;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import elec332.core.player.PlayerHelper;
import elec332.core.util.EventHelper;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ServerHelper {

    public static final ServerHelper instance = new ServerHelper();

    private ServerHelper(){
        EventHelper.registerHandlerForgeAndFML(new EventHandler());
        this.playerData = Maps.newHashMap();
        this.worldData = Maps.newHashMap();
        this.extendedPropertiesList = Maps.newHashMap();
        this.locked = false;
        setInvalid();
    }

    public void registerExtendedProperties(String identifier, Class<? extends ElecPlayer.ExtendedProperties> propClass){
        if (extendedPropertiesList.keySet().contains(identifier))
            throw new IllegalArgumentException("Property for "+identifier+" has already been registered!");
        if (Loader.instance().hasReachedState(LoaderState.AVAILABLE) || locked)
            throw new IllegalArgumentException("Mod is attempting to register properties too late: "+identifier+"  "+propClass.getName());
        extendedPropertiesList.put(identifier, propClass);
    }

    private final Map<String, Class<? extends ElecPlayer.ExtendedProperties>> extendedPropertiesList;
    private NBTHelper generalData;
    private Map<UUID, ElecPlayer> playerData;
    private Map<Integer, NBTHelper> worldData;
    private boolean locked; //Extra safety, in case Loader.instance().hasReachedState(LoaderState.AVAILABLE) fails

    public ElecPlayer getPlayer(EntityPlayer player){
        return getPlayer(PlayerHelper.getPlayerUUID(player));
    }

    public ElecPlayer getPlayer(UUID uuid){
        if (isValid())
            return playerData.get(uuid);
        return null;
    }

    public NBTHelper getWorldData(World world){
        return getWorldData(WorldHelper.getDimID(world));
    }

    public NBTHelper getWorldData(int i){
        if (!isValid())
            return null;
        NBTHelper ret = worldData.get(i);
        if (ret == null){
            ret = new NBTHelper(new NBTTagCompound());
            worldData.put(i, ret);
        }
        return ret;
    }

    public NBTHelper getGlobalData(){
        if (isValid())
            return generalData;
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<EntityPlayer> getOnlinePlayers(){
        return (List<EntityPlayer>) getMinecraftServer().getConfigurationManager().playerEntityList;
    }

    public boolean isPlayerOnline(UUID uuid){
        return getPlayer(uuid).isOnline();
    }

    public EntityPlayer getRealPlayer(UUID uuid){
        if (isPlayerOnline(uuid)){
            for (EntityPlayer player : getOnlinePlayers()){
                if (PlayerHelper.getPlayerUUID(player).equals(uuid))
                    return player;
            }
        }
        return null;
    }

    public MinecraftServer getMinecraftServer(){
        return MinecraftServer.getServer();
    }

    public boolean isValid(){
        return getMinecraftServer() != null;
    }

    public static boolean isServer(World world){
        return !world.isRemote;
    }

    private void setInvalid(){
        this.generalData = null;
        this.playerData.clear();
        this.worldData.clear();
    }

    public class EventHandler{
        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event){
            if (isServer(event.world) && WorldHelper.getDimID(event.world) == 0){
                File folder = new File(event.world.getSaveHandler().getWorldDirectory(), "elec332/");
                ServerHelper.this.generalData = new NBTHelper(fromFile(new File(folder, "generalData.dat")));
                NBTTagList tagList1 = fromFile(new File(folder, "playerData.dat")).getTagList("playerData", 10);
                for (int i = 0; i < tagList1.tagCount(); i++) {
                    NBTTagCompound tagCompound = tagList1.getCompoundTagAt(i);
                    UUID uuid = UUID.fromString(tagCompound.getString("uuid"));
                    NBTTagCompound data = tagCompound.getCompoundTag("data");
                    ElecPlayer player = new ElecPlayer(uuid);
                    player.setExtendedProperties(extendedPropertiesList);
                    player.readFromNBT(data);
                    ServerHelper.this.playerData.put(uuid, player);
                }
                NBTTagList tagList2 = fromFile(new File(folder, "worldData.dat")).getTagList("dimData", 10);
                for (int i = 0; i < tagList2.tagCount(); i++) {
                    NBTTagCompound tagCompound = tagList2.getCompoundTagAt(i);
                    int dim = tagCompound.getInteger("dim");
                    NBTTagCompound data = tagCompound.getCompoundTag("data");
                    ServerHelper.this.worldData.put(dim, new NBTHelper(data));
                }
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event){
            if (isServer(event.world) && WorldHelper.getDimID(event.world) == 0){
                File folder = new File(event.world.getSaveHandler().getWorldDirectory(), "elec332/");
                toFile(ServerHelper.this.generalData.toNBT(), new File(folder, "generalData.dat"));
                NBTTagList tagList1 = new NBTTagList();
                for (UUID uuid : ServerHelper.this.playerData.keySet()){
                    tagList1.appendTag(new NBTHelper(new NBTTagCompound()).addToTag(uuid.toString(), "uuid").addToTag(ServerHelper.this.playerData.get(uuid).saveToNBT(), "data").toNBT());
                }
                toFile(new NBTHelper().addToTag(tagList1, "playerData").toNBT(), new File(folder, "playerData.dat"));
                NBTTagList tagList2 = new NBTTagList();
                for (Integer i : ServerHelper.this.worldData.keySet()){
                    tagList2.appendTag(new NBTHelper(new NBTTagCompound()).addToTag(i, "dim").addToTag(ServerHelper.this.worldData.get(i).toNBT(), "data").toNBT());
                }
                toFile(new NBTHelper().addToTag(tagList2, "dimData").toNBT(), new File(folder, "worldData.dat"));
            }
        }

        @SubscribeEvent
        public void onWorldUnload(WorldEvent.Unload event){
            if (WorldHelper.getDimID(event.world) == 0)
                ServerHelper.this.setInvalid();
        }

        @SubscribeEvent
        public void playerJoined(PlayerEvent.PlayerLoggedInEvent event){
            ServerHelper.this.locked = true;
            UUID uuid = PlayerHelper.getPlayerUUID(event.player);
            if (!ServerHelper.this.playerData.keySet().contains(uuid)) {
                ElecPlayer player = new ElecPlayer(uuid);
                player.setExtendedProperties(extendedPropertiesList);
                player.readFromNBT(new NBTTagCompound());
                ServerHelper.this.playerData.put(uuid, player);
            }
            ServerHelper.this.playerData.get(uuid).setOnline(true);
        }

        @SubscribeEvent
        public void onPlayerDisconnected(PlayerEvent.PlayerLoggedOutEvent event){
            ServerHelper.this.playerData.get(PlayerHelper.getPlayerUUID(event.player)).setOnline(false);
        }
    }

    public static NBTTagCompound fromFile(File file){
        if (file == null)
            return null;
        try {
            if (!file.exists()) {
                createFile(file);
                return new NBTTagCompound();
            }
            return CompressedStreamTools.read(file);
        } catch (IOException e){
            //Bad luck for you
            e.printStackTrace();
            return null;
        }
    }

    public static void createFile(File file) throws IOException{
        if (!file.exists())
            if (!file.getParentFile().mkdirs() && !file.createNewFile())
                throw new IOException();
    }

    public void toFile(NBTTagCompound tagCompound, File file){
        try {
            CompressedStreamTools.write(tagCompound, file);
        } catch (IOException e){
            //Bad luck for you
            e.printStackTrace();
        }
    }
}
