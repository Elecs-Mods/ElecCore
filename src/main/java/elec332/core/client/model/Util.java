package elec332.core.client.model;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 17-11-2015.
 */
@SideOnly(Side.CLIENT)
public class Util {

    public static Iterable<Block> getBlockIterator(){
        return GameData.getBlockRegistry().typeSafeIterable();
    }

    public static Iterable<Item> getItemIterator(){
        return GameData.getItemRegistry().typeSafeIterable();
    }

    public static String getNameFor(@Nonnull ItemStack stack){
        if (stack.getItem() == null)
            throw new IllegalArgumentException();
        return GameData.getItemRegistry().getNameForObject(stack.getItem()).toString();
    }

    public static TextureAtlasSprite getIconFrom(ResourceLocation rl){
        return getIconFrom(rl.toString());
    }

    public static TextureAtlasSprite getIconFrom(String s){
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(s);
    }

}
