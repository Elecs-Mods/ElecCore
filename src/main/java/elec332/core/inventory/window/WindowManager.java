package elec332.core.inventory.window;

import com.google.common.collect.Maps;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.network.simple.ISimplePacket;
import elec332.core.api.network.simple.ISimplePacketHandler;
import elec332.core.main.ElecCore;
import elec332.core.util.FMLUtil;
import elec332.core.util.NBTTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Elec332 on 29-11-2016.
 */
public enum WindowManager implements ISimplePacket, ISimplePacketHandler {

    INSTANCE;

    private WindowManager(){
        MinecraftForge.EVENT_BUS.register(new Object(){

            @SubscribeEvent
            public void onPlayerConnected(PlayerEvent.PlayerLoggedInEvent event){
                if (event.player instanceof EntityPlayerMP) {
                    ElecCore.networkHandler.sendTo(WindowManager.this, (EntityPlayerMP) event.player);
                }
            }

        });
        ElecCore.networkHandler.registerPacket(this, this);
        this.names = Maps.newHashMap();
        this.map = Maps.newHashMap();
        this.lookup = Maps.newHashMap();
        this.index = 0;
    }

    private final Map<Integer, String> names;
    private final Map<IWindowHandler, Entry> map;
    private final Map<String, IWindowHandler> lookup;
    private int index;

    public void register(IWindowHandler windowHandler){
        index++;
        Entry entry = new Entry(windowHandler, index);
        register(entry);
    }

    private void register(Entry entry){
        if (!FMLUtil.isInModInitialisation()){
            return;
        }
        if (names.containsValue(entry.toString())){
            entry.name = entry.name + "-";
            register(entry);
            return;
        }
        map.put(entry.windowHandler, entry);
        lookup.put(entry.name, entry.windowHandler);
        names.put(entry.id, entry.name);
    }

    public int getID(IWindowHandler windowHandler){
        Entry ret = map.get(windowHandler);
        if (ret == null){
            throw new RuntimeException("WindowHandler "+windowHandler+" has not been registered!");
        }
        return ret.id;
    }

    public IWindowHandler get(int id){
        return lookup.get(names.get(id));
    }

    @Override
    public void onPacket(ElecByteBuf data, IExtendedMessageContext messageContext, ISimpleNetworkPacketManager networkHandler) {
        NBTTagCompound tag1 = data.readNBTTagCompoundFromBuffer();
        NBTTagList list = tag1.getTagList("list", NBTTypes.COMPOUND.getID());
        names.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            names.put(tag.getInteger("i"), tag.getString("n"));
        }
    }

    @Override
    public void toBytes(ElecByteBuf byteBuf) {
        NBTTagList list = new NBTTagList();
        NBTTagCompound tag;
        for (Map.Entry<Integer, String> entry : names.entrySet()){
            tag = new NBTTagCompound();
            tag.setInteger("i", entry.getKey());
            tag.setString("n", entry.getValue());
            list.appendTag(tag);
        }
        NBTTagCompound tag1 = new NBTTagCompound();
        tag1.setTag("list", list);
        byteBuf.writeNBTTagCompoundToBuffer(tag1);
    }

    @SideOnly(Side.CLIENT)
    public static void openClientWindow(Window window){
        Minecraft.getMinecraft().displayGuiScreen(new WindowGui(ElecCore.proxy.getClientPlayer(), window));
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, BlockPos pos){
        openWindow(player, windowHandler, world, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, BlockPos pos){
        openWindow(player, windowHandler, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, BlockPos pos, byte id){
        openWindow(player, windowHandler, world, pos.getX(), pos.getY(), pos.getZ(), id);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, BlockPos pos, byte id){
        openWindow(player, windowHandler, pos.getX(), pos.getY(), pos.getZ(), id);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, int x, int y, int z){
        openWindow(player, windowHandler, world, x, y, z, (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, int x, int y, int z){
        openWindow(player, windowHandler, x, y, z, (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, int x, int y, int z, byte id){
        openWindow(player, windowHandler, player.getEntityWorld(), x, y, z, id);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, int x, int y, int z, byte id){
        player.openGui(ElecCore.instance, (id << 8) + INSTANCE.getID(windowHandler), world, x, y, z);
    }

    @Nullable
    public static Window getOpenWindow(EntityPlayer player){
        Container container = player.openContainer;
        if (container instanceof WindowContainer){
            return ((WindowContainer) container).getWindow();
        }
        return null;
    }
    @Nullable
    public static Window getOpenWindow(EntityPlayer player, int windowID){
        Window window = getOpenWindow(player);
        if (window != null && window.getWindowID() == windowID){
            return window;
        }
        return null;
    }

    private class Entry {

        private Entry(IWindowHandler windowHandler, int id){
            this.windowHandler = windowHandler;
            this.name = windowHandler.getClass().getCanonicalName();
            this.id = id;
        }

        private final IWindowHandler windowHandler;
        private String name;
        private final int id;

        @Override
        public String toString() {
            return name;
        }

    }

}
