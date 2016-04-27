package elec332.core.proxies;

import elec332.core.client.model.ElecResourceManager;
import elec332.core.client.newstuff.ElecModelHandler;
import elec332.core.main.ElecCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Elec332.
 */
public class ClientProxy extends CommonProxy {

	public ClientProxy(){
		this.minecraft = Minecraft.getMinecraft();
	}

	private final Minecraft minecraft;

	public boolean isClient() {
		return true;
	}

	@Override
	public void preInitRendering() {
		IResourceManager resourceManager = minecraft.getResourceManager();
		if (!(resourceManager instanceof SimpleReloadableResourceManager)){
			ElecCore.logger.error("Someone replaced the resource manager, but it doesn't extend SimpleResourceManager!");
			ElecCore.logger.error("This is an severe error, forge will crash further down the line, exiting minecraft now!");
			ElecCore.logger.error("Source: " + resourceManager.getClass().getCanonicalName());
			throw new RuntimeException("Class: " + resourceManager.getClass().getCanonicalName() + " is not a valid replacement for the vanilla resource manager.");
		}
		ElecResourceManager newResourceManager = new ElecResourceManager((SimpleReloadableResourceManager) resourceManager);
		newResourceManager.addListenHook(new RenderReplacer());
		minecraft.mcResourceManager = newResourceManager;
		MinecraftForge.EVENT_BUS.register(new elec332.core.client.model.EventHandler());
	}

	@Override
	public void addPersonalMessageToPlayer(String s) {
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentString(s));
	}

	@SuppressWarnings("unchecked")
	private class RenderReplacer implements ElecResourceManager.IResourceHook {

		@Override
		public boolean onRegister(IReloadableResourceManager resourceManager, final IResourceManagerReloadListener listener) {
			/*if (listener.getClass() == RenderItem.class){
				/*resourceManager.registerReloadListener(new IResourceManagerReloadListener() {
					@Override
					public void onResourceManagerReload(IResourceManager resourceManager) {
						//ElecModelHandler.registerItemModels((RenderItem) listener);
						listener.onResourceManagerReload(resourceManager);
					}
				});
				return false;
			} /*else if (listener.getClass() == BlockRendererDispatcher.class){
				if (ElecCore.oldBlocks) {
					minecraft.blockRenderDispatcher = ASMHooks.Client.newBlockRendererDispatcher();
					resourceManager.registerReloadListener(minecraft.blockRenderDispatcher);
					return false;
				}
				return true;
			} else*/ if (listener.getClass() == ModelManager.class){
				resourceManager.registerReloadListener(new IResourceManagerReloadListener() {
					@Override
					public void onResourceManagerReload(IResourceManager resourceManager) {
						ElecModelHandler.registerBlockModels((ModelManager)listener);
						ElecModelHandler.registerItemModels(minecraft.renderItem);
						ElecModelHandler.registerMultiPartModels();
					}
				});
				return true;
			}
			return true;
		}

	}

}
