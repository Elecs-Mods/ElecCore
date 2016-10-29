package elec332.core.util;

import com.google.common.base.Preconditions;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class FMLUtil {

    public static String getOwnerName(Class<?> clazz){
        ModContainer mod = getOwner(clazz);
        return mod == null ? "<unknown>" : mod.getModId();
    }

    @Nullable
    public static ModContainer getOwner(Class<?> clazz){
        for (ModContainer modContainer : getLoader().getActiveModList()){
            if (modContainer.getOwnedPackages().contains(clazz.getPackage().getName())){
                return modContainer;
            }
        }
        return null;
    }

    @Nullable
    public static ModContainer getModContainer(Object mod){
        return mod instanceof ModContainer ? (ModContainer) mod : FMLCommonHandler.instance().findContainerFor(mod);
    }

    public static boolean isInModInitialisation(){
        return !hasReachedState(LoaderState.AVAILABLE);
    }

    public static boolean hasReachedState(LoaderState state){
        return getLoader().hasReachedState(state);
    }

    @Nonnull
    public static Loader getLoader(){
        return Loader.instance();
    }

    @Nonnull
    public static ASMDataTable getASMDataTable(){
        if (dataTable == null){
            try {
                Field f = ModAPIManager.class.getDeclaredField("dataTable");
                f.setAccessible(true);
                dataTable = Preconditions.checkNotNull((ASMDataTable) f.get(ModAPIManager.INSTANCE));
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return dataTable;
    }

    @Nonnull
    public static LoadController getLoadController(){
        if (lc == null){
            try {
                Field f = Loader.class.getDeclaredField("modController");
                f.setAccessible(true);
                lc = Preconditions.checkNotNull((LoadController) f.get(getLoader()));
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return lc;
    }

    @Nullable
    public static ModContainer findMod(String modId){
        for (ModContainer mc : getLoader().getActiveModList()){
            if (mc.getModId().equals(modId)){
                return mc;
            }
        }
        return null;
    }

    private static ASMDataTable dataTable;
    private static LoadController lc;

}
