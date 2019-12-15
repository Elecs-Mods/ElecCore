package elec332.core.proxies;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.util.function.Consumer;

/**
 * Created by Elec332.
 */
public class CommonProxy {

    public boolean isClient() {
        return false;
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

    private static MinecraftServer server = null;

    static {
        MinecraftForge.EVENT_BUS.addListener((Consumer<FMLServerAboutToStartEvent>) event -> server = event.getServer());
    }

}
