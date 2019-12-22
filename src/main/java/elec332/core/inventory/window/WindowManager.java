package elec332.core.inventory.window;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import elec332.core.ElecCore;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.util.FMLHelper;
import elec332.core.world.WorldHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    public static final ThreadLocal<PlayerEntity> currentOpeningPlayer = new ThreadLocal<>();

    private static final ResourceLocation TILE_WINDOW_HANDLER = new ResourceLocation(ElecCore.MODID, "tile");

    public void register(IWindowHandler windowHandler) {
        ModContainer mc = FMLHelper.getActiveModContainer();
        if (mc == null || mc == FMLHelper.getMinecraftModContainer()) {
            throw new RuntimeException();
        }
        register(windowHandler, new ResourceLocation(FMLHelper.getActiveModContainer().getModId(), windowHandler.getClass().getCanonicalName()));
    }

    public void register(IWindowHandler windowHandler, ResourceLocation name) {
        if (lookup.containsKey(name)) {
            throw new IllegalArgumentException("Name already in use: " + name);
        }
        lookup.put(name, windowHandler);
    }

    public IWindowHandler get(ResourceLocation name) {
        return lookup.get(name);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openClientWindow(Window window) {
        Minecraft.getInstance().displayGuiScreen(new WindowGui(-1, ElecCore.proxy.getClientPlayer(), window));
    }

    public static void openTileWindow(@Nonnull PlayerEntity player, BlockPos pos) {
        openWindow(player, INSTANCE.get(TILE_WINDOW_HANDLER), bb -> bb.writeBlockPos(pos));
    }

    public static void openWindow(@Nonnull PlayerEntity player, IWindowHandler windowHandler, World world, Consumer<ElecByteBuf> dataSupplier) {
        ElecByteBuf data = ElecByteBuf.of(Unpooled.buffer());
        dataSupplier.accept(data);
        openWindow(player, windowHandler, world, data);
    }

    public static void openWindow(@Nonnull PlayerEntity player, IWindowHandler windowHandler, Consumer<ElecByteBuf> dataSupplier) {
        ElecByteBuf data = ElecByteBuf.of(Unpooled.buffer());
        dataSupplier.accept(data);
        openWindow(player, windowHandler, data);
    }

    public static void openWindow(@Nonnull PlayerEntity player, IWindowHandler windowHandler, ElecByteBuf data) {
        openWindow(player, windowHandler, player.getEntityWorld(), data);
    }

    public static void openWindow(@Nonnull PlayerEntity player, IWindowHandler windowHandler, World world, ElecByteBuf data) {
        if (player instanceof ServerPlayerEntity) {
            ResourceLocation name = INSTANCE.lookup.inverse().get(windowHandler);
            if (name == null) {
                throw new IllegalArgumentException();
            }
            ElecByteBuf cdata = ElecByteBuf.of(Unpooled.wrappedBuffer(data));
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {

                @Nonnull
                @Override
                public ITextComponent getDisplayName() {
                    return new StringTextComponent("window");
                }

                @Nonnull
                @Override
                public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
                    return INSTANCE.getServerGuiElement(i, playerEntity, world, cdata, windowHandler);
                }

            }, allData -> {
                allData.writeResourceLocation(name);
                allData.writeVarInt(data.readableBytes());
                allData.writeBytes(data);
            });
            return;
        }
        throw new IllegalArgumentException();
    }

    public synchronized WindowContainer getServerGuiElement(int containerId, PlayerEntity player, World world, ElecByteBuf data, IWindowHandler windowHandler) {
        currentOpeningPlayer.set(player);
        WindowContainer ret = new WindowContainer(player, windowHandler.createWindow(player, world, data), containerId);
        currentOpeningPlayer.remove();
        return ret;
    }

    @Nullable
    public static Window getOpenWindow(PlayerEntity player) {
        Container container = player.openContainer;
        if (container instanceof WindowContainer) {
            return ((WindowContainer) container).getWindow();
        }
        return null;
    }

    @Nullable
    public static Window getOpenWindow(PlayerEntity player, int windowID) {
        Window window = getOpenWindow(player);
        if (window != null && window.getWindowID() == windowID) {
            return window;
        }
        return null;
    }

    static {
        INSTANCE.register((player, world, data) -> {
            TileEntity tile = WorldHelper.getTileAt(world, data.readBlockPos());
            if (tile instanceof IWindowFactory) {
                return ((IWindowFactory) tile).createWindow();
            }
            return null;
        }, TILE_WINDOW_HANDLER);
    }

}
