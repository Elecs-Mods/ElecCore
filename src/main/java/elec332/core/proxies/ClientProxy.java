package elec332.core.proxies;

import elec332.core.asm.asmload.ASMHooks;
import elec332.core.client.model.ElecResourceManager;
import elec332.core.client.model.INoJsonBlock;
import elec332.core.client.model.INoJsonItem;
import elec332.core.java.ReflectionHelper;
import elec332.core.main.ElecCore;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.IdentityHashMap;

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

	@SuppressWarnings("unchecked")
	private class RenderReplacer implements ElecResourceManager.IResourceHook {

		private boolean ri = false;

		@Override
		public boolean onRegister(IReloadableResourceManager resourceManager, final IResourceManagerReloadListener listener) {
			if (listener.getClass() == RenderItem.class && !ri){
				//ASMHooks.Client.initItemRender();
				resourceManager.registerReloadListener(new IResourceManagerReloadListener() {
					@Override
					public void onResourceManagerReload(IResourceManager resourceManager) {
						IdentityHashMap<Item, TIntObjectHashMap<IBakedModel>> models = null;
						try {
							models = (IdentityHashMap<Item, TIntObjectHashMap<IBakedModel>>) ReflectionHelper.makeFieldAccessible(ItemModelMesherForge.class.getDeclaredField("models")).get(((RenderItem) listener).itemModelMesher);
						} catch (Exception e){
							e.printStackTrace();
						}
						for (Item item : GameData.getItemRegistry().typeSafeIterable()){
							boolean i = item instanceof INoJsonItem;
							if ((i || ((item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonBlock))) && models != null){
								models.put(item, new InternalItemMap());
								((RenderItem)listener).itemModelMesher.register(item, 0, new ModelResourceLocation(item.delegate.getResourceName().toString(), i ? "inventory" : "normal"));
							}
						}
						listener.onResourceManagerReload(resourceManager);
					}
				});
				return false;
			} else if (listener.getClass() == BlockRendererDispatcher.class){
				if (ElecCore.oldBlocks) {
					minecraft.blockRenderDispatcher = ASMHooks.Client.newBlockRendererDispatcher();
					resourceManager.registerReloadListener(minecraft.blockRenderDispatcher);
					return false;
				}
				return true;
			} else if (listener.getClass() == ModelManager.class){
				resourceManager.registerReloadListener(new IResourceManagerReloadListener() {
					@Override
					public void onResourceManagerReload(IResourceManager resourceManager) {
						//Set<Block> ignoredBlocks = minecraft.modelManager.getBlockModelShapes().getBlockStateMapper().setBuiltInBlocks;
						for (Block block : GameData.getBlockRegistry().typeSafeIterable()){
							if (block instanceof INoJsonBlock){
								//ignoredBlocks.add(block);
								minecraft.modelManager.modelProvider.getBlockStateMapper().registerBlockStateMapper(block, new StateMapperBase() {
									@Override
									protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
										return new ModelResourceLocation(state.getBlock().delegate.getResourceName().toString());
									}
								});
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

	private class InternalItemMap extends TIntObjectHashMap<IBakedModel> {

		public InternalItemMap(){
			super();
		}

		@Override
		public IBakedModel put(int key, IBakedModel value) {
			return super.put(0, value);
		}

		@Override
		public IBakedModel get(int key) {
			return super.get(0);
		}

	}

}
