package elec332.core.util;

/**
 * Created by Elec332 on 6-4-2015.
 */
public interface IRedstoneHandler {

    public int isProvidingWeakPower(int side);

    public boolean canConnectRedstone(int direction);

}
