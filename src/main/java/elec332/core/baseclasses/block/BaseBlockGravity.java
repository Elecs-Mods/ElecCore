package elec332.core.baseclasses.block;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class BaseBlockGravity {}/*extends BlockFalling{
    //Just an copy of baseBlock, but I cant extend 2 classes at the same time ;)
    public BaseBlockGravity(Material baseMaterial, String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event, int setQuantitydropped) {
        super(baseMaterial);
        this.modID = event.getModMetadata().modId;
        this.Dropped = setQuantitydropped;
        setBlockName(modID + "." + blockName);
        setCreativeTab(CreativeTab);
        this.name = blockName;
        RegisterHelper.registerBlock(this, blockName);
    }

    String name;
    String modID;
    int Dropped;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(modID + ":" + name);
    }

    public int quantityDropped(Random random)
    {
        return Dropped;
    }
}*/
