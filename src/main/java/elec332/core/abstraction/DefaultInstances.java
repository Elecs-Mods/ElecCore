package elec332.core.abstraction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import elec332.core.mcabstractionlayer.impl.MCAbstractedDefaultIItemInstance;
import elec332.core.util.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 21-12-2016.
 */
public class DefaultInstances {

    public static final IItem DEFAULT_ITEM;
    public static final IItemArrow DEFAULT_ARROW;


    public static IItemArmor createDefault(IItemArmor base){
        return new DefaultArmour(new ItemArmor(base.getArmorMaterial(), base.getRenderIndex(), base.getEquipmentSlot()));
    }

    public static IItemBlock createDefault(IItemBlock base){
        return new DefaultItemBlock(base);
    }

    static {
        DEFAULT_ITEM = new DefaultItem(new Item());
        DEFAULT_ARROW = new DefaultArrow(new ItemArrow());
    }

    public static IItemBlock.IDefaultBlockPlaceBehaviour getDefaultBlockPlacementBehaviour(IItemBlock owner){
        return new DefaultItemBlock.ExtIB(owner).gBPB();
    }

    private static class DefaultItemBlock extends DefaultItem<IItemBlock> implements IItemBlock {

        private DefaultItemBlock(IItemBlock owner) {
            super(new ExtIB(owner));
        }

        @Override
        public Block getBlock() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
            return ((ItemBlock)item).canPlaceBlockOnSide(world, pos, side, player, stack);
        }

        @Override
        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState, IDefaultBlockPlaceBehaviour defaultBlockPlaceBehaviour) {
            return defaultBlockPlaceBehaviour.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        }

        private static class ExtIB extends ItemBlock {

            @SuppressWarnings("all")
            private ExtIB(IItemBlock owner) {
                super(owner.getBlock());
                this.owner = owner;
            }

            private final IItemBlock owner;

            @Override
            public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
                return owner.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState, gBPB());
            }

            public IDefaultBlockPlaceBehaviour gBPB(){
                return new IDefaultBlockPlaceBehaviour(){

                    @Override
                    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
                        return ExtIB.super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
                    }

                };
            }

        }

    }

    private static class DefaultArmour extends DefaultItem<IItemArmor> implements IItemArmor {

        private DefaultArmour(Item item) {
            super(item);
        }

        @Nonnull
        @Override
        public EntityEquipmentSlot getEquipmentSlot() {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public ItemArmor.ArmorMaterial getArmorMaterial() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getRenderIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getColor(ItemStack stack) {
            return ((ItemArmor) item).getColor(stack);
        }

        @Override
        public void removeColor(ItemStack stack) {
            ((ItemArmor) item).removeColor(stack);
        }

        @Override
        public void setColor(ItemStack stack, int color) {
            ((ItemArmor) item).setColor(stack, color);
        }

        @Override
        public boolean hasOverlay(ItemStack stack) {
            return ((ItemArmor) item).hasOverlay(stack);
        }

    }

    private static class DefaultArrow extends DefaultItem<IItemArrow> implements IItemArrow {

        private DefaultArrow(ItemArrow item) {
            super(item);
        }

        @Override
        @Nonnull
        public EntityArrow createArrow(@Nonnull World world, @Nonnull ItemStack stack, EntityLivingBase shooter) {
            return ((ItemArrow) item).createArrow(world, stack, shooter);
        }

        @Override
        public boolean isInfinite(ItemStack arrow, ItemStack bow, EntityPlayer shooter) {
            return ((ItemArrow) item).isInfinite(arrow, bow, shooter);
        }

    }

    private static class DefaultItem<T extends IItem> extends MCAbstractedDefaultIItemInstance {

        private DefaultItem(Item item){
            super(item);
        }

        @Override
        public void addPropertyOverride(ResourceLocation key, IItemPropertyGetter getter) {
            item.addPropertyOverride(key, getter);
        }

        @Nullable
        @Override
        public IItemPropertyGetter getPropertyGetter(ResourceLocation key) {
            return item.getPropertyGetter(key);
        }

        @Override
        public boolean updateItemStackNBT(NBTTagCompound tag) {
            return item.updateItemStackNBT(tag);
        }

        @Override
        public boolean hasCustomProperties() {
            return item.hasCustomProperties();
        }

        @Override
        public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            return item.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        @Override
        public float getStrengthVsBlock(ItemStack stack, IBlockState state) {
            return item.getDestroySpeed(stack, state);
        }

        @Override
        public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
            return item.onItemUseFinish(stack, world, entityLiving);
        }

        @Override
        public int getMetadata(int damage) {
            return item.getMetadata(damage);
        }

        @Override
        public boolean getHasSubtypes() {
            return item.getHasSubtypes();
        }

        @Override
        public IItem setHasSubtypes(boolean hasSubtypes) {
            item.setHasSubtypes(hasSubtypes);
            return this;
        }

        @Override
        public IItem setMaxDamage(int maxDamage) {
            item.setMaxDamage(maxDamage);
            return this;
        }

        @Override
        public IItem setMaxStackSize(int maxStackSize) {
            item.setMaxStackSize(maxStackSize);
            return this;
        }

        @Override
        public boolean isDamageable() {
            return item.isDamageable();
        }

        @Override
        public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
            return item.hitEntity(stack, target, attacker);
        }

        @Override
        public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
            return item.onBlockDestroyed(stack, world, state, pos, entityLiving);
        }

        @Override
        public boolean canHarvestBlock(IBlockState state) {
            return item.canHarvestBlock(state);
        }

        @Override
        public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
            return item.itemInteractionForEntity(stack, player, target, hand);
        }

        @Override
        public IItem setFull3D() {
            item.setFull3D();
            return this;
        }

        @Override
        public boolean isFull3D() {
            return item.isFull3D();
        }

        @Override
        public boolean shouldRotateAroundWhenRendering() {
            return item.shouldRotateAroundWhenRendering();
        }

        @Override
        public IItem setUnlocalizedName(String unlocalizedName) {
            item.setUnlocalizedName(unlocalizedName);
            return this;
        }

        @Override
        public String getUnlocalizedNameInefficiently(ItemStack stack) {
            return item.getUnlocalizedNameInefficiently(stack);
        }

        @Override
        public String getUnlocalizedName() {
            return item.getUnlocalizedName();
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return item.getUnlocalizedName(stack);
        }

        @Override
        public IItem setContainerItem(Item containerItem) {
            item.setContainerItem(containerItem);
            return this;
        }

        @Override
        public boolean getShareTag() {
            return item.getShareTag();
        }

        @Nullable
        @Override
        public Item getContainerItem() {
            return item.getContainerItem();
        }

        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
            item.onUpdate(stack, world, entity, itemSlot, isSelected);
        }

        @Override
        public void onCreated(ItemStack stack, World world, EntityPlayer player) {
            item.onCreated(stack, world, player);
        }

        @Override
        public boolean isMap() {
            return item.isMap();
        }

        @Override
        public EnumAction getItemUseAction(ItemStack stack) {
            return item.getItemUseAction(stack);
        }

        @Override
        public int getMaxItemUseDuration(ItemStack stack) {
            return item.getMaxItemUseDuration(stack);
        }

        @Override
        public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
            item.onPlayerStoppedUsing(stack, world, entityLiving, timeLeft);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
            InventoryHelper.addInformation(item, stack, world, tooltip, advanced);
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            return item.getItemStackDisplayName(stack);
        }

        @Override
        public boolean hasEffect(ItemStack stack) {
            return item.hasEffect(stack);
        }

        @Override
        public EnumRarity getRarity(ItemStack stack) {
            return item.getRarity(stack);
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return item.isEnchantable(stack);
        }

        @Override
        public RayTraceResult rayTrace(World world, EntityPlayer playerIn, boolean useLiquids) {
            return item.rayTrace(world, playerIn, useLiquids);
        }

        @Override
        public IItem setCreativeTab(CreativeTabs tab) {
            item.setCreativeTab(tab);
            return this;
        }

        @Nullable
        @Override
        public CreativeTabs getCreativeTab() {
            return item.getCreativeTab();
        }

        @Override
        public boolean canItemEditBlocks() {
            return item.canItemEditBlocks();
        }

        @Override
        public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
            return item.getIsRepairable(toRepair, repair);
        }

        @Override
        public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
            return item.getAttributeModifiers(slot, stack);
        }

        @Override
        public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
            return item.onDroppedByPlayer(stack, player);
        }

        @Override
        public String getHighlightTip(ItemStack stack, String displayName) {
            return item.getHighlightTip(stack, displayName);
        }

        @Override
        public boolean isRepairable() {
            return item.isRepairable();
        }

        @Override
        public IItem setNoRepair() {
            item.setNoRepair();
            return this;
        }

        @Nullable
        @Override
        public NBTTagCompound getNBTShareTag(ItemStack stack) {
            return item.getNBTShareTag(stack);
        }

        @Override
        public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
            return item.onBlockStartBreak(itemstack, pos, player);
        }

        @Override
        public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
            item.onUsingTick(stack, player, count);
        }

        @Override
        public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
            return item.onLeftClickEntity(stack, player, entity);
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
            return item.getContainerItem(itemStack);
        }

        @Override
        public boolean hasContainerItem(ItemStack stack) {
            return item.hasCustomEntity(stack);
        }

        @Override
        public int getEntityLifespan(ItemStack itemStack, World world) {
            return item.getEntityLifespan(itemStack, world);
        }

        @Override
        public boolean hasCustomEntity(ItemStack stack) {
            return item.hasCustomEntity(stack);
        }

        @Nullable
        @Override
        public Entity createEntity(World world, Entity location, ItemStack itemstack) {
            return item.createEntity(world, location, itemstack);
        }

        @Override
        public boolean onEntityItemUpdate(EntityItem entityItem) {
            return item.onEntityItemUpdate(entityItem);
        }

        @Override
        public CreativeTabs[] getCreativeTabs() {
            return item.getCreativeTabs();
        }

        @Override
        public float getSmeltingExperience(ItemStack stack) {
            return item.getSmeltingExperience(stack);
        }

        @Override
        public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
            return item.doesSneakBypassUse(stack, world, pos, player);
        }

        @Override
        public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
            item.onArmorTick(world, player, itemStack);
        }

        @Override
        public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
            return item.isValidArmor(stack, armorType, entity);
        }

        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
            return item.isBookEnchantable(stack, book);
        }

        @Nullable
        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
            return item.getArmorTexture(stack, entity, slot, type);
        }

        @Nullable
        @Override
        public FontRenderer getFontRenderer(ItemStack stack) {
            return item.getFontRenderer(stack);
        }

        @Nullable
        @Override
        public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
            return item.getArmorModel(entityLiving, itemStack, armorSlot, _default);
        }

        @Override
        public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
            return item.onEntitySwing(entityLiving, stack);
        }

        @Override
        public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks) {
            item.renderHelmetOverlay(stack, player, resolution, partialTicks);
        }

        @Override
        public int getDamage(ItemStack stack) {
            return item.getDamage(stack);
        }

        @Override
        public int getMetadata(ItemStack stack) {
            return item.getMetadata(stack);
        }

        @Override
        public boolean showDurabilityBar(ItemStack stack) {
            return item.showDurabilityBar(stack);
        }

        @Override
        public double getDurabilityForDisplay(ItemStack stack) {
            return item.getDurabilityForDisplay(stack);
        }

        @Override
        public int getMaxDamage(ItemStack stack) {
            return item.getMaxDamage(stack);
        }

        @Override
        public boolean isDamaged(ItemStack stack) {
            return item.isDamaged(stack);
        }

        @Override
        public void setDamage(ItemStack stack, int damage) {
            item.setDamage(stack, damage);
        }

        @Override
        public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
            return item.canHarvestBlock(state, stack);
        }

        @Override
        public int getItemStackLimit(ItemStack stack) {
            return item.getItemStackLimit(stack);
        }

        @Override
        public void setHarvestLevel(String toolClass, int level) {
            item.setHarvestLevel(toolClass, level);
        }

        @Override
        public Set<String> getToolClasses(ItemStack stack) {
            return item.getToolClasses(stack);
        }

        @Override
        public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
            return item.getHarvestLevel(stack, toolClass, player, blockState);
        }

        @Override
        public int getItemEnchantability(ItemStack stack) {
            return item.getItemEnchantability(stack);
        }

        @Override
        public boolean isBeaconPayment(ItemStack stack) {
            return item.isBeaconPayment(stack);
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            return item.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
        }

        @Override
        public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
            return item.shouldCauseBlockBreakReset(oldStack, newStack);
        }

        @Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
            return item.initCapabilities(stack, nbt);
        }

        @Override
        public ImmutableMap<String, ITimeValue> getAnimationParameters(ItemStack stack, World world, EntityLivingBase entity) {
            return item.getAnimationParameters(stack, world, entity);
        }

        @Override
        public T getFallback() {
            throw new UnsupportedOperationException();
        }

    }

}
