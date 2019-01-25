package elec332.core.handler.annotations;

import elec332.core.api.discovery.AnnotationDataProcessor;
import net.minecraftforge.fml.ModLoadingStage;

/**
 * Created by Elec332 on 24-9-2016.
 */
@SuppressWarnings("unused")
@AnnotationDataProcessor(ModLoadingStage.COMMON_SETUP)
public class PreInitAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    protected void registerProcesses() {

    }

}
