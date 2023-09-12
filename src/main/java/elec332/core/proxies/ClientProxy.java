package elec332.core.proxies;

import elec332.core.api.client.IColoredBlock;
import elec332.core.api.client.IColoredItem;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.ClientEventHandler;
import elec332.core.inventory.window.WindowContainer;
import elec332.core.inventory.window.WindowGui;
import elec332.core.util.RegistryHelper;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Elec332.
 */
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        this.minecraft = Minecraft.getInstance();
    }

    private final Minecraft minecraft;
    private static final ItemColor COLORED_ITEM, COLORED_BlockItem;
    private static final BlockColor COLORED_BLOCK;

    public boolean isClient() {
        return true;
    }

    @Override
    public void preInitRendering() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        ScreenManager.IScreenFactory<WindowContainer, WindowGui> factory = (container, playerInventory, txt) -> new WindowGui(container); //Java bug, have to cast otherwise...
        ScreenManager.registerFactory(WINDOW_CONTAINER_TYPE, factory);
    }

    @Override
    public void postInitRendering() {
        for (Item item : RegistryHelper.getItemRegistry()) {
            if (item instanceof IColoredItem) {
                RenderHelper.getItemColors().register(COLORED_ITEM, item);
            }
            if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block instanceof IColoredItem) {
                    RenderHelper.getItemColors().register(COLORED_ITEM, block);
                } else if (block instanceof IColoredBlock) {
                    RenderHelper.getItemColors().register(COLORED_BlockItem, item);
                }
            }
        }
        for (Block block : RegistryHelper.getBlockRegistry()) {
            if (block instanceof IColoredBlock) {
                RenderHelper.getBlockColors().register(COLORED_BLOCK, block);
            }
        }
    }

    @Override
    public void addPersonalMessageToPlayer(String s) {
        getClientPlayer().displayClientMessage(new TextComponent(s), false);
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    static {

        COLORED_ITEM = (stack, tintIndex) -> ((IColoredItem) stack.getItem()).getColorFromItemStack(stack, tintIndex);

        COLORED_BlockItem = (stack, tintIndex) -> {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            return ((IColoredBlock) block).colorMultiplier(block.defaultBlockState(), null, null, tintIndex);
        };

        COLORED_BLOCK = (state, worldIn, pos, tintIndex) -> ((IColoredBlock) state.getBlock()).colorMultiplier(state, worldIn, pos, tintIndex);

    }

}
