package elec332.core.server;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import elec332.core.api.annotations.ASMDataProcessor;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 20-7-2016.
 */
@ExternalSaveHandler
public class NBTSaveHandler implements IExternalSaveHandler {

    private static final List<IExternalNBTSaveHandler> saveHandlers;
    private static final List<String> usedNames;

    @Override
    public void load(File worldDirectory) {
        File folder = getFolder(worldDirectory);
        for (IExternalNBTSaveHandler nbtSaveHandler : saveHandlers){
            String s = getName(nbtSaveHandler);

        }
    }

    @Override
    public void save(File worldDirectory) {

    }

    private File getFolder(File worldDirectory){
        return new File(worldDirectory, "externalData/NBT");
    }

    private static String getName(IExternalNBTSaveHandler handler){
        String s = handler.getClass().getAnnotation(ExternalSaveHandler.class).value();
        if (Strings.isNullOrEmpty(s)){
            throw new IllegalArgumentException("Null or empty ExternalNBTSaveHandler name for class: "+handler.getClass().getCanonicalName());
        }
        return s;
    }

    @ASMDataProcessor(LoaderState.INITIALIZATION)
    public static class ASMLoader implements IASMDataProcessor {

        @Override
        public void processASMData(IASMDataHelper asmData, LoaderState state) {
            if (saveHandlers.isEmpty()){
                Set<ASMDataTable.ASMData> dataSet = asmData.getAnnotationList(ExternalSaveHandler.class);
                for (ASMDataTable.ASMData data : dataSet){
                    try {
                        @SuppressWarnings("unchecked")
                        Class<?> clazz = getClass().getClassLoader().loadClass(data.getClassName());
                        if (IExternalNBTSaveHandler.class.isAssignableFrom(clazz)) {
                            IExternalNBTSaveHandler handler = (IExternalNBTSaveHandler) clazz.newInstance();
                            String s = getName(handler);
                            if (usedNames.contains(s)){
                                throw new IllegalArgumentException("ExternalNBTSaveHandler " + clazz.getCanonicalName() + "uses a name that has already been taken! Name: "+s);
                            }
                            usedNames.add(s);
                            saveHandlers.add(handler);
                        }
                    } catch (ClassNotFoundException e) {
                        //;
                    } catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    static {
        saveHandlers = Lists.newArrayList();
        usedNames = Lists.newArrayList();
    }

}
