package elec332.core.compat.waila;

import elec332.core.api.module.ElecModule;
import elec332.core.compat.ModNames;
import elec332.core.main.ElecCore;

/**
 * Created by Elec332 on 15-8-2015.
 */
@SuppressWarnings("deprecation")
@ElecModule(owner = ElecCore.MODID, name = "WailaCompat", modDependencies = ModNames.WAILA)
public class WailaCompatHandler{}
/* implements IWailaDataProvider {

    @ElecModule.Instance
    private static WailaCompatHandler instance;

    private static Map<Capability, IWailaCapabilityDataProvider> map;

    @ElecModule.EventHandler
    public void init(FMLInitializationEvent event){
        FMLInterModComms.sendMessage("Waila", "register", getClass().getCanonicalName()+".register");
    }

    public static void register(IWailaRegistrar registrar){
        registrar.registerBodyProvider(instance, Block.class);
        registrar.registerNBTProvider(instance, Block.class);

        CapabilityProvider capabilityProvider = new CapabilityProvider();
        registrar.registerBodyProvider(capabilityProvider, TileEntity.class);
        registrar.registerNBTProvider(capabilityProvider, TileEntity.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IWailaInfoTile){
            currentTip = ((IWailaInfoTile) accessor.getTileEntity()).getWailaBody(itemStack, currentTip, accessor, config);
        }
        final List<String> currentTipF = currentTip;
        if (accessor.getNBTData() != null && !accessor.getNBTData().getBoolean("_nope_")) {
            InformationHandler.INSTANCE.addInformation(new IInformation() {

                @Nonnull
                @Override
                public InfoMod getProviderType() {
                    return InfoMod.WAILA;
                }

                @Override
                public void addInformation(String line) {
                    currentTipF.add(line);
                }

            }, new IInfoDataAccessorBlock() {

                @Nonnull
                @Override
                public EntityPlayer getPlayer() {
                    return accessor.getPlayer();
                }

                @Nonnull
                @Override
                public World getWorld() {
                    return accessor.getWorld();
                }

                @Nonnull
                @Override
                public BlockPos getPos() {
                    return accessor.getPosition();
                }

                @Nonnull
                @Override
                public NBTTagCompound getData() {
                    return accessor.getNBTData();
                }

                @Nonnull
                @Override
                public EnumFacing getSide() {
                    return accessor.getSide();
                }

                @Override
                public Vec3d getHitVec() {
                    return accessor.getMOP() == null ? null : accessor.getMOP().hitVec;
                }

                @Nonnull
                @Override
                public IBlockState getBlockState() {
                    return accessor.getBlockState();
                }

                @Nonnull
                @Override
                public Block getBlock() {
                    return accessor.getBlock();
                }

                @Nullable
                @Override
                public TileEntity getTileEntity() {
                    return accessor.getTileEntity();
                }

                @Override
                public ItemStack getStack() {
                    return accessor.getStack();
                }

                @Override
                public RayTraceResult getRayTraceResult() {
                    return accessor.getMOP();
                }

            });
        }
        return currentTip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te instanceof IWailaInfoTile && tag != null){
            tag = ((IWailaInfoTile) te).getWailaTag(player, te, tag, world, pos);
        }
        if (tag == null){
            tag = new NBTTagCompound();
        }
        final NBTTagCompound fTag = tag;
        RayTraceResult rtr = RayTraceHelper.retraceBlock(world, player, pos);
        if (rtr == null){
            fTag.setBoolean("_nope_", true);
            return fTag;

        }
        final RayTraceResult rtrF = rtr;
        return InformationHandler.INSTANCE.getInfoNBTData(fTag, te, player, new IInfoDataAccessorBlock() {

            private IBlockState ibs;

            @Nonnull
            @Override
            public EntityPlayer getPlayer() {
                return player;
            }

            @Nonnull
            @Override
            public World getWorld() {
                return world;
            }

            @Nonnull
            @Override
            public BlockPos getPos() {
                return pos;
            }

            @Nonnull
            @Override
            public NBTTagCompound getData() {
                return fTag;
            }

            @Override
            public Vec3d getHitVec() {
                return getRayTraceResult().hitVec;
            }

            @Nonnull
            @Override
            public EnumFacing getSide() {
                return getRayTraceResult().sideHit;
            }

            @Nonnull
            @Override
            public IBlockState getBlockState() {
                if (ibs == null){
                    ibs = WorldHelper.getBlockState(getWorld(), getPos());
                }
                return ibs;
            }

            @Nonnull
            @Override
            public Block getBlock() {
                return getBlockState().getBlock();
            }

            @Override
            public TileEntity getTileEntity() {
                return te;
            }

            @Override
            public ItemStack getStack() {
                return null;
            }

            @Override
            public RayTraceResult getRayTraceResult() {
                return rtrF;
            }

        });
    }

    @SuppressWarnings("unchecked")
    private static class CapabilityProvider implements IWailaDataProvider {

        @Override
        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return null;
        }

        @Override
        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return null;
        }

        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            EnumFacing facing = accessor.getSide();
            TileEntity tile = accessor.getTileEntity();
            if (tile == null || accessor.getNBTData().getBoolean("nope")){
                return currenttip;
            }
            for (Map.Entry<Capability, IWailaCapabilityDataProvider> entry : map.entrySet()){
                if (tile.hasCapability(entry.getKey(), facing)){
                    currenttip = entry.getValue().getWailaBody(currenttip, tile.getCapability(entry.getKey(), facing), accessor.getNBTData(), accessor.getPlayer(), accessor.getMOP(), accessor.getWorld(), accessor.getPosition(), accessor.getTileEntity());
                }
            }
            return currenttip;
        }

        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return null;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            RayTraceResult rtr = RayTraceHelper.retraceBlock(world, player, pos);
            if (te == null || rtr == null){
                tag.setBoolean("nope", true);
                return tag;
            }
            EnumFacing facing = rtr.sideHit;
            for (Map.Entry<Capability, IWailaCapabilityDataProvider> entry : map.entrySet()){
                if (te.hasCapability(entry.getKey(), facing)){
                    tag = entry.getValue().getWailaTag(te.getCapability(entry.getKey(), facing), player, te, tag, world, pos);
                }
            }
            return tag;
        }

    }

    @Deprecated
    public static interface IWailaInfoTile {

        public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config);

        public NBTTagCompound getWailaTag(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos);

    }

    static {
        map = ElecCoreRegistrar.WAILA_CAPABILITY_PROVIDER.getAllRegisteredObjects();
    }

}

*/