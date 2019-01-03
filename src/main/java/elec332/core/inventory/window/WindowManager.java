package elec332.core.inventory.window;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.MC113ToDoReference;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.network.simple.ISimplePacket;
import elec332.core.api.network.simple.ISimplePacketHandler;
import elec332.core.util.FMLHelper;
import elec332.core.util.NBTTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Elec332 on 29-11-2016.
 */
public enum WindowManager implements ISimplePacket, ISimplePacketHandler {

    INSTANCE;

    WindowManager() {
        ElecCore.networkHandler.registerSimplePacket(this, this);
        MinecraftForge.EVENT_BUS.register(new Object() {

            @SubscribeEvent
            public void onPlayerConnected(PlayerEvent.PlayerLoggedInEvent event) {
                if (event.player instanceof EntityPlayerMP) {
                    ElecCore.networkHandler.sendTo(WindowManager.this, (EntityPlayerMP) event.player);
                }
            }

        });
        this.names = HashBiMap.create();
        this.map = Maps.newHashMap();
        this.lookup = Maps.newHashMap();
        this.index = 0;
    }

    private final BiMap<Integer, String> names;
    private final Map<IWindowHandler, Entry> map;
    private final Map<String, IWindowHandler> lookup;
    private int index;

    public void register(IWindowHandler windowHandler) {
        register(windowHandler, new ResourceLocation(FMLHelper.getActiveModContainer().getModId(), windowHandler.getClass().getCanonicalName()));
    }

    public void register(IWindowHandler windowHandler, ResourceLocation name) {
        index++;
        Entry entry = new Entry(windowHandler, name);
        register(entry, index);
    }

    private void register(Entry entry, int i) {
        if (!FMLHelper.isInModInitialisation()) {
            throw new IllegalStateException("Cannot register window handlers after mod loading.");
        }
        if (names.containsValue(entry.toString())) {
            throw new IllegalArgumentException("There is already a registered WindowHandler for name: " + entry.name);
        }
        map.put(entry.windowHandler, entry);
        lookup.put(entry.name, entry.windowHandler);
        names.put(i, entry.name);
    }

    public int getID(IWindowHandler windowHandler) {
        Entry ret = map.get(windowHandler);
        if (ret == null) {
            throw new RuntimeException("WindowHandler " + windowHandler + " has not been registered!");
        }
        return ret.getId();
    }

    public IWindowHandler get(int id) {
        return lookup.get(names.get(id));
    }

    @Override
    public void onPacket(ElecByteBuf data, IExtendedMessageContext messageContext, ISimpleNetworkPacketManager networkHandler) {
        NBTTagCompound tag1 = data.readNBTTagCompoundFromBuffer();
        if (tag1 == null) {
            throw new IllegalArgumentException();
        }
        NBTTagList list = tag1.getList("list", NBTTypes.COMPOUND.getID());
        names.clear();
        for (int i = 0; i < list.size(); i++) {
            NBTTagCompound tag = list.getCompound(i);
            names.put(tag.getInt("i"), tag.getString("n"));
        }
    }

    @Override
    public void toBytes(ElecByteBuf byteBuf) {
        NBTTagList list = new NBTTagList();
        NBTTagCompound tag;
        for (Map.Entry<Integer, String> entry : names.entrySet()) {
            tag = new NBTTagCompound();
            tag.putInt("i", entry.getKey());
            tag.putString("n", entry.getValue());
            list.add(tag);
        }
        NBTTagCompound tag1 = new NBTTagCompound();
        tag1.put("list", list);
        byteBuf.writeNBTTagCompoundToBuffer(tag1);
    }

    @Override
    public ISimplePacketHandler getPacketHandler() {
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientWindow(Window window) {
        Minecraft.getInstance().displayGuiScreen(new WindowGui(ElecCore.proxy.getClientPlayer(), window));
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, BlockPos pos) {
        openWindow(player, windowHandler, world, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, BlockPos pos) {
        openWindow(player, windowHandler, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, BlockPos pos, byte id) {
        openWindow(player, windowHandler, world, pos.getX(), pos.getY(), pos.getZ(), id);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, BlockPos pos, byte id) {
        openWindow(player, windowHandler, pos.getX(), pos.getY(), pos.getZ(), id);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, int x, int y, int z) {
        openWindow(player, windowHandler, world, x, y, z, (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, int x, int y, int z) {
        openWindow(player, windowHandler, x, y, z, (byte) 0);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, int x, int y, int z, byte id) {
        openWindow(player, windowHandler, player.getEntityWorld(), x, y, z, id);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, int x, int y, int z, byte id) {
        MC113ToDoReference.update();
        //player.openGui(ElecCore.instance, (id << 8) + INSTANCE.getID(windowHandler), world, x, y, z);
    }

    @Nullable
    public static Window getOpenWindow(EntityPlayer player) {
        Container container = player.openContainer;
        if (container instanceof WindowContainer) {
            return ((WindowContainer) container).getWindow();
        }
        return null;
    }

    @Nullable
    public static Window getOpenWindow(EntityPlayer player, int windowID) {
        Window window = getOpenWindow(player);
        if (window != null && window.getWindowID() == windowID) {
            return window;
        }
        return null;
    }

    private class Entry {

        private Entry(IWindowHandler windowHandler, ResourceLocation name) {
            this.windowHandler = windowHandler;
            this.name = name.toString();
        }

        private final IWindowHandler windowHandler;
        private final String name;

        private int getId() {
            return names.inverse().get(name);
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
