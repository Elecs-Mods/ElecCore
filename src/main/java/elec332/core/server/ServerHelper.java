package elec332.core.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import elec332.core.main.ElecCore;
import elec332.core.network.NetworkHandler;
import elec332.core.player.PlayerHelper;
import elec332.core.util.EventHelper;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.io.FileUtils;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
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

    /**
     * Dummy method to make sure this gets registered on the Forge EventBus before world load
     */
    public void load(){
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
    public List<EntityPlayerMP> getOnlinePlayers(){
        return (List<EntityPlayerMP>) getMinecraftServer().getConfigurationManager().playerEntityList;
    }

    public boolean isPlayerOnline(UUID uuid){
        return getPlayer(uuid).isOnline();
    }

    public EntityPlayerMP getRealPlayer(UUID uuid){
        if (isPlayerOnline(uuid)){
            for (EntityPlayerMP player : getOnlinePlayers()){
                if (PlayerHelper.getPlayerUUID(player).equals(uuid))
                    return player;
            }
        }
        return null;
    }

    public List<EntityPlayerMP> getAllPlayersWatchingBlock(World world, int x, int z){
        List<EntityPlayerMP> ret = Lists.newArrayList();
        if (world instanceof WorldServer) {
            PlayerManager playerManager = ((WorldServer) world).getPlayerManager();
            for (EntityPlayerMP player : getOnlinePlayers()) {
                Chunk chunk = world.getChunkFromBlockCoords(x, z);
                if (playerManager.isPlayerWatchingChunk(player, chunk.xPosition, chunk.zPosition))
                    ret.add(player);
            }
        }
        return ret;
    }

    public void sendMessageToAllPlayersWatchingBlock(World world, int x, int z, IMessage message, NetworkHandler networkHandler){
        for (EntityPlayerMP player : getAllPlayersWatchingBlock(world, x, z))
            networkHandler.getNetworkWrapper().sendTo(message, player);
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

    private void investigateErrors(boolean errorBefore){
        if (errorBefore)
            ElecCore.logger.error("Starting thorough investigation...");
        boolean error = false;
        for (EntityPlayerMP playerMP : getOnlinePlayers()){
            if (isValid()){
                ElecPlayer elecPlayer = getPlayer(playerMP);
                if (elecPlayer == null){
                    if (!errorBefore){
                        investigateErrors(true);
                        return;
                    }
                    error = true;
                    ElecCore.logger.error("Seems like there is a player online that also never properly connected: "+playerMP.getDisplayName());
                }
            }
        }
        for (ElecPlayer elecPlayer : ServerHelper.this.playerData.values()){
            if (elecPlayer.isOnline()) {
                EntityPlayerMP online = getRealPlayer(elecPlayer.getPlayerUUID());
                if (online == null) {
                    if (!errorBefore) {
                        investigateErrors(true);
                        return;
                    }
                    error = true;
                    ElecCore.logger.error("Seems like the player with UUID: " + elecPlayer.getPlayerUUID() + " never properly disconnected from the server.");
                }
            }
        }
        if (errorBefore) {
            if (!error)
                ElecCore.logger.error("No additional errors found...");
            ElecCore.logger.error("Finished investigation.");
        }
    }

    public class EventHandler{

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event){
            if (isServer(event.world) && WorldHelper.getDimID(event.world) == 0){
                if (!ServerHelper.this.locked)
                    ServerHelper.this.locked = true;
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

        /*@SubscribeEvent
        public void onWorldUnload(WorldEvent.Unload event){
            if (isServer(event.world) && WorldHelper.getDimID(event.world) == 0)
                ServerHelper.this.setInvalid();
        }*/

        @SubscribeEvent
        public void playerJoined(PlayerEvent.PlayerLoggedInEvent event){
            if (!(event.player instanceof EntityPlayerMP))
                return;
            if (!ServerHelper.this.locked)
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
            if (!(event.player instanceof EntityPlayerMP))
                return;
            ElecPlayer player = ServerHelper.this.playerData.get(PlayerHelper.getPlayerUUID(event.player));
            if (player == null){
                for (int i = 0; i < 30; i++) {
                    ElecCore.logger.error("A player disconnected from the server without connecting first!");
                    ElecCore.logger.error("Player: "+event.player.getDisplayName());
                }
                investigateErrors(true);
                return;
            }
            player.setOnline(false);
        }
    }

    public static NBTTagCompound fromFile(File file){
        if (file == null)
            return null;
        try {
            try {
                if (!file.exists()) {
                    createFile(file);
                    return new NBTTagCompound();
                }
                return CompressedStreamTools.read(file);
            } catch (EOFException e) {
                ElecCore.logger.error("Error reading NBT files, something weird must have happened when you last shutdown MC, unfortunately, some game data will be lost. Fixing file now....");
                String date = Calendar.getInstance().getTime().toString();
                String ext = file.getName().split(".")[1];
                File newFile = new File(file.getCanonicalPath().replace(ext, "-" + date + ext));
                FileUtils.moveFile(file, newFile);
                if (!file.delete()){
                    ElecCore.logger.error("Error deleting file: "+file.getCanonicalPath()+", please remove it yourself.");
                    throw new IOException();
                }
                createFile(file);
                return new NBTTagCompound();
            }
        } catch (IOException e){
            //Bad luck for you
            e.printStackTrace();
            throw new RuntimeException(e); //return null;
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
            throw new RuntimeException(e); //e.printStackTrace();
        }
    }
}
