package elec332.core.proxies;

import elec332.core.ElecCore;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.inventory.window.WindowContainer;
import elec332.core.inventory.window.WindowManager;
import elec332.core.util.RegistryHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.network.IContainerFactory;

import java.util.function.Consumer;

/**
 * Created by Elec332.
 */
public class CommonProxy {

    public static final ContainerType<WindowContainer> WINDOW_CONTAINER_TYPE;
    private static final ResourceLocation GUI_NAME = new ResourceLocation(ElecCore.MODID, "window_gui");

    public boolean isClient() {
        return false;
    }

    @SubscribeEvent
    public void registerWindow(RegistryEvent.Register<ContainerType<?>> event){
        RegistryHelper.getContainers().register(WINDOW_CONTAINER_TYPE.setRegistryName(GUI_NAME));
    }

    public void preInitRendering() {
    }

    public void postInitRendering() {
    }

    public void addPersonalMessageToPlayer(String s) {
    }

    public World getClientWorld() {
        return null;
    }

    public PlayerEntity getClientPlayer() {
        return null;
    }

    public MinecraftServer getServer() {
        return server;
    }

    /*public WindowContainer getServerGuiElement(int windowId, PlayerEntity player, World world, PacketBuffer data) {
        ResourceLocation name = data.readResourceLocation();
        return WindowManager.INSTANCE.getServerGuiElement(player, world, ElecByteBuf.of(Unpooled.wrappedBuffer(data.readByteArray())), WindowManager.INSTANCE.get(name));
    }*/

    private static MinecraftServer server = null;

    static {
        MinecraftForge.EVENT_BUS.addListener((Consumer<FMLServerAboutToStartEvent>) event -> server = event.getServer());
        WINDOW_CONTAINER_TYPE = new ContainerType<>((IContainerFactory<WindowContainer>) (windowId, inv, data) -> {
            ResourceLocation name = data.readResourceLocation();
            return WindowManager.INSTANCE.getServerGuiElement(windowId, inv.player, inv.player.world, ElecByteBuf.of(Unpooled.wrappedBuffer(data.readByteArray())), WindowManager.INSTANCE.get(name));
        });
    }

}
