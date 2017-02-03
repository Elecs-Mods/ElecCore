package elec332.core.abstraction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
public interface IItem {


    @SideOnly(Side.CLIENT)
    @Nonnull //1.11+ only
    default public ItemStack getDefaultInstance(Item item) {
        return getFallback().getDefaultInstance(item);
    }

    /**
     * Creates a new override param for item models. See usage in clock, compass, elytra, etc.
     */
    default public void addPropertyOverride(ResourceLocation key, IItemPropertyGetter getter) {
        getFallback().addPropertyOverride(key, getter);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    default public IItemPropertyGetter getPropertyGetter(ResourceLocation key) {
        return getFallback().getPropertyGetter(key);
    }

    /**
     * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
     */
    default public boolean updateItemStackNBT(NBTTagCompound tag) {
        return getFallback().updateItemStackNBT(tag);
    }

    @SideOnly(Side.CLIENT)
    default public boolean hasCustomProperties() {
        return getFallback().hasCustomProperties();
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    default public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return getFallback().onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    default public float getStrengthVsBlock(ItemStack stack, IBlockState state) {
        return getFallback().getStrengthVsBlock(stack, state);
    }

    default public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return getFallback().onItemRightClick(world, player, hand);
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    default public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        return getFallback().onItemUseFinish(stack, world, entityLiving);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    default public int getMetadata(int damage) {
        return getFallback().getMetadata(damage);
    }

    default public boolean getHasSubtypes() {
        return getFallback().getHasSubtypes();
    }

    default public IItem setHasSubtypes(boolean hasSubtypes) {
        getFallback().setHasSubtypes(hasSubtypes);
        return this;
    }

    /**
     * set max damage of an Item
     */
    default public IItem setMaxDamage(int maxDamage) {
        getFallback().setMaxDamage(maxDamage);
        return this;
    }

    default public boolean isDamageable() {
        return getFallback().isDamageable();
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    default public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return getFallback().hitEntity(stack, target, attacker);
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    default public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        return getFallback().onBlockDestroyed(stack, world, state, pos, entityLiving);
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    default public boolean canHarvestBlock(IBlockState state) {
        return getFallback().canHarvestBlock(state);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    default public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        return getFallback().itemInteractionForEntity(stack, player, target, hand);
    }

    /**
     * Sets bFull3D to True and return the object.
     */
    default public IItem setFull3D() {
        getFallback().setFull3D();
        return this;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    default public boolean isFull3D() {
        return getFallback().isFull3D();
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    @SideOnly(Side.CLIENT)
    default public boolean shouldRotateAroundWhenRendering() {
        return getFallback().shouldRotateAroundWhenRendering();
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    default public IItem setUnlocalizedName(String unlocalizedName) {
        getFallback().setUnlocalizedName(unlocalizedName);
        return this;
    }

    /**
     * Translates the unlocalized name of this item, but without the .name suffix, so the translation fails and the
     * unlocalized name itself is returned.
     */
    default public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return getFallback().getUnlocalizedNameInefficiently(stack);
    }

    /**
     * Returns the unlocalized name of this item.
     */
    default public String getUnlocalizedName() {
        return getFallback().getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    default public String getUnlocalizedName(ItemStack stack) {
        return getFallback().getUnlocalizedName(stack);
    }

    default public IItem setContainerItem(Item containerItem) {
        getFallback().setContainerItem(containerItem);
        return this;
    }

    /**
     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
     */
    default public boolean getShareTag() {
        return getFallback().getShareTag();
    }

    @Nullable
    default public Item getContainerItem() {
        return getFallback().getContainerItem();
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    default public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        getFallback().onUpdate(stack, world, entity, itemSlot, isSelected);
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    default public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        getFallback().onCreated(stack, world, player);
    }

    /**
     * false for all Items except sub-classes of ItemMapBase
     */
    default public boolean isMap() {
        return getFallback().isMap();
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    default public EnumAction getItemUseAction(ItemStack stack) {
        return getFallback().getItemUseAction(stack);
    }

    /**
     * How long it takes to use or consume an item
     */
    default public int getMaxItemUseDuration(ItemStack stack) {
        return getFallback().getMaxItemUseDuration(stack);
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    default public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        getFallback().onPlayerStoppedUsing(stack, world, entityLiving, timeLeft);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    default public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        getFallback().addInformation(stack, player, tooltip, advanced);
    }

    default public String getItemStackDisplayName(ItemStack stack) {
        return getFallback().getItemStackDisplayName(stack);
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     *
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @SideOnly(Side.CLIENT)
    default public boolean hasEffect(ItemStack stack) {
        return getFallback().hasEffect(stack);
    }

    /**
     * Return an item rarity from EnumRarity
     */
    default public EnumRarity getRarity(ItemStack stack) {
        return getFallback().getRarity(stack);
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    default public boolean isEnchantable(ItemStack stack) {
        return getFallback().isEnchantable(stack);
    }

    default public RayTraceResult rayTrace(World world, EntityPlayer playerIn, boolean useLiquids) {
        return getFallback().rayTrace(world, playerIn, useLiquids);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    default public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        getFallback().getSubItems(item, tab, subItems);
    }

    /**
     * returns this;
     */
    default public IItem setCreativeTab(CreativeTabs tab) {
        getFallback().setCreativeTab(tab);
        return this;
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    default public CreativeTabs getCreativeTab() {
        return getFallback().getCreativeTab();
    }

    /**
     * Returns true if players can use this item to affect the world (e.g. placing blocks, placing ender eyes in portal)
     * when not in creative
     */
    default public boolean canItemEditBlocks() {
        return getFallback().canItemEditBlocks();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    default public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return getFallback().getIsRepairable(toRepair, repair);
    }

    default public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return getFallback().getAttributeModifiers(slot, stack);
    }

    /**
     * Called when a player drops the item into the world,
     * returning false from this will prevent the item from
     * being removed from the players inventory and spawning
     * in the world
     *
     * @param player The player that dropped the item
     * @param item The item stack, before the item is removed.
     */
    default public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        return getFallback().onDroppedByPlayer(item, player);
    }

    /**
     * Allow the item one last chance to modify its name used for the
     * tool highlight useful for adding something extra that can't be removed
     * by a user in the displayed name, such as a mode of operation.
     *
     * @param item the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in this method.
     */
    default public String getHighlightTip(ItemStack item, String displayName) {
        return getFallback().getHighlightTip(item, displayName);
    }

    /**
     * This is called when the item is used, before the block is activated.
     * @param player The Player that used the item
     * @param world The Current World
     * @param pos Target position
     * @param side The side of the target hit
     * @param hand Which hand the item is being held in.
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    default public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return getFallback().onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    /**
     * Called by CraftingManager to determine if an item is reparable.
     * @return True if reparable
     */
    default public boolean isRepairable() {
        return getFallback().isRepairable();
    }

    /**
     * Call to disable repair recipes.
     * @return The current Item instance
     */
    default public IItem setNoRepair() {
        getFallback().setNoRepair();
        return this;
    }

    default public IItem setMaxStackSize(int maxStackSize) {
        getFallback().setMaxDamage(maxStackSize);
        return this;
    }


    /**
     * Override this method to change the NBT data being sent to the client.
     * You should ONLY override this when you have no other choice, as this might change behavior client side!
     *
     * 1.11+ only
     *
     * @param stack The stack to send the NBT tag for
     * @return The NBT tag
     */
    @Nullable
    default public NBTTagCompound getNBTShareTag(ItemStack stack) {
        return getFallback().getNBTShareTag(stack);
    }

    /**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param pos Block's position in world
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    default public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        return getFallback().onBlockStartBreak(itemstack, pos, player);
    }

    /**
     * Called each tick while using an item.
     * @param stack The Item being used
     * @param player The Player using the item
     * @param count The amount of time in tick the item has been used for continuously
     */
    default public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        getFallback().onUsingTick(stack, player, count);
    }

    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    default public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return getFallback().onLeftClickEntity(stack, player, entity);
    }

    /**
     * ItemStack sensitive version of getContainerItem.
     * Returns a full ItemStack instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    default public ItemStack getContainerItem(ItemStack itemStack) {
        return getFallback().getContainerItem(itemStack);
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    default public boolean hasContainerItem(ItemStack stack) {
        return getFallback().hasContainerItem(stack);
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground as a EntityItem.
     * This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param world The world the entity is in
     * @return The normal lifespan in ticks.
     */
    default public int getEntityLifespan(ItemStack itemStack, World world) {
        return getFallback().getEntityLifespan(itemStack, world);
    }

    /**
     * Determines if this Item has a special entity for when they are in the world.
     * Is called when a EntityItem is spawned in the world, if true and Item#createCustomEntity
     * returns non null, the EntityItem will be destroyed and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true, Item#createCustomEntity will be called
     */
    default public boolean hasCustomEntity(ItemStack stack) {
        return getFallback().hasCustomEntity(stack);
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to function normally.
     * Called when the item it placed in a world.
     *
     * @param world The world object
     * @param location The EntityItem object, useful for getting the position of the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
    @Nullable
    default public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return getFallback().createEntity(world, location, itemstack);
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing for cleaner
     * control over the update of the item without having to write a subclass.
     *
     * @param entityItem The entity Item
     * @return Return true to skip any further update code.
     */
    default public boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem entityItem) {
        return getFallback().onEntityItemUpdate(entityItem);
    }

    /**
     * Gets a list of tabs that items belonging to this class can display on,
     * combined properly with getSubItemsC allows for a single item to span
     * many sub-items across many tabs.
     *
     * @return A list of all tabs that this item could possibly be one.
     */
    default public CreativeTabs[] getCreativeTabs() {
        return getFallback().getCreativeTabs();
    }

    /**
     * Determines the base experience for a player when they remove this item from a furnace slot.
     * This number must be between 0 and 1 for it to be valid.
     * This number will be multiplied by the stack size to get the total experience.
     *
     * -1 will default to the old lookups.
     *
     * @param item The item stack the player is picking up.
     * @return The amount to award for each item.
     */
    default public float getSmeltingExperience(ItemStack item) {
        return getFallback().getSmeltingExperience(item);
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
     *
     * @param world The world
     * @param pos Block position in world
     * @param player The Player that is wielding the item
     * @return
     */
    default public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return getFallback().doesSneakBypassUse(stack, world, pos, player);
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    default public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
        getFallback().onArmorTick(world, player, itemStack);
    }

    /**
     * Determines if the specific ItemStack can be placed in the specified armor slot.
     *
     * @param stack The ItemStack
     * @param armorType Armor slot ID: 0: Helmet, 1: Chest, 2: Legs, 3: Boots
     * @param entity The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    default public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
        return getFallback().isValidArmor(stack, armorType, entity);
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book The book
     * @return if the enchantment is allowed
     */
    default public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return getFallback().isBookEnchantable(stack, book);
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that
     * should be use for the currently equipped item.
     * This will only be called on instances of ItemArmor.
     *
     * Returning null from this function will use the default value.
     *
     * @param stack ItemStack for the equipped armor
     * @param entity The entity wearing the armor
     * @param slot The slot the armor is in
     * @param type The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    @Nullable
    default public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return getFallback().getArmorTexture(stack, entity, slot, type);
    }

    /**
     * Returns the font renderer used to render tooltips and overlays for this item.
     * Returning null will use the standard font renderer.
     *
     * @param stack The current item stack
     * @return A instance of FontRenderer or null to use default
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    default public FontRenderer getFontRenderer(ItemStack stack) {
        return getFallback().getFontRenderer(stack);
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     *
     * @param  entityLiving  The entity wearing the armor
     * @param  itemStack  The itemStack to render the model of
     * @param  armorSlot  The slot the armor is in
     * @param _default Original armor model. Will have attributes set.
     * @return  A ModelBiped to render instead of the default
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    default public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        return getFallback().getArmorModel(entityLiving, itemStack, armorSlot, _default);
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *
     * @param entityLiving The entity swinging the item.
     * @param stack The Item stack
     * @return True to cancel any further processing by EntityLiving
     */
    default public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return getFallback().onEntitySwing(entityLiving, stack);
    }

    /**
     * Called when the client starts rendering the HUD, for whatever item the player currently has as a helmet.
     * This is where pumpkins would render there overlay.
     *
     * @param stack The ItemStack that is equipped
     * @param player Reference to the current client entity
     * @param resolution Resolution information about the current viewport and configured GUI Scale
     * @param partialTicks Partial ticks for the renderer, useful for interpolation
     */
    @SideOnly(Side.CLIENT)
    default public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks){
        getFallback().renderHelmetOverlay(stack, player, resolution, partialTicks);
    }

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the itemDamage field on ItemStack, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    default public int getDamage(ItemStack stack) {
        return getFallback().getDamage(stack);
    }

    /**
     * This used to be 'display damage' but its really just 'aux' data in the ItemStack, usually shares the same variable as damage.
     * @param stack
     * @return
     */
    default public int getMetadata(ItemStack stack) {
        return getFallback().getMetadata(stack);
    }

    /**
     * Determines if the durability bar should be rendered for this item.
     * Defaults to vanilla stack.isDamaged behavior.
     * But modders can use this for any data they wish.
     *
     * @param stack The current Item Stack
     * @return True if it should render the 'durability' bar.
     */
    default public boolean showDurabilityBar(ItemStack stack) {
        return getFallback().showDurabilityBar(stack);
    }

    /**
     * Queries the percentage of the 'Durability' bar that should be drawn.
     *
     * @param stack The current ItemStack
     * @return 1.0 for 100% 0 for 0%
     */
    default public double getDurabilityForDisplay(ItemStack stack) {
        return getFallback().getDurabilityForDisplay(stack);
    }

    /**
     * Returns the packed int RGB value used to render the durability bar in the GUI.
     * Defaults to a value based on the hue scaled as the damage decreases, but can be overriden.
     *
     *  1.11+ only
     *
     * @param stack Stack to get durability from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    default public int getRGBDurabilityForDisplay(ItemStack stack) {
        return getFallback().getRGBDurabilityForDisplay(stack);
    }
    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in this item,
     * but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    default public int getMaxDamage(ItemStack stack) {
        return getFallback().getMaxDamage(stack);
    }

    /**
     * Return if this itemstack is damaged. Note only called if {@link #isDamageable()} is true.
     * @param stack the stack
     * @return if the stack is damaged
     */
    default public boolean isDamaged(ItemStack stack) {
        return getFallback().isDamaged(stack);
    }

    /**
     * Set the damage for this itemstack. Note, this method is responsible for zero checking.
     * @param stack the stack
     * @param damage the new damage value
     */
    default public void setDamage(ItemStack stack, int damage) {
        getFallback().setDamage(stack, damage);
    }

    /**
     * ItemStack sensitive version of {@link #canHarvestBlock(IBlockState)}
     * @param state The block trying to harvest
     * @param stack The itemstack used to harvest the block
     * @return true if can harvest the block
     */
    default public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return getFallback().canHarvestBlock(state, stack);
    }

    /**
     * Gets the maximum number of items that this stack should be able to hold.
     * This is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
     *
     * @param stack The ItemStack
     * @return The maximum number this item can be stacked to
     */
    default public int getItemStackLimit(ItemStack stack) {
        return getFallback().getItemStackLimit(stack);
    }

    /**
     * Sets or removes the harvest level for the specified tool class.
     *
     * @param toolClass Class
     * @param level Harvest level:
     *     Wood:    0
     *     Stone:   1
     *     Iron:    2
     *     Diamond: 3
     *     Gold:    0
     */
    default public void setHarvestLevel(String toolClass, int level) {
        getFallback().setHarvestLevel(toolClass, level);
    }

    default public Set<String> getToolClasses(ItemStack stack) {
        return getFallback().getToolClasses(stack);
    }

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack This item stack instance
     * @param toolClass Tool Class
     * @param player The player trying to harvest the given blockstate
     * @param blockState The block to harvest
     * @return Harvest level, or -1 if not the specified tool type.
     */
    default public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        return getFallback().getHarvestLevel(stack, toolClass, player, blockState);
    }

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @param stack The ItemStack
     * @return the item echantability value
     */
    default public int getItemEnchantability(ItemStack stack) {
        return getFallback().getItemEnchantability(stack);
    }

    /**
     * Checks whether an item can be enchanted with a certain enchantment. This applies specifically to enchanting an item in the enchanting table and is called when retrieving the list of possible enchantments for an item.
     * Enchantments may additionally (or exclusively) be doing their own checks in {@link net.minecraft.enchantment.Enchantment#canApplyAtEnchantingTable(ItemStack)}; check the individual implementation for reference.
     * By default this will check if the enchantment type is valid for this item type.
     * @param stack the item stack to be enchanted
     *
     * 1.11+ only
     *
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    default public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return getFallback().canApplyAtEnchantingTable(stack, enchantment);
    }

    /**
     * Whether this Item can be used as a payment to activate the vanilla beacon.
     * @param stack the ItemStack
     * @return true if this Item can be used
     */
    default public boolean isBeaconPayment(ItemStack stack) {
        return getFallback().isBeaconPayment(stack);
    }

    /**
     * Determine if the player switching between these two item stacks
     * @param oldStack The old stack that was equipped
     * @param newStack The new stack
     * @param slotChanged If the current equipped slot was changed,
     *                    Vanilla does not play the animation if you switch between two
     *                    slots that hold the exact same item.
     * @return True to play the item change animation
     */
    default public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return getFallback().shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    /**
     * Called when the player is mining a block and the item in his hand changes.
     * Allows to not reset blockbreaking if only NBT or similar changes.
     * @param oldStack The old stack that was used for mining. Item in players main hand
     * @param newStack The new stack
     * @return True to reset block break progress
     */
    default public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return getFallback().shouldCauseBlockBreakReset(oldStack, newStack);
    }

    /**
     * Called from ItemStack.setItem, will hold extra data for the life of this ItemStack.
     * Can be retrieved from stack.getCapabilities()
     * The NBT can be null if this is not called from readNBT or if the item the stack is
     * changing FROM is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem() to see the OLD item.
     * Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold capabilities for the life of this item.
     */
    @Nullable
    default public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return getFallback().initCapabilities(stack, nbt);
    }

    default public ImmutableMap<String, ITimeValue> getAnimationParameters(final ItemStack stack, final World world, final EntityLivingBase entity) {
        return getFallback().getAnimationParameters(stack, world, entity);
    }

    default public IItem getFallback(){
        return DefaultInstances.DEFAULT_ITEM;
    }

}
