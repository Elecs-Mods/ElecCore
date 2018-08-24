package elec332.core.handler.annotations;

import elec332.core.api.discovery.ASMDataProcessor;
import net.minecraftforge.fml.common.LoaderState;

/**
 * Created by Elec332 on 24-9-2016.
 */
@SuppressWarnings("unused")
@ASMDataProcessor(LoaderState.PREINITIALIZATION)
public class PreInitAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    protected void registerProcesses() {

    }

}
