package elec332.core.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

/**
 * Created by Elec332.
 */
public class ClientProxy extends CommonProxy {
	public boolean isClient() { return true; }

	@Override
	public void addPersonalMessageToPlayer(String s) {
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(s));
	}
}
