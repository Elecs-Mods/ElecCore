package elec332.core.asm;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import elec332.core.asm.asmload.ASMHooks;
import net.minecraft.launchwrapper.IClassTransformer;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 18-11-2015.
 */
public final class ASMLoader implements IClassTransformer {

    public ASMLoader(){
        try {
            Set<ClassPath.ClassInfo> list = ClassPath.from(getClass().getClassLoader()).getTopLevelClasses("elec332.core.asm.asmload");
            for (ClassPath.ClassInfo classInfo : list){
                Class clazz = classInfo.load();
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
                    continue;
                if (IASMClassTransformer.class.isAssignableFrom(clazz)) {
                    classTransformers.add((IASMClassTransformer) clazz.newInstance());
                }
            }
        } catch (Exception e){
            throw new RuntimeException("ElecCore-ASM failed to load properly.", e);
        }
    }

    private static final List<IASMClassTransformer> classTransformers;
    private static final String asmHooksClass, asmHooksClassClient;

    @Override
    public byte[] transform(String obf, String deobf, byte[] bytes) {
        for (IASMClassTransformer classTransformer : classTransformers){
            if (classTransformer.getDeObfuscatedClassName().equals(deobf)){
                bytes = classTransformer.transformClass(bytes);
            }
        }
        return bytes;
    }

    public static String getAsmHooksClass() {
        return asmHooksClass;
    }

    public static String getAsmHooksClassClient() {
        return asmHooksClassClient;
    }

    static {
        classTransformers = Lists.newArrayList();
        asmHooksClass = ASMHelper.getInternalName(ASMHooks.class);
        asmHooksClassClient = ASMHelper.getInternalName(ASMHooks.Client.class);
    }

}
