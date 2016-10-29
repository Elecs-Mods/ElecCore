package elec332.core.api.discovery;

import net.minecraftforge.fml.common.LoaderState;

/**
 * Created by Elec332 on 7-3-2016.
 */
public interface IASMDataProcessor {

    public void processASMData(IASMDataHelper asmData, LoaderState state);

}
