package elec332.core.util.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class baseblock extends Block{
    public baseblock(Material baseMaterial, String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event, int setQuantitydropped) {
        super(baseMaterial);
        this.modID = event.getModMetadata().modId;
        this.Dropped = setQuantitydropped;
        setBlockName(modID + "." + blockName);
        setCreativeTab(CreativeTab);
        this.name = blockName;
        RegisterHelper.registerBlock(this, blockName);
    }

    public baseblock setGhost(){
        this.ghost = true;
        this.setNoOpaqueCube();
        return this;
    }

    public baseblock setItemDropped(Item itemDropped){
        this.dropped = itemDropped;
        return this;
    }

    //I know vanilla has this, but that's a void, this isn't ;)
    public baseblock setToolLevel(String toolClass, int level){
        this.setHarvestLevel(toolClass, level);
        return this;
    }

    public baseblock setNoOpaqueCube(){
        this.opaqueCube = false;
        return this;
    }

    Boolean opaqueCube;
    Item dropped;
    boolean ghost;
    String name;
    String modID;
    int Dropped;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister){
        blockIcon = iconRegister.registerIcon(this.getTextureName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getTextureName(){
        if (this.textureName != null)
            return textureName;
        return modID + ":" + name;
    }

    @Override
    public boolean isOpaqueCube(){
        return opaqueCube != null ? opaqueCube : true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
        if (ghost)
            return null;
        return AxisAlignedBB.getBoundingBox((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)y + this.maxY, (double)z + this.maxZ);
    }

    @Override
    public int quantityDropped(Random random){
        return Dropped;
    }

    @Override
    public Item getItemDropped(int par1, Random rand, int par2){
        return dropped != null ? dropped : super.getItemDropped(par1, rand, par2);
    }
}
