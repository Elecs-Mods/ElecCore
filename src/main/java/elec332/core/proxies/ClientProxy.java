package elec332.core.proxies;

import elec332.core.api.client.IColoredBlock;
import elec332.core.api.client.IColoredItem;
import elec332.core.inventory.window.WindowGui;
import elec332.core.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Created by Elec332.
 */
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
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

    }

    @Override
    public void postInitRendering() {
        for (Item item : RegistryHelper.getItemRegistry()) {
            if (item instanceof IColoredItem) {
                minecraft.itemColors.registerItemColorHandler(COLORED_ITEM, item);
            }
            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof IColoredItem) {
                    minecraft.itemColors.registerItemColorHandler(COLORED_ITEM, block);
                } else if (block instanceof IColoredBlock) {
                    minecraft.itemColors.registerItemColorHandler(COLORED_ITEMBLOCK, item);
                }
            }
        }
        for (Block block : RegistryHelper.getBlockRegistry()) {
            if (block instanceof IColoredBlock) {
                minecraft.blockColors.registerBlockColorHandler(COLORED_BLOCK, block);
            }
        }
    }

    @Override
    public void addPersonalMessageToPlayer(String s) {
        getClientPlayer().sendMessage(new TextComponentString(s));
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public synchronized Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new WindowGui(super.getServerGuiElement(ID, player, world, x, y, z));
    }

    static {

        COLORED_ITEM = (stack, tintIndex) -> ((IColoredItem) stack.getItem()).getColorFromItemStack(stack, tintIndex);

        COLORED_ITEMBLOCK = (stack, tintIndex) -> {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            return ((IColoredBlock) block).colorMultiplier(block.getStateFromMeta(stack.getItemDamage()), null, null, tintIndex);
        };

        COLORED_BLOCK = (state, worldIn, pos, tintIndex) -> ((IColoredBlock) state.getBlock()).colorMultiplier(state, worldIn, pos, tintIndex);

    }

}
