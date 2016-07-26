package elec332.core.proxies;

import net.minecraft.world.World;

/**
 * Created by Elec332.
 */
public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public void preInitRendering(){
	}

	public void addPersonalMessageToPlayer(String s){
	}


	public World getClientWorld(){
		return null;
	}

}
