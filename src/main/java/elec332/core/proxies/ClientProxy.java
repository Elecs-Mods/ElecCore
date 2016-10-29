package elec332.core.proxies;

import elec332.core.api.client.IColoredBlock;
import elec332.core.api.client.IColoredItem;
import elec332.core.client.model.ModelEventHandler;
import elec332.core.client.model.loading.handler.ElecModelHandler;
import elec332.core.client.model.replace.ElecTileEntityItemStackRenderer;
import elec332.core.main.ElecCore;
import elec332.core.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332.
 */
public class ClientProxy extends CommonProxy {

	public ClientProxy(){
		this.minecraft = Minecraft.getMinecraft();
	}

	private final Minecraft minecraft;
	private static final IItemColor COLORED_ITEM, COLORED_ITEMBLOCK;
	private static final IBlockColor COLORED_BLOCK;

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
		((SimpleReloadableResourceManager) resourceManager).registerReloadListener(new ModelReloadListener());
		MinecraftForge.EVENT_BUS.register(new ModelEventHandler());
	}

	@Override
	public void postInitRendering() {
		for (Item item : RegistryHelper.getItemRegistry()){
			if (item instanceof IColoredItem){
				minecraft.itemColors.registerItemColorHandler(COLORED_ITEM, item);
			}
			if (item instanceof ItemBlock){
				Block block = ((ItemBlock) item).getBlock();
				if (block instanceof IColoredItem){
					minecraft.itemColors.registerItemColorHandler(COLORED_ITEM, block);
				} else if (block instanceof IColoredBlock){
					minecraft.itemColors.registerItemColorHandler(COLORED_ITEMBLOCK, item);
				}
			}
		}
		for (Block block : RegistryHelper.getBlockRegistry()){
			if (block instanceof IColoredBlock) {
				minecraft.blockColors.registerBlockColorHandler(COLORED_BLOCK, block);
			}
		}
		TileEntityItemStackRenderer.instance = new ElecTileEntityItemStackRenderer();
	}

	@Override
	public void addPersonalMessageToPlayer(String s) {
		minecraft.thePlayer.addChatComponentMessage(new TextComponentString(s));
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	private class ModelReloadListener implements IResourceManagerReloadListener {

		@Override
		public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
			if (minecraft.renderItem != null) {
				ElecModelHandler.registerModels();
			}
		}
	}

	static {

		COLORED_ITEM = new IItemColor() {

			@Override
			public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
				return ((IColoredItem)stack.getItem()).getColorFromItemStack(stack, tintIndex);
			}

		};

		COLORED_ITEMBLOCK = new IItemColor() {

			@Override
			public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
				Block block = ((ItemBlock) stack.getItem()).getBlock();
				return ((IColoredBlock) block).colorMultiplier(block.getStateFromMeta(stack.getItemDamage()), null, null, tintIndex);
			}

		};

		COLORED_BLOCK = new IBlockColor() {

			@Override
			public int colorMultiplier(@Nonnull IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
				return ((IColoredBlock) state.getBlock()).colorMultiplier(state, worldIn, pos, tintIndex);
			}

		};

	}

}
