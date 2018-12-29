package elec332.core.hud.position;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Created by Elec332 on 8-1-2017.
 */
public enum VerticalStartingPoint implements IStartingPoint {

    TOP {
        @Override
        public int getStartingPoint(Minecraft mc, ScaledResolution sr, int hudHeight) {
            return 9;
        }

    },
    MIDDLE {
        @Override
        public int getStartingPoint(Minecraft mc, ScaledResolution sr, int hudHeight) {
            return sr.getScaledHeight() / 2 - hudHeight / 2;
        }

    },
    BOTTOM {
        @Override
        public int getStartingPoint(Minecraft mc, ScaledResolution sr, int hudHeight) {
            return sr.getScaledHeight() - hudHeight - 9;
        }

    };

}
