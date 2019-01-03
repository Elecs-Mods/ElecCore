package elec332.core.handler.event;

import elec332.core.api.util.IRightClickCancel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Created by Elec332 on 13-8-2018.
 */
public class PlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SuppressWarnings("all")
    public void onItemRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack;
        if (event.getHand() == EnumHand.OFF_HAND) {
            stack = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
            if (stack != null && stack.getItem() instanceof IRightClickCancel && ((IRightClickCancel) stack.getItem()).cancelInteraction(stack)) {
                event.setCanceled(true);
                return;
            }
        }
        stack = event.getItemStack();
        if (stack != null && stack.getItem() instanceof IRightClickCancel && ((IRightClickCancel) stack.getItem()).cancelInteraction(stack)) {
            event.setCanceled(true);
            ItemUseContext iuc = new ItemUseContext(event.getEntityPlayer(), event.getItemStack(), event.getPos(), event.getFace(), (float) event.getHitVec().x, (float) event.getHitVec().y, (float) event.getHitVec().z);
            stack.getItem().onItemUse(iuc);
        }
    }

}
