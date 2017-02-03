package elec332.core.hud.armor;

import elec332.core.api.module.ElecModule;
import elec332.core.hud.DamageDisplayType;
import elec332.core.hud.IDamageDisplayType;
import elec332.core.hud.drawing.ItemStackDrawer;
import elec332.core.hud.position.Alignment;
import elec332.core.hud.position.HorizontalStartingPoint;
import elec332.core.hud.position.IStartingPoint;
import elec332.core.hud.position.VerticalStartingPoint;
import elec332.core.main.ElecCore;
import elec332.core.util.ItemStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 8-1-2017.
 */
@SideOnly(Side.CLIENT)
@ElecModule(owner = ElecCore.MODID, name = "hud")
public class ArmorHud {

    public ArmorHud(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static final int hudHeight = 117;

    @SubscribeEvent
    public void render(TickEvent.RenderTickEvent event){

        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        if (player != null) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution res = new ScaledResolution(mc);

            IDamageDisplayType damageDisplayType = DamageDisplayType.USES_LEFT;
            Alignment alignment = Alignment.LEFT;
            IStartingPoint x = HorizontalStartingPoint.LEFT;
            IStartingPoint y = VerticalStartingPoint.BOTTOM;

            int startX = x.getStartingPoint(mc, res, hudHeight);
            int startY = y.getStartingPoint(mc, res, hudHeight);

            int h = startY + 81;
            for (EntityEquipmentSlot eeqs : EntityEquipmentSlot.values()){
                ItemStack stack = player.getItemStackFromSlot(eeqs);
                if (ItemStackHelper.isStackValid(stack)) {
                    int h2 = h;
                    if (eeqs.getSlotType() == EntityEquipmentSlot.Type.HAND) {
                        h2 += 18 * eeqs.ordinal();
                    } else {
                        h2 += 9 - 18 * eeqs.ordinal();
                    }
                    String s = null;
                    if (stack.isItemStackDamageable()){
                        s = damageDisplayType.getDamageForDisplay(stack);
                    }
                    alignment.renderHudPart(ItemStackDrawer.INSTANCE, stack, s, startX, h2);
                }
            }

        }
    }

}
