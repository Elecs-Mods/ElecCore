package elec332.core.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 14-7-2020
 */
public class SimpleArmorMaterial implements IArmorMaterial {

    public SimpleArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmounts, int enchantability, SoundEvent equipSound, float toughness, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmounts;
        this.enchantability = enchantability;
        this.soundEvent = equipSound;
        this.toughness = toughness;
        this.repairMaterial = new LazyValue<>(repairMaterial);
    }

    private static final int[] DAMAGE_REFERENCE = new int[]{13, 15, 16, 11};

    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final LazyValue<Ingredient> repairMaterial;

    @Override
    public int getDurability(EquipmentSlotType slotIn) {
        return DAMAGE_REFERENCE[slotIn.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
        return this.damageReductionAmountArray[slotIn.getIndex()];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Nonnull
    @Override
    public SoundEvent getSoundEvent() {
        return this.soundEvent;
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return this.repairMaterial.getValue();
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

}
