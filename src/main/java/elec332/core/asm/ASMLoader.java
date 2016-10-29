package elec332.core.asm;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import net.minecraft.launchwrapper.IClassTransformer;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Elec332 on 18-11-2015.
 *
 * Unused
 */
@SuppressWarnings("unused")
public final class ASMLoader implements IClassTransformer {

    public ASMLoader(){
        try {
            List<ClassPath.ClassInfo> list = Lists.newArrayList(ClassPath.from(getClass().getClassLoader()).getTopLevelClasses("elec332.core.asm.asmload"));
            list.addAll(ClassPath.from(getClass().getClassLoader()).getTopLevelClasses("elec332.asmload"));
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

    @Override
    public byte[] transform(String obf, String deobf, byte[] bytes) {
        for (IASMClassTransformer classTransformer : classTransformers){
            if (classTransformer.getDeObfuscatedClassName().equals(deobf)){
                bytes = classTransformer.transformClass(bytes);
            }
        }
        return bytes;
    }

    static {
        classTransformers = Lists.newArrayList();
    }

}
