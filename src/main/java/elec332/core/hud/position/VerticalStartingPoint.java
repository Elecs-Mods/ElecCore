package elec332.core.hud.position;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

/**
 * Created by Elec332 on 8-1-2017.
 */
public enum VerticalStartingPoint implements IStartingPoint {

    TOP {
        @Override
        public int getStartingPoint(Minecraft mc, MainWindow sr, int hudHeight) {
            return 9;
        }

    },
    MIDDLE {
        @Override
        public int getStartingPoint(Minecraft mc, MainWindow sr, int hudHeight) {
            return sr.getScaledHeight() / 2 - hudHeight / 2;
        }

    },
    BOTTOM {
        @Override
        public int getStartingPoint(Minecraft mc, MainWindow sr, int hudHeight) {
            return sr.getScaledHeight() - hudHeight - 9;
        }

    };

}
