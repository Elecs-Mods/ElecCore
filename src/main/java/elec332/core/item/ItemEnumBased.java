package elec332.core.item;

import com.google.common.base.Preconditions;
import elec332.core.api.client.IColoredItem;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.loading.INoJsonItem;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 21-8-2016.
 */
public class ItemEnumBased<E extends Enum<E> & IEnumItem> extends AbstractItem implements INoJsonItem, IColoredItem {

    public ItemEnumBased(Class<E> clazz) {
        super(clazz.getEnumConstants()[0].getItemData());
        this.clazz = clazz;
        this.values = clazz.getEnumConstants();
        this.nji = this.values[0] instanceof INoJsonItem;
        this.values[0].initializeItem(this);
    }

    protected final Class<E> clazz;
    protected final E[] values;
    private final boolean nji;
    @OnlyIn(Dist.CLIENT)
    private TextureAtlasSprite[][] textures;
    @OnlyIn(Dist.CLIENT)
    private IBakedModel[] models;

    public ItemStack getStackFromType(E type) {
        return getStackFromType(type, 1);
    }

    public ItemStack getStackFromType(E type, int amount) {
        ItemStack ret = new ItemStack(this, amount);
        putOrdinal(ret, type);
        return ret;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int tintIndex) {
        int i = getOrdinal(stack);
        return i >= values.length ? -1 : values[i].getColorFromItemStack(stack, tintIndex);
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup creativeTab, @Nonnull NonNullList<ItemStack> subItems) {
        if (!isInGroup(creativeTab)) {
            return;
        }
        for (E e : values) {
            if (e.shouldShow()) {
                subItems.add(getStackFromType(e));
            }
        }
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        E e = stack == null ? null : get(getOrdinal(stack));
        if (e == null) {
            return super.getTranslationKey(stack);
        }
        return e.getUnlocalizedName(stack);
    }

    @Override
    public void registerTextures(IIconRegistrar iconRegistrar) {
        textures = new TextureAtlasSprite[values.length][];
        for (E e : values) {
            if (nji) {
                ((INoJsonItem) e).registerTextures(iconRegistrar);
            } else {
                ResourceLocation[] rls = e.getTextures();
                textures[e.ordinal()] = new TextureAtlasSprite[rls.length];
                for (int i = 0; i < rls.length; i++) {
                    textures[e.ordinal()][i] = iconRegistrar.registerSprite(rls[i]);
                }
            }
        }
    }

    @Override
    public IBakedModel getItemModel(ItemStack stack, World world, LivingEntity entity) {
        int i = getOrdinal(stack);
        E e = get(i);
        if (e == null) {
            return RenderHelper.getMissingModel();
        } else {
            if (nji) {
                return ((INoJsonItem) e).getItemModel(stack, world, entity);
            } else {
                return models[i];
            }
        }
    }

    @Override
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
        models = new IBakedModel[values.length];
        for (E e : values) {
            if (nji) {
                ((INoJsonItem) e).registerModels(quadBakery, modelBakery, templateBakery);
            } else {
                models[e.ordinal()] = modelBakery.itemModelForTextures(textures[e.ordinal()]);
            }
        }
    }

    private E get(int i) {
        return i >= values.length ? null : values[i];
    }

    private int getOrdinal(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        return Preconditions.checkNotNull(stack.getTag()).getInt("elenord");
    }

    private void putOrdinal(ItemStack stack, E val) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        Preconditions.checkNotNull(stack.getTag()).putInt("elenord", val.ordinal());
    }

}
