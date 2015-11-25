package elec332.core.tile;

import net.minecraft.util.EnumFacing;

/**
 * Created by Elec332 on 6-4-2015.
 */
public interface IRedstoneHandler {

    public int isProvidingWeakPower(EnumFacing side);

    public boolean canConnectRedstone(EnumFacing direction);

}
