package elec332.core.compat.forestry.allele;

import elec332.core.compat.forestry.EffectData;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Created by Elec332 on 25-8-2016.
 */
public class AlleleEffectPotion extends AlleleEffectThrottled {

    public AlleleEffectPotion(ResourceLocation rl, PotionEffect baseEffect) {
        super(rl);
        setThrottle(200);
        this.baseEffect = baseEffect;
    }

    public AlleleEffectPotion(String s, PotionEffect baseEffect) {
        super(s);
        setThrottle(200);
        this.baseEffect = baseEffect;
    }

    public AlleleEffectPotion(String uid, String unlocalizedName, PotionEffect baseEffect) {
        super(uid, unlocalizedName);
        setThrottle(200);
        this.baseEffect = baseEffect;
    }

    public AlleleEffectPotion setBypassesArmour(){
        this.bypassesArmour = true;
        return this;
    }

    private final PotionEffect baseEffect;
    private boolean bypassesArmour;

    @Override
    public IEffectData validateStorage(IEffectData storedData) {
        if (storedData == null){
            storedData = new EffectData(1, 0, 0);
        }
        return storedData;
    }

    @Override
    public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
        List<EntityLivingBase> entities = getEntitiesInRange(genome, housing, EntityLivingBase.class);
        for (EntityLivingBase entityLivingBase : entities){
            if (bypassesArmour){
                entityLivingBase.addPotionEffect(new PotionEffect(baseEffect));
            } else {
                int armour = BeeManager.armorApiaristHelper.wearsItems(entityLivingBase, getUID(), true);
                PotionEffect effect = new PotionEffect(baseEffect.getPotion(), baseEffect.getDuration() / 60 * armour, baseEffect.getAmplifier(), false, baseEffect.doesShowParticles());
                effect.setCurativeItems(baseEffect.getCurativeItems());
                entityLivingBase.addPotionEffect(effect);
            }
        }
        return storedData;
    }

}
