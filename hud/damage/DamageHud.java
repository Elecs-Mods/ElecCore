package elec332.core.hud.damage;

import elec332.core.api.module.ElecModule;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.hud.AbstractHud;
import elec332.core.hud.drawing.EntityDrawer;
import elec332.core.hud.drawing.IDrawer;
import elec332.core.hud.position.Alignment;
import elec332.core.hud.position.HorizontalStartingPoint;
import elec332.core.hud.position.VerticalStartingPoint;
import elec332.core.main.ElecCore;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Elec332 on 13-1-2017.
 */
@ElecModule(owner = ElecCore.MODID, name = "damage")
public class DamageHud extends AbstractHud {

    public DamageHud() {
        super(Alignment.LEFT, HorizontalStartingPoint.LEFT, VerticalStartingPoint.TOP);
    }

    @Nullable
    private EntityLivingBase entity;
    private int noSelectTime, noSelTimeConf = 200, deathShowConf = 20;

    @Override
    public int getHudHeight() {
        return 50;
    }

    @Override
    protected void configure(@Nonnull Configuration config) {
        this.noSelTimeConf = config.getInt("noSelectTime", Configuration.CATEGORY_GENERAL, noSelTimeConf, 0, 1000, "The time to show the mob-hud whilst not hovering over it anymore. (In ticks, 1/20th of a second)");
        this.deathShowConf = config.getInt("timeShowDeath", Configuration.CATEGORY_GENERAL, deathShowConf, 0, 1000, "The time to show the mob-hud of an entity whilst it's dead. (In ticks, 1/20th of a second)");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event){
        Entity entity = GuiDraw.mc.renderManager.pointedEntity;
        if (this.entity != null && this.entity.isDead && this.noSelectTime < noSelTimeConf - deathShowConf){
            this.noSelectTime = noSelTimeConf - deathShowConf;
        }
        if (entity != null && entity instanceof EntityLivingBase){
            if (this.entity == null || this.entity.getEntityId() != entity.getEntityId()){
                this.entity = (EntityLivingBase) entity;
                this.noSelectTime = 0;
            }
        } else if (this.entity != null){
            noSelectTime++;
            if (noSelectTime >= noSelTimeConf){
                this.entity = null;
                this.noSelectTime = 0;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHud(@Nonnull EntityPlayerSP player, @Nonnull World world, @Nonnull Alignment alignment, int startX, int startY, float partialTicks) {
        if (entity != null) {

            RenderHelper.bindTexture(hud);
            GlStateManager.color(1, 1, 1, 1);

            GuiDraw.drawScaledCustomSizeModalRect(startX, startY, 0, 0, 120, 120, 40, 40, 350, 128);
            GuiDraw.drawScaledCustomSizeModalRect(startX + 43, startY, 120, 0, 240, 75, 80, 25, 350, 128);

            int s = 125, e = 345;
            float scale = entity.getHealth() / entity.getMaxHealth();
            GuiDraw.drawScaledCustomSizeModalRect(startX + 43 + 1, startY + 36 / 3, s, 76, (int) ((e - s) * scale), 37, (int) (((e - s + 2) * scale) / 3), 32 / 3, 350, 128);

            GlStateManager.pushMatrix();
            GlStateManager.translate(startX + 82, startY + 4, 0);
            GlStateManager.scale(0.65f, 0.65f, 0.65f);
            FontRenderer font = RenderHelper.getMCFontrenderer();
            String txt = entity.getName();
            if (entity.isChild() && !entity.hasCustomName()){
                txt = "Baby " + txt;
            }
            font.drawString(txt, -(font.getStringWidth(txt) / 2), 0, Color.WHITE.getRGB());
            txt = (int) entity.getHealth() + "/" + (int) entity.getMaxHealth();
            font.drawString(txt, -(font.getStringWidth(txt) / 2), 17, Color.WHITE.getRGB());
            GlStateManager.popMatrix();

            float entS = 1;
            if (entity.height > 2) {
                entS = entity.height / 2;
            }
            if (entity.width > 1.5f) {
                entS = Math.max(entS, entity.width / 1.5f);
            }

            int i = 15728880;
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
            GlStateManager.color(1, 1, 1, 1);
            alignment.renderHudPart(hudEntityDrawer, entity, startX, startY, (player.rotationYaw) + 180, entS);
        }
    }

    private static final IDrawer<Entity> hudEntityDrawer;
    private static final ResourceLocation hud = new ResourceLocation(ElecCore.MODID, "mobhudbackbgound.png");

    static {
        hudEntityDrawer = new EntityDrawer(25, 33, 15);
    }

}
