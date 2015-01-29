package elec332.core.api.dimension;

import net.minecraftforge.common.DimensionManager;

import java.util.HashMap;

/**
 * Created by Elec332 on 29-1-2015.
 */
public class DimensionAPI {

    static HashMap<Integer, PortalBlock> Registry = new HashMap<Integer, PortalBlock>();

    public static void Register(IDimAPIProvider provider){
        DimensionManager.registerProviderType(provider.getDimID(), provider.getWorldProvider(), provider.keepDimLoaded());
        DimensionManager.registerDimension(provider.getDimID(), provider.getDimID());
        if (provider.makePortalBlock()){
            RegisterPortal(new PortalBlock(provider.portalUnlocalisedName(), provider.getPortalFrameBlock(), provider.getDimID()), provider.getDimID());
        }
    }

    public static void RegisterPortal(PortalBlock portalBlock, int DimID){
        Registry.put(DimID, portalBlock);
    }

    public static PortalBlock getPortalFromDIM(int DimID){
        return Registry.get(DimID);
    }
}
