package elec332.core.item;

import elec332.core.api.client.IColoredItem;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.loading.INoJsonItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 21-8-2016.
 */
public class ItemEnumBased<E extends Enum<E> & IEnumItem> extends AbstractItem implements INoJsonItem, IColoredItem {

    public ItemEnumBased(ResourceLocation rl, Class<E> clazz) {
        super(rl);
        this.clazz = clazz;
        this.values = clazz.getEnumConstants();
        this.nji = this.values[0] instanceof INoJsonItem;
        this.setHasSubtypes(true);
        this.values[0].initializeItem(this);
    }

    protected final Class<E> clazz;
    protected final E[] values;
    private final boolean nji;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite[][] textures;
    @SideOnly(Side.CLIENT)
    private IBakedModel[] models;

    public ItemStack getStackFromType(E type) {
        return getStackFromType(type, 1);
    }

    public ItemStack getStackFromType(E type, int amount) {
        return new ItemStack(this, amount, type.ordinal());
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int tintIndex) {
        int i = stack.getItemDamage();
        return i >= values.length ? -1 : values[i].getColorFromItemStack(stack, tintIndex);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs creativeTab, @Nonnull NonNullList<ItemStack> subItems) {
        if (!isInCreativeTab(creativeTab)) {
            return;
        }
        for (E e : values) {
            if (e.shouldShow()) {
                subItems.add(getStackFromType(e));
            }
        }
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack stack) {
        E e = stack == null ? null : get(stack.getItemDamage());
        if (e == null) {
            return super.getUnlocalizedName(stack);
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
    public IBakedModel getItemModel(ItemStack stack, World world, EntityLivingBase entity) {
        int i = stack.getItemDamage();
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

    @Override
    public int getDamage(ItemStack stack) {
        if (values.length <= super.getDamage(stack)) {
            stack.setItemDamage(0);
        }
        return super.getDamage(stack);
    }


    private E get(int i) {
        return i >= values.length ? null : values[i];
    }

}
