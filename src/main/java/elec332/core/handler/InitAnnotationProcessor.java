package elec332.core.handler;

import elec332.core.api.discovery.ASMDataProcessor;
import elec332.core.api.registration.RegisteredTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 7-3-2016.
 */

@ASMDataProcessor(LoaderState.INITIALIZATION)
public class InitAnnotationProcessor extends AbstractAnnotationProcessor {

    @Override
    protected void registerProcesses() {

/*
        registerDataProcessor(RegisteredMultiPart.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            @SuppressWarnings("unchecked")
            public void accept(ASMDataTable.ASMData data) {
                try {
                    MultipartRegistry.registerPart((Class<? extends Multipart>) Class.forName(data.getClassName()), (String) data.getAnnotationInfo().get("value"));
                } catch (Exception e){
                    logger.error("Error registering multipart: "+data.getClassName());
                }
            }

        }, Loader.isModLoaded("mcmultipart"));
*/
        registerDataProcessor(RegisteredTileEntity.class, new Consumer<ASMDataTable.ASMData>() {

            @Override
            @SuppressWarnings("unchecked")
            public void accept(ASMDataTable.ASMData data) {
                try {
                    GameRegistry.registerTileEntity((Class<? extends TileEntity>) Class.forName(data.getClassName()), (String) data.getAnnotationInfo().get("value"));
                } catch (Exception e){
                    logger.error("Error registering tile: "+data.getClassName());
                }
            }

        });

    }

}