package elec332.core.baseclasses.block;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.ModInfoHelper;
import elec332.core.helper.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class BaseBlock extends Block{
    public BaseBlock(Material baseMaterial, String blockName, FMLPreInitializationEvent event) {
       this(baseMaterial, blockName, ModInfoHelper.getModID(event));
    }
    public BaseBlock(Material material, String blockName, String modID){
        super(material);
        this.modID = modID;
        setBlockName(modID + "." + blockName);
        this.name = blockName;
        RegisterHelper.registerBlock(this, blockName);
    }

    public BaseBlock setGhost(){
        this.ghost = true;
        this.setNoOpaqueCube();
        return this;
    }

    public BaseBlock setItemDropped(Item itemDropped){
        this.dropped = itemDropped;
        return this;
    }

    //I know vanilla has this, but that's a void, this isn't ;)
    public BaseBlock setToolLevel(String toolClass, int level){
        this.setHarvestLevel(toolClass, level);
        return this;
    }

    public BaseBlock setNoOpaqueCube(){
        this.opaqueCube = false;
        return this;
    }

    public BaseBlock setQuantityDropped(int setQuantityDropped){
        this.Dropped = setQuantityDropped;
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
