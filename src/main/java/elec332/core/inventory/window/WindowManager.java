package elec332.core.inventory.window;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import elec332.core.ElecCore;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.inventory.NullInteractionObject;
import elec332.core.util.FMLHelper;
import elec332.core.world.WorldHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DefaultModContainers;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 29-11-2016.
 */
public enum WindowManager {

    INSTANCE;

    WindowManager() {
        this.lookup = HashBiMap.create();
    }

    private final BiMap<ResourceLocation, IWindowHandler> lookup;
    public static final ThreadLocal<EntityPlayer> currentOpeningPlayer = new ThreadLocal<>();

    private static final ResourceLocation GUI_NAME = new ResourceLocation(ElecCore.MODID, "window_gui");
    private static final ResourceLocation TILE_WINDOW_HANDLER = new ResourceLocation(ElecCore.MODID, "tile");

    public void register(IWindowHandler windowHandler) {
        ModContainer mc = FMLHelper.getActiveModContainer();
        if (mc == null || mc == DefaultModContainers.MINECRAFT){
            throw new RuntimeException();
        }
        register(windowHandler, new ResourceLocation(FMLHelper.getActiveModContainer().getModId(), windowHandler.getClass().getCanonicalName()));
    }

    public void register(IWindowHandler windowHandler, ResourceLocation name) {
        if (lookup.containsKey(name)){
            throw new IllegalArgumentException("Name already in use: "+name);
        }
        lookup.put(name, windowHandler);
    }

    public IWindowHandler get(ResourceLocation name){
        return lookup.get(name);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientWindow(Window window) {
        Minecraft.getInstance().displayGuiScreen(new WindowGui(ElecCore.proxy.getClientPlayer(), window));
    }

    public static void openTileWindow(@Nonnull EntityPlayer player, BlockPos pos) {
        openWindow(player, INSTANCE.get(TILE_WINDOW_HANDLER), bb -> bb.writeBlockPos(pos));
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, Consumer<ElecByteBuf> dataSupplier) {
        ElecByteBuf data = ElecByteBuf.of(Unpooled.buffer());
        dataSupplier.accept(data);
        openWindow(player, windowHandler, world, data);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, Consumer<ElecByteBuf> dataSupplier) {
        ElecByteBuf data = ElecByteBuf.of(Unpooled.buffer());
        dataSupplier.accept(data);
        openWindow(player, windowHandler, data);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, ElecByteBuf data) {
        openWindow(player, windowHandler, player.getEntityWorld(), data);
    }

    public static void openWindow(@Nonnull EntityPlayer player, IWindowHandler windowHandler, World world, ElecByteBuf data) {
        if (player instanceof EntityPlayerMP) {
            ResourceLocation name = INSTANCE.lookup.inverse().get(windowHandler);
            if (name == null){
                throw new IllegalArgumentException();
            }
            byte[] b = data.readByteArray();
            ElecByteBuf cdata = ElecByteBuf.of(Unpooled.wrappedBuffer(b));
            ElecByteBuf allData = ElecByteBuf.of(Unpooled.buffer());
            allData.writeResourceLocation(name);
            allData.writeBytes(b);
            NetworkHooks.openGui((EntityPlayerMP) player, new NullInteractionObject(GUI_NAME) {

                @Nonnull
                @Override
                public Container createContainer(@Nonnull InventoryPlayer inventoryPlayer, @Nonnull EntityPlayer entityPlayer) {
                    return INSTANCE.getServerGuiElement(entityPlayer, world, cdata, windowHandler);
                }

            }, allData);
            return;
        }
        throw new IllegalArgumentException();
    }

    public synchronized WindowContainer getServerGuiElement(EntityPlayer player, World world, ElecByteBuf data, IWindowHandler windowHandler) {
        currentOpeningPlayer.set(player);
        WindowContainer ret = new WindowContainer(player, windowHandler.createWindow(player, world, data));
        currentOpeningPlayer.remove();
        return ret;
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

    static {
        INSTANCE.register((player, world, data) -> {
            TileEntity tile = WorldHelper.getTileAt(world, data.readBlockPos());
            if (tile instanceof IWindowFactory){
                return ((IWindowFactory) tile).createWindow();
            }
            return null;
        }, TILE_WINDOW_HANDLER);
    }

}
