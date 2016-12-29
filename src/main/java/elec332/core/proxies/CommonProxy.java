package elec332.core.proxies;

import elec332.core.inventory.window.WindowContainer;
import elec332.core.inventory.window.WindowManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by Elec332.
 */
public class CommonProxy implements IGuiHandler {

	public boolean isClient() {
		return false;
	}

	public void preInitRendering(){
	}

	public void postInitRendering(){
	}

	public void addPersonalMessageToPlayer(String s){
	}


	public World getClientWorld(){
		return null;
	}

	public EntityPlayer getClientPlayer(){
		return null;
	}

	@Override
	public synchronized WindowContainer getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		currentOpeningPlayer.set(player);
		WindowContainer ret = new WindowContainer(player, WindowManager.INSTANCE.get(ID & 0xFF).createWindow((byte) (ID >> 8), player, world, x, y, z));
		currentOpeningPlayer.remove();
		return ret;
	}

	@Override
	public synchronized Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		throw new RuntimeException();
	}

	public static final ThreadLocal<EntityPlayer> currentOpeningPlayer;

	static {
		currentOpeningPlayer = new ThreadLocal<EntityPlayer>();
	}

}
