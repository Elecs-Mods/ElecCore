package elec332.core.hud.position;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Created by Elec332 on 8-1-2017.
 */
public enum HorizontalStartingPoint implements IStartingPoint {

    LEFT {
        @Override
        public int getStartingPoint(Minecraft mc, ScaledResolution sr, int hudHeight) {
            return 5;
        }

    },
    RIGHT {
        @Override
        public int getStartingPoint(Minecraft mc, ScaledResolution sr, int hudHeight) {
            return sr.getScaledWidth() - 5;
        }

    };

}
