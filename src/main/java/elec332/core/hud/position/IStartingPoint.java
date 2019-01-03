package elec332.core.hud.position;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

/**
 * Created by Elec332 on 8-1-2017.
 */
public interface IStartingPoint {

    public int getStartingPoint(Minecraft mc, MainWindow sr, int hudHeight);

}
