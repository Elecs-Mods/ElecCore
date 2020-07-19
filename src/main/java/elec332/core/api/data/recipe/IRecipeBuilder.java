package elec332.core.api.data.recipe;

import elec332.core.util.RegistryHelper;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 15-7-2020
 */
public interface IRecipeBuilder<T extends IRecipeBuilder<T>> extends IRecipeBuilderBase<T> {

    /**
     * Gets the group of this recipe
     */
    String getGroup();

    /**
     * Gets the result of this recipe
     */
    Item getResult();

    T withTag(CompoundNBT tag);

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    default void build() {
        this.build(RegistryHelper.getItemRegistry().getKey(this.getResult()));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build()} if save is the same as the ID for
     * the result.
     */
    default void build(String save) {
        ResourceLocation resourcelocation = RegistryHelper.getItemRegistry().getKey(this.getResult());
        if ((new ResourceLocation(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(new ResourceLocation(save));
        }
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    void build(ResourceLocation id);

}
