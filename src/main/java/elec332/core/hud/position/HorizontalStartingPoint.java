package elec332.core.hud.position;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

/**
 * Created by Elec332 on 8-1-2017.
 */
public enum HorizontalStartingPoint implements IStartingPoint {

    LEFT {
        @Override
        public int getStartingPoint(Minecraft mc, MainWindow sr, int hudHeight) {
            return 5;
        }

    },
    RIGHT {
        @Override
        public int getStartingPoint(Minecraft mc, MainWindow sr, int hudHeight) {
            return sr.getScaledWidth() - 5;
        }

    };

}
