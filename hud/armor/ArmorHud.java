package elec332.core.hud.armor;

import elec332.core.api.module.ElecModule;
import elec332.core.hud.AbstractHud;
import elec332.core.hud.position.Alignment;
import elec332.core.hud.position.HorizontalStartingPoint;
import elec332.core.hud.position.VerticalStartingPoint;
import elec332.core.hud.drawing.ItemStackDrawer;
import elec332.core.main.ElecCore;
import elec332.core.util.ItemStackHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 8-1-2017.
 */
@SideOnly(Side.CLIENT)
@ElecModule(owner = ElecCore.MODID, name = "hud")
public class ArmorHud extends AbstractHud {

    public ArmorHud() {
        super(Alignment.LEFT, HorizontalStartingPoint.LEFT, VerticalStartingPoint.MIDDLE);
    }

    private IDamageDisplayType displayType = DamageDisplayType.USES_LEFT;

    @Override
    protected void configure(@Nonnull Configuration config) {
        displayType = DamageDisplayType.valueOf(config.getString("displayType", Configuration.CATEGORY_CLIENT, displayType.toString(), "This defines the way that the tool/armor damage will be displayed.", displayTypes));
    }

    @Override
    public int getHudHeight() {
        return 117;
    }

    @Override
    public void renderHud(@Nonnull EntityPlayerSP player, @Nonnull World world, @Nonnull Alignment alignment, int startX, int startY, float partialTicks) {
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
                    s = displayType.getDamageForDisplay(stack);
                }
                alignment.renderHudPart(ItemStackDrawer.INSTANCE, stack, s, startX, h2);
            }
        }
    }

    private static final String[] displayTypes;

    static {
        displayTypes = new String[DamageDisplayType.values().length];
        for (int i = 0; i < displayTypes.length; i++) {
            displayTypes[i] = DamageDisplayType.values()[i].toString();
        }
    }

}
