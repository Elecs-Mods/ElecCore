package elec332.core.hud;

import com.google.common.base.Strings;
import elec332.core.ElecCore;
import elec332.core.api.config.IConfigurableElement;
import elec332.core.hud.position.Alignment;
import elec332.core.hud.position.HorizontalStartingPoint;
import elec332.core.hud.position.IStartingPoint;
import elec332.core.hud.position.VerticalStartingPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 13-1-2017.
 */
public abstract class AbstractHud implements IConfigurableElement {

    public AbstractHud(@Nonnull Alignment alignment, @Nonnull IStartingPoint horizontal, @Nonnull IStartingPoint vertical){
        MinecraftForge.EVENT_BUS.register(this);
        this.alignment = alignment;
        this.horiz = horizontal;
        this.ver = vertical;
        this.category = Configuration.CATEGORY_CLIENT;
    }

    public AbstractHud setConfigCategory(String s){
        if (Strings.isNullOrEmpty(s)){
            throw new IllegalArgumentException();
        }
        if (configuredOnce){
            throw new IllegalStateException();
        }
        this.category = s;
        return this;
    }

    private String category;
    private boolean configuredOnce;
    private static final String[] a, h, v;
    private Alignment alignment = Alignment.LEFT;
    private IStartingPoint horiz = HorizontalStartingPoint.LEFT, ver = VerticalStartingPoint.MIDDLE;

    @Override
    public void reconfigure(Configuration config) {
        if (config != null) {
            if (!configuredOnce) {
                configuredOnce = true;
            }
            this.alignment = Alignment.valueOf(config.getString("alignment", category, alignment.toString(), "The alignment for this hud.", a));
            if (!(horiz instanceof HorizontalStartingPoint && ver instanceof VerticalStartingPoint)){
                configureCustom(config, horiz, ver);
            } else {
                horiz = HorizontalStartingPoint.valueOf(config.getString("horizontalPosition", category, horiz.toString(), "The horizontal position of this hud.", h));
                ver = VerticalStartingPoint.valueOf(config.getString("verticalPosition", category, ver.toString(), "The vertical position of this hud.", v));
            }
            configure(config);
        }
    }

    public String getConfigCategory() {
        return this.category;
    }

    /**
     * If your HUD uses one or more non-default horizontal or vertical starting points, this method will be called,
     * as the system doesn't know how to configure those.
     *
     * @param config The config
     * @param horizontal The (non-default) horizontal starting point
     * @param vertical The (non-default) vertical starting point
     */
    protected void configureCustom(@Nonnull Configuration config, @Nonnull IStartingPoint horizontal, @Nonnull IStartingPoint vertical){
        throw new UnsupportedOperationException();
    }

    /**
     * Use this internally to configure additional HUD settings
     *
     * @param config The config
     */
    protected abstract void configure(@Nonnull Configuration config);

    @Nonnull
    protected Alignment getAlignment(){
        return alignment;
    }

    @Nonnull
    protected IStartingPoint getHorizontalStartingPoint(){
        return horiz;
    }

    @Nonnull
    protected IStartingPoint getVerticalStartingPoint(){
        return ver;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public final void onRenderTick(TickEvent.RenderTickEvent event) {
        EntityPlayerSP player = (EntityPlayerSP) ElecCore.proxy.getClientPlayer();
        if (player != null && Minecraft.getMinecraft().inGameHasFocus && shouldRenderHud(player, event.renderTickTime, event.phase)) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution res = new ScaledResolution(mc);

            int hudHeight = getHudHeight();
            int startX = getHorizontalStartingPoint().getStartingPoint(mc, res, hudHeight);
            int startY = getVerticalStartingPoint().getStartingPoint(mc, res, hudHeight);

            renderHud(player, ElecCore.proxy.getClientWorld(), getAlignment(), startX, startY, event.renderTickTime);

        }
    }

    protected boolean shouldRenderHud(@Nonnull EntityPlayerSP player, float partialTicks, TickEvent.Phase phase){
        return phase == TickEvent.Phase.END;
    }

    public abstract int getHudHeight();

    @SideOnly(Side.CLIENT)
    public abstract void renderHud(@Nonnull EntityPlayerSP player, @Nonnull World world, @Nonnull Alignment alignment, int startX, int startY, float partialTicks);

    static {
        a = new String[Alignment.values().length];
        for (int i = 0; i < a.length; i++) {
            a[i] = Alignment.values()[i].toString();
        }
        h = new String[HorizontalStartingPoint.values().length];
        for (int i = 0; i < h.length; i++) {
            h[i] = HorizontalStartingPoint.values()[i].toString();
        }
        v = new String[VerticalStartingPoint.values().length];
        for (int i = 0; i < v.length; i++) {
            v[i] = VerticalStartingPoint.values()[i].toString();
        }
    }

}
