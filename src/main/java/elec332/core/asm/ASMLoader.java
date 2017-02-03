package elec332.core.asm;

import com.google.common.collect.Lists;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.discovery.IAdvancedASMData;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.List;

/**
 * Created by Elec332 on 18-11-2015.
 */
@SuppressWarnings("unused")
public final class ASMLoader implements IClassTransformer {

    @APIHandlerInject
    public static void collectTransformers(IASMDataHelper data){
        for (IAdvancedASMData asmData : data.getAdvancedAnnotationList(ASMTransformer.class)){
            Class<?> clazz = asmData.loadClass();
            if (IASMClassTransformer.class.isAssignableFrom(clazz)){
                try {
                    classTransformers.add((IASMClassTransformer) clazz.newInstance());
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final List<IASMClassTransformer> classTransformers;

    @Override
    public byte[] transform(String obf, String deobf, byte[] bytes) {
        for (IASMClassTransformer classTransformer : classTransformers){
            if (deobf.contains(classTransformer.getDeObfuscatedClassName())){
                bytes = classTransformer.transformClass(bytes);
            }
        }
        return bytes;
    }

    static {
        classTransformers = Lists.newArrayList();
    }

}
