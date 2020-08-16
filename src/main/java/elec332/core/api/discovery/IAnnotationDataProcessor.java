package elec332.core.api.discovery;

import net.minecraftforge.fml.ModLoadingStage;

/**
 * Created by Elec332 on 7-3-2016.
 * <p>
 * A class that wants to process the ASMDataTable at a given time
 * (Defined in {@link AnnotationDataProcessor} and given as a argument in {@link IAnnotationDataProcessor#processASMData(IAnnotationDataHandler, ModLoadingStage)})
 * in the mod lifecycle
 */
public interface IAnnotationDataProcessor {

    /**
     * Process the ASMDataTable, the current {@link ModLoadingStage} is supplied in case
     * this processor gets run at multiple times in the mod lifecycle
     *
     * @param annotationData The ASMDataTable
     * @param state          The current {@link ModLoadingStage}
     */
    void processASMData(IAnnotationDataHandler annotationData, ModLoadingStage state);

}
