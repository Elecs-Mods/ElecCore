package elec332.core.effects.api.ability;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;

import java.awt.*;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class Ability {

    public Ability(String name){
        this.name = name;
        this.maxLevel = Integer.MAX_VALUE;
        this.renderColor = Color.WHITE;
        this.type = Type.NEUTRAL;
        this.components = new Ability[0];
    }

    private final String name;
    private int maxLevel;
    private Color renderColor;
    private Type type;
    private Ability[] components;

    public final Ability setMaxLevel(int maxLevel){
        this.maxLevel = maxLevel;
        return this;
    }

    public final Ability setRenderColor(Color renderColor) {
        this.renderColor = renderColor;
        return this;
    }

    public final Ability setType(Type type) {
        this.type = type;
        return this;
    }

    public final Ability setComponents(Ability[] components) {
        this.components = components;
        return this;
    }

    public boolean isInstant(){
        return false;
    }

    public void onEffectAddedToEntity(EntityLivingBase entity, WrappedAbility activeEffect){
    }

    public void updateEffectOnEntity(EntityLivingBase entity, WrappedAbility activeEffect){
    }

    public void onEffectRemovedFromEntity(EntityLivingBase entity, WrappedAbility activeEffect){
    }

    public final String getName() {
        return name;
    }

    public String getLocalisedName(){
        return StatCollector.translateToLocal("ability."+name);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public Color getRenderColor() {
        return renderColor;
    }

    public enum Type{
        GOOD, NEUTRAL, BAD
    }
}
