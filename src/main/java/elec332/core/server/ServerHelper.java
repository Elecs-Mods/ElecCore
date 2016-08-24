package elec332.core.server;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import elec332.core.main.ElecCore;
import elec332.core.nbt.NBTMap;
import elec332.core.network.NetworkHandler;
import elec332.core.util.NBTHelper;
import elec332.core.util.PlayerHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.io.FileUtils;
import scala.collection.generic.Clearable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by Elec332 on 28-5-2015.
 */
@SuppressWarnings("unused")
public class ServerHelper {

    public static final ServerHelper instance = new ServerHelper();

    private ServerHelper(){
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        this.playerData = NBTMap.newNBTMap(UUID.class, ElecPlayer.class, new Function<UUID, ElecPlayer>() {
            @Override
            public ElecPlayer apply(UUID input) {
                ElecPlayer ret = new ElecPlayer(input);
                ret.setExtendedProperties(extendedPropertiesList);
                return ret;
            }
        });
        this.worldData = NBTMap.newNBTMap(Integer.class, NBTHelper.class, new NBTHelper.DefaultFunction<Integer>());
        this.savedData = NBTMap.newNBTMap(String.class, INBTSerializable.class, new Function<String, INBTSerializable>() {
            @Nullable
            @Override
            public INBTSerializable apply(@Nullable String input) {
                try {
                    return extendedSaveData.get(input).call();
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
        this.extendedPropertiesList_ = Maps.newHashMap();
        this.extendedPropertiesList = Collections.unmodifiableMap(extendedPropertiesList_);
        this.extendedSaveData_ = Maps.newHashMap();
        this.extendedSaveData = Collections.unmodifiableMap(extendedSaveData_);
        //this.saveDataInstances = Maps.newHashMap();
        this.locked = false;
        setInvalid();
    }

    /**
     * Dummy method to make sure this gets registered on the Forge EventBus before world load
     */
    public void load(){
        SaveHandler.INSTANCE.dummyLoad();
    }

    public void registerExtendedPlayerProperties(String identifier, Class<? extends ElecPlayer.ExtendedProperties> propClass){
        if (extendedPropertiesList_.keySet().contains(identifier))
            throw new IllegalArgumentException("Property for "+identifier+" has already been registered!");
        if (Loader.instance().hasReachedState(LoaderState.AVAILABLE) || locked)
            throw new IllegalArgumentException("Mod is attempting to register properties too late: "+identifier+"  "+propClass.getName());
        extendedPropertiesList_.put(identifier, propClass);
    }

    public void registerExtendedProperties(String s, Callable<INBTSerializable> callable){
        if (extendedSaveData_.containsKey(s))
            throw new IllegalArgumentException("Property for "+s+" has already been registered!");
        if (Loader.instance().hasReachedState(LoaderState.AVAILABLE) || locked)
            throw new IllegalArgumentException("Mod is attempting to register properties too late: "+s+"  "+callable.getClass());
        extendedSaveData_.put(s, callable);
    }

    private final Map<String, Class<? extends ElecPlayer.ExtendedProperties>> extendedPropertiesList_;
    private final Map<String, Callable<INBTSerializable>> extendedSaveData_;
    private final Map<String, Class<? extends ElecPlayer.ExtendedProperties>> extendedPropertiesList;
    private final Map<String, Callable<INBTSerializable>> extendedSaveData;

    private NBTHelper generalData;
    private NBTMap<UUID, ElecPlayer> playerData;
    private NBTMap<Integer, NBTHelper> worldData;
    private NBTMap<String, INBTSerializable> savedData;
    //private Map<String, INBTSerializable<NBTTagCompound>> saveDataInstances;
    private boolean locked; //Extra safety, in case Loader.instance().hasReachedState(LoaderState.AVAILABLE) fails
    private boolean loaded;

    public ElecPlayer getPlayer(EntityPlayer player){
        return getPlayer(PlayerHelper.getPlayerUUID(player));
    }

    public ElecPlayer getPlayer(@Nonnull UUID uuid){
        if (isValid()) {
            return playerData.get(uuid);
        }
        return null;
    }

    public NBTHelper getPersistentWorldData(World world){
        return getPersistentWorldData(WorldHelper.getDimID(world));
    }

    public NBTHelper getPersistentWorldData(int i){
        if (!isValid())
            return null;
        NBTHelper ret = worldData.get(i);
        if (ret == null){
            ret = new NBTHelper(new NBTTagCompound());
            worldData.put(i, ret);
        }
        return ret;
    }

    public NBTHelper getPersistentGlobalData(){
        if (isValid()) {
            return generalData;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends INBTSerializable<NBTTagCompound>> T getExtendedSaveData(Class<T> clazz, String name){
        return clazz.cast(getExtendedSaveData(name));
    }

    public INBTSerializable getExtendedSaveData(String name){
        return savedData.get(name);
    }

    @SuppressWarnings("unchecked")
    public List<EntityPlayerMP> getOnlinePlayers(){
        return getMinecraftServer().getPlayerList().getPlayerList();
    }

    public boolean isPlayerOnline(UUID uuid){
        if (uuid == null){
            return false;
        }
        ElecPlayer player = getPlayer(uuid);
        return player != null && getPlayer(uuid).isOnline();
    }

    public EntityPlayerMP getRealPlayer(UUID uuid){
        if (isPlayerOnline(uuid)){
            for (EntityPlayerMP player : getOnlinePlayers()){
                if (PlayerHelper.getPlayerUUID(player).equals(uuid)) {
                    return player;
                }
            }
        }
        return null;
    }

    public List<EntityPlayerMP> getAllPlayersWatchingBlock(World world, BlockPos pos){
        return getAllPlayersWatchingBlock(world, pos.getX(), pos.getZ());
    }

    public List<EntityPlayerMP> getAllPlayersWatchingBlock(World world, int x, int z){
        List<EntityPlayerMP> ret = Lists.newArrayList();
        if (world instanceof WorldServer) {
            PlayerChunkMap playerManager = ((WorldServer) world).getPlayerChunkMap();
            for (EntityPlayerMP player : getOnlinePlayers()) {
                Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
                if (playerManager.isPlayerWatchingChunk(player, chunk.xPosition, chunk.zPosition)) {
                    ret.add(player);
                }
            }
        }
        return ret;
    }

    public void sendMessageToAllPlayersWatchingBlock(World world, BlockPos pos, IMessage message, NetworkHandler networkHandler){
        for (EntityPlayerMP player : getAllPlayersWatchingBlock(world, pos)) {
            networkHandler.getNetworkWrapper().sendTo(message, player);
        }
    }

    public List<EntityPlayerMP> getAllPlayersInDimension(int dimension){
        List<EntityPlayerMP> ret = Lists.newArrayList();
        for (EntityPlayerMP player : getOnlinePlayers()){
            if (WorldHelper.getDimID(player.worldObj) == dimension){
                ret.add(player);
            }
        }
        return ret;
    }

    public void sendMessageToAllPlayersInDimension(int dimension, IMessage message, NetworkHandler networkHandler){
        for (EntityPlayerMP playerMP : getAllPlayersInDimension(dimension)){
            networkHandler.getNetworkWrapper().sendTo(message, playerMP);
        }
    }

    public MinecraftServer getMinecraftServer(){
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public boolean isValid(){
        boolean ret = getMinecraftServer() != null;
        if (!ret){
            ElecCore.logger.error("Someone tried to access the server whilst it was in an invalid state!");
        }
        return ret;
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
        if (errorBefore) {
            ElecCore.logger.error("Starting thorough investigation...");
        }
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
            if (!error) {
                ElecCore.logger.error("No additional errors found...");
            }
            ElecCore.logger.error("Finished investigation.");
        }
    }

    public class EventHandler{

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event){
            if (isServer(event.getWorld()) && WorldHelper.getDimID(event.getWorld()) == 0 && event.getWorld().getClass() == WorldServer.class){
                //System.out.println("loading data: "+event.getWorld()+"  "+!event.world.isRemote);

                if (!ServerHelper.this.locked) {
                    ServerHelper.this.locked = true;
                }

                File folder = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "elec332/");

                ServerHelper.this.generalData = new NBTHelper(fromFile(new File(folder, "generalData.dat")));

                NBTTagList tagList1 = fromFile(new File(folder, "playerData.dat")).getTagList("playerData", 10);
                playerData.deserializeNBT(tagList1);

                NBTTagList tagList2 = fromFile(new File(folder, "worldData.dat")).getTagList("dimData", 10);
                worldData.deserializeNBT(tagList2);

                for (Object o : savedData.values()){
                    if (o instanceof Clearable){
                        ((Clearable) o).clear();
                    }
                }

                NBTTagList tagList3 = fromFile(new File(folder, "savedData.dat")).getTagList("savedData", 10);
                savedData.deserializeNBT(tagList3);

                for (Map.Entry<String, Callable<INBTSerializable>> entry : extendedSaveData.entrySet()){
                    if (!savedData.keySet().contains(entry.getKey())){
                        try {
                            savedData.put(entry.getKey(), entry.getValue().call());
                        } catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                }

                ServerHelper.this.loaded = true;
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event){
            if (isServer(event.getWorld()) && WorldHelper.getDimID(event.getWorld()) == 0 && event.getWorld().getClass() == WorldServer.class){
               // if (!ServerHelper.this.loaded){
               //     ElecCore.logger.error("World is unloading before data has been loaded, skipping data saving...");
               //     return;
               // }
                //System.out.println("saving: "+event.world);
                File folder = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "elec332/");

                toFile(ServerHelper.this.generalData.serializeNBT(), new File(folder, "generalData.dat"));

                NBTTagList tagList1 = playerData.serializeNBT();
                toFile(new NBTHelper().addToTag(tagList1, "playerData").serializeNBT(), new File(folder, "playerData.dat"));

                NBTTagList tagList2 = worldData.serializeNBT();
                toFile(new NBTHelper().addToTag(tagList2, "dimData").serializeNBT(), new File(folder, "worldData.dat"));


                /*String s = null;
                for (Map.Entry<String, INBTSerializable<NBTTagCompound>> entry : saveDataInstances.entrySet()){
                    if (savedData.containsKey(entry.getKey())){
                        s = entry.getKey();
                        continue;
                    }
                    savedData.put(entry.getKey(), entry.getValue().serializeNBT());
                }
                saveDataInstances.clear();*/
                NBTTagList tagList3 = savedData.serializeNBT();
                toFile(new NBTHelper().addToTag(tagList3, "savedData").serializeNBT(), new File(folder, "savedData.dat"));
                //if (s != null){
                //    throw new IllegalStateException("Duplicate names were used: "+s);
                //}
                ServerHelper.this.loaded = false;
            }
        }

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
                player.deserializeNBT(new NBTTagCompound());
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

    @Deprecated
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
                String date = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());
                String ext = file.getName().replace('.', ' ').split(" ")[1];
                File newFile = new File(file.getCanonicalPath().replace("."+ext, "-" + date + "."+ext));
                FileUtils.moveFile(file, newFile);
                //if (!file.delete()){
                //    ElecCore.logger.error("Error deleting file: "+file.getCanonicalPath()+", please remove it yourself.");
                //   throw new IOException();
                //}
                createFile(file);
                try {
                    File backup = getBackupFile(file);
                    if (backup.exists()) {
                        return CompressedStreamTools.read(backup);
                    }
                } catch (IOException ex){
                    ElecCore.logger.info("Failed to read backup file: "+file);
                }
                return new NBTTagCompound();
            }
        } catch (IOException e){
            ElecCore.logger.error("Failed to load "+file, e);
            return new NBTTagCompound();
        }
    }

    @Deprecated
    public static void createFile(File file) throws IOException {
        if (!file.exists())
            if (!file.getParentFile().mkdirs() && !file.createNewFile())
                throw new IOException();
    }

    @Deprecated
    public void toFile(NBTTagCompound tagCompound, File file){
        try {
            if (file.exists()) {
                Files.move(file, getBackupFile(file));
            }
            file.createNewFile();
            CompressedStreamTools.write(tagCompound, file);
        } catch (IOException e){
            // This method is called at a sensitive time in the world saving
            // process, and if we were to throw an exception, the server will
            // crash and corrupt the ID map. This is obviously undesirable, and
            // as such, logging an error will have to do.
            ElecCore.logger.error("Failed to save "+file, e);
        }
    }

    @Deprecated
    private static File getBackupFile(File file) throws IOException {
        return new File(file.getCanonicalPath()+"_back");
    }

}
