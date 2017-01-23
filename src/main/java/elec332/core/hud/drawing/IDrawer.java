package elec332.core.hud.drawing;

import elec332.core.hud.position.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 8-1-2017.
 */
public interface IDrawer<D> {

    @SideOnly(Side.CLIENT)
    public int draw(D drawable, Minecraft mc, Alignment alignment, int x, int y, Object... data);

}
