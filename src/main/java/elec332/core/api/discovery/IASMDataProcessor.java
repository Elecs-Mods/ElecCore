package elec332.core.api.discovery;

import net.minecraftforge.fml.common.LoaderState;

/**
 * Created by Elec332 on 7-3-2016.
 *
 * A class that wants to process the ASMDataTable at a given time
 * (Defined in {@link ASMDataProcessor} and given as a argument in {@link IASMDataProcessor#processASMData(IASMDataHelper, LoaderState)})
 * in the mod lifecycle
 */
public interface IASMDataProcessor {

    /**
     * Process the ASMDataTable, the current {@link LoaderState} is supplied in case
     * this processor gets run at multiple times in the mod lifecycle
     *
     * @param asmData The ASMDataTable
     * @param state The current {@link LoaderState}
     */
    public void processASMData(IASMDataHelper asmData, LoaderState state);

}
