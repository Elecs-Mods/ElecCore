package elec332.core.proxies;

import elec332.core.api.client.IColoredBlock;
import elec332.core.api.client.IColoredItem;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.client.util.ClientEventHandler;
import elec332.core.inventory.window.WindowGui;
import elec332.core.inventory.window.WindowManager;
import elec332.core.util.FMLHelper;
import elec332.core.util.RegistryHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.network.FMLPlayMessages;

/**
 * Created by Elec332.
 */
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        this.minecraft = Minecraft.getInstance();
    }

    private final Minecraft minecraft;
    private static final IItemColor COLORED_ITEM, COLORED_ITEMBLOCK;
    private static final IBlockColor COLORED_BLOCK;

    public boolean isClient() {
        return true;
    }

    @Override
    public void preInitRendering() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        FMLHelper.getActiveModContainer().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> this::openGui);
    }

    @Override
    public void postInitRendering() {
        for (Item item : RegistryHelper.getItemRegistry()) {
            if (item instanceof IColoredItem) {
                minecraft.itemColors.register(COLORED_ITEM, item);
            }
            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof IColoredItem) {
                    minecraft.itemColors.register(COLORED_ITEM, block);
                } else if (block instanceof IColoredBlock) {
                    minecraft.itemColors.register(COLORED_ITEMBLOCK, item);
                }
            }
        }
        for (Block block : RegistryHelper.getBlockRegistry()) {
            if (block instanceof IColoredBlock) {
                minecraft.blockColors.register(COLORED_BLOCK, block);
            }
        }
    }

    @Override
    public void addPersonalMessageToPlayer(String s) {
        getClientPlayer().sendMessage(new TextComponentString(s));
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public GuiScreen openGui(FMLPlayMessages.OpenContainer data_){
        PacketBuffer data = data_.getAdditionalData();
        ResourceLocation name = data.readResourceLocation();
        return new WindowGui(WindowManager.INSTANCE.getServerGuiElement(getClientPlayer(), getClientWorld(), ElecByteBuf.of(Unpooled.wrappedBuffer(data.readByteArray())), WindowManager.INSTANCE.get(name)));
    }

    static {

        COLORED_ITEM = (stack, tintIndex) -> ((IColoredItem) stack.getItem()).getColorFromItemStack(stack, tintIndex);

        COLORED_ITEMBLOCK = (stack, tintIndex) -> {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            return ((IColoredBlock) block).colorMultiplier(block.getDefaultState(), null, null, tintIndex);
        };

        COLORED_BLOCK = (state, worldIn, pos, tintIndex) -> ((IColoredBlock) state.getBlock()).colorMultiplier(state, worldIn, pos, tintIndex);

    }

}
