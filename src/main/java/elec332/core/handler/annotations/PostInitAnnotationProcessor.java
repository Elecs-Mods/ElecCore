package elec332.core.handler.annotations;

import elec332.core.api.discovery.ASMDataProcessor;
import net.minecraftforge.fml.common.LoaderState;

/**
 * Created by Elec332 on 7-10-2016.
 */
@SuppressWarnings("unused")
@ASMDataProcessor(LoaderState.POSTINITIALIZATION)
public class PostInitAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    @SuppressWarnings("unchecked")
    protected void registerProcesses() {
        /*registerDataProcessor(RegisteredForestryIndividual.class, new Consumer<ASMDataTable.ASMData>() {
            @Override
            public void accept(ASMDataTable.ASMData asmData) {
                Class<?> clazz = loadClass(asmData);
                if (clazz.isEnum() && IIndividualTemplate.class.isAssignableFrom(clazz)){
                    for (Object o : clazz.getEnumConstants()){
                        IndividualDefinitionRegistry.registerBee((IIndividualTemplate)o);
                    }
                }
            }
        });*/
    }

}
