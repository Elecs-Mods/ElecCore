package elec332.core.api.dimension;

import net.minecraft.block.Block;
import net.minecraft.world.WorldProvider;

/**
 * Created by Elec332 on 28-1-2015.
 */
public interface IDimAPIProvider {

    /**Returns the Dimension ID of custom dimension
     */
    int getDimID();

    /**Returns the WordProvider of custom dimension
     */
    Class<? extends WorldProvider> getWorldProvider();

    /**Returns if the dimension should be loaded even if there
     * is no-one in the dimension
     * (Enable only if really necessary)
     */
    boolean keepDimLoaded();

    /**Returns if DimensionAPI sould make a portalBlock for you
     * THIS IS RECOMMENDED
     */
    boolean makePortalBlock();

    /**Returns the unlocalised name of the PortalBlock
     */
    String portalUnlocalisedName();

    /**Returns frame where the portal should be made of
     */
    Block getPortalFrameBlock();

}
