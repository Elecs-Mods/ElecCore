package elec332.core.handler;

import elec332.core.api.annotations.ASMDataProcessor;
import elec332.core.api.annotations.RegisterTile;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import elec332.core.main.ElecCore;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 7-3-2016.
 */
@SuppressWarnings("unused")
@ASMDataProcessor(LoaderState.INITIALIZATION)
public class AnnotationProcessor implements IASMDataProcessor {

    private static Logger logger = ElecCore.logger;

    @Override
    @SuppressWarnings("unchecked")
    public void processASMData(IASMDataHelper asmData, LoaderState state) {
        for (ASMDataTable.ASMData data : asmData.getAnnotationList(RegisterTile.class)){
            try {
                GameRegistry.registerTileEntity((Class<? extends TileEntity>) Class.forName(data.getClassName()), (String) data.getAnnotationInfo().get("name"));
            } catch (Exception e){
                logger.error("Error registering tile: "+data.getClassName());
            }
        }
    }

}
