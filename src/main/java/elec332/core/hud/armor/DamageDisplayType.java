package elec332.core.hud;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

/**
 * Created by Elec332 on 8-1-2017.
 */
public enum DamageDisplayType implements IDamageDisplayType {

    DAMAGE {

        @Override
        public String getDamageForDisplay(@Nonnull ItemStack stack) {
             return EMPTY + stack.getItemDamage();
        }

    },
    USES_LEFT {

        @Override
        public String getDamageForDisplay(@Nonnull ItemStack stack) {
            return EMPTY + (stack.getMaxDamage() - stack.getItemDamage());
        }

    },
    PERCENT {

        private final DecimalFormat format = new DecimalFormat("###.#");

        @Override
        public String getDamageForDisplay(@Nonnull ItemStack stack) {
            return format.format((stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage() * 100) + "%";
        }

    },
    NONE {

        @Override
        public String getDamageForDisplay(@Nonnull ItemStack stack) {
            return null;
        }

    };

    private static final String EMPTY = "";

}
