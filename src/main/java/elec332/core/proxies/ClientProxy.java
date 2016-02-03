package elec332.core.proxies;

import elec332.core.asm.asmload.ASMHooks;
import elec332.core.client.model.ElecResourceManager;
import elec332.core.client.model.INoJsonBlock;
import elec332.core.client.model.replace.ElecModelManager;
import elec332.core.main.ElecCore;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.Set;

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
		if (!(minecraft.mcResourceManager instanceof SimpleReloadableResourceManager)){
			ElecCore.logger.error("Someone replaced the resource manager, but it doesn't extend SimpleResourceManager!");
			ElecCore.logger.error("This is an severe error, forge will crash further down the line, exiting minecraft now!");
			ElecCore.logger.error("Source: "+minecraft.mcResourceManager.getClass().getCanonicalName());
			throw new RuntimeException("Class: "+minecraft.mcResourceManager.getClass().getCanonicalName()+" is not a valid replacement for the vanilla resource manager.");
		}
		ElecResourceManager newResourceManager = new ElecResourceManager((SimpleReloadableResourceManager) minecraft.mcResourceManager);
		newResourceManager.addListenHook(new RenderReplacer());
		minecraft.mcResourceManager = newResourceManager;
		MinecraftForge.EVENT_BUS.register(new elec332.core.client.model.EventHandler());
	}

	@Override
	public void addPersonalMessageToPlayer(String s) {
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(s));
	}

	private class RenderReplacer implements ElecResourceManager.IResourceHook {

		@Override
		public boolean onRegister(IReloadableResourceManager resourceManager, IResourceManagerReloadListener listener) {
			if (listener.getClass() == RenderItem.class){
				ASMHooks.Client.initItemRender();
				return false;
			} else if (listener.getClass() == BlockRendererDispatcher.class){
				minecraft.blockRenderDispatcher = ASMHooks.Client.newBlockRendererDispatcher();
				resourceManager.registerReloadListener(minecraft.blockRenderDispatcher);
				return false;
			} else if (listener.getClass() == ModelManager.class){
				resourceManager.registerReloadListener(new IResourceManagerReloadListener() {
					@Override
					public void onResourceManagerReload(IResourceManager resourceManager) {
						Set<Block> ignoredBlocks = minecraft.modelManager.getBlockModelShapes().getBlockStateMapper().setBuiltInBlocks;
						for (Block block : GameData.getBlockRegistry().typeSafeIterable()){
							if (block instanceof INoJsonBlock){
								ignoredBlocks.add(block);
							}
						}
					}
				});
				return true;
				/*minecraft.modelManager = new ElecModelManager(minecraft.textureMapBlocks);
				resourceManager.registerReloadListener(minecraft.modelManager);
				return false;*/
			}
			return true;
		}

	}

}
