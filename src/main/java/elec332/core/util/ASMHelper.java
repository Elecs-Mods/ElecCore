package elec332.core.util;

import com.google.common.collect.Lists;
import elec332.core.main.ElecCore;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by Elec332 on 21-12-2016.
 */
public class ASMHelper {

    private ASMHelper(){
    }

    private static int count = 0;

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> makeImplementInterfaces(Class<T> original, Class<?> clazz, IClassModifier classModifier, Class<?>... exceptions){
        List<Class<?>> itf = Lists.newArrayList(exceptions);
        ClassWriter cw = new ClassWriter(0);
        List<Class<?>> ifs = Lists.newArrayList(clazz.getInterfaces());
        ifs.removeAll(itf);
        Class<?>[] interfaces = ifs.toArray(new Class[0]);
        String[] interfaceNames = new String[interfaces.length];
        String superType = getInternalName(original);
        String clazzType = getInternalName(clazz);
        String clazzNN = original.getName();
        for (int i = 0; i < interfaces.length; i++) {
            Class<?> interfaceC = interfaces[i];
            clazzNN += "_"+interfaceC.getSimpleName();
            interfaceNames[i] = getInternalName(interfaceC);
        }
        String name = getASMName(clazzNN);
        String internalName = "generated/" + name.replace('.', '/');
        String instanceDesc = Type.getDescriptor(Object.class);//Type.getDescriptor(clazz);
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, internalName, null, getInternalName(original), interfaceNames);
        cw.visitSource(".dynamic", null);
        cw.visitField(ACC_PUBLIC, "instance", instanceDesc, null, null).visitEnd();
        MethodVisitor mv;
        for (Constructor cTor : original.getConstructors()) {

            String cTorDescSuper = "(";

            for (Class c : cTor.getParameterTypes()){
                cTorDescSuper += Type.getDescriptor(c);
            }


            cTorDescSuper += ")V";
            String mycTorDesc = cTorDescSuper.replace("(", "("+instanceDesc);

            mv = cw.visitMethod(ACC_PUBLIC, "<init>", mycTorDesc, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, internalName, "instance", instanceDesc);
            mv.visitVarInsn(ALOAD, 0);
            for (int i = 2; i < cTor.getParameterCount() + 2; i++) {
                mv.visitVarInsn(Type.getType(cTor.getParameterTypes()[i - 2]).getOpcode(ILOAD), i);
            }
            mv.visitMethodInsn(INVOKESPECIAL, superType, "<init>", cTorDescSuper, false);

           /// mv.visitVarInsn(ALOAD, 0);
           /// mv.visitVarInsn(ALOAD, 1);
           /// mv.visitFieldInsn(PUTFIELD, internalName, "instance", instanceDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(cTor.getParameterCount() + 2, cTor.getParameterCount() + 2);
            mv.visitEnd();
        }
        for (Class<?> interfaceC : interfaces){
            for (Method m : interfaceC.getMethods()){
                mv = cw.visitMethod(ACC_PUBLIC, m.getName(), getMethodDescriptor(m), null, null);
                mv.visitCode();

                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, internalName, "instance", instanceDesc);
                mv.visitTypeInsn(CHECKCAST, getInternalName(interfaceC));

                for (int i = 0; i < m.getParameterCount(); i++) {
                    mv.visitVarInsn(Type.getType(m.getParameterTypes()[i]).getOpcode(ILOAD), i + 1);
                }

                mv.visitMethodInsn(INVOKEINTERFACE, /*clazzType*/ getInternalName(interfaceC) , m.getName(), getMethodDescriptor(m), true);
                Label l1 = new Label(), l2 = new Label();

                mv.visitInsn(Type.getType(m.getReturnType()).getOpcode(IRETURN));

                //mv.visitLocalVariable("this", "L"+internalName+";", null, l1, l2, 0);
                int i = m.getParameterCount() + 1;
                for (int j = 1; j < m.getParameterCount() + 1; j++) {
                    mv.visitLocalVariable("o"+j, Type.getType(m.getParameterTypes()[j - 1]).getDescriptor(), null, l1, l2, j);
                }
                mv.visitMaxs(i, i);
                mv.visitEnd();
            }
        }
        if (classModifier != null){
            cw = classModifier.modifyClass(internalName, cw);
        }
        cw.visitEnd();
        File file = new File(ASMHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace(ASMHelper.class.getSimpleName(), "testBackup.class"));
        if (ElecCore.developmentEnvironment && false) {
            System.out.println(file.toString());
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(cw.toByteArray());
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (Class<? extends T>) defineClass(internalName.replace('/', '.'), cw);
    }

    //Yep, this is the only way to do this...
    public static ClassReader getClassReaderFrom(Class<?> clazz){
        try {
            ClassLoader correctLoader = ASMHelper.class.getClassLoader();
            //correctLoader.loadClass(clazz.getName());
            return new ClassReader(correctLoader.getResourceAsStream(clazz.getName().replace('.', '/') + ".class"));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String getMethodDescriptor(Method m){
        return Type.getMethodDescriptor(m);
    }

    public static String getInternalName(Class<?> clazz){
        return Type.getInternalName(clazz);
    }

    public static String getInternalName(String clazz){
        return clazz.replace('.', '/');
    }

    public static Class<?> defineClass(String name, ClassWriter classWriter) {
        return defineClass(name, classWriter.toByteArray());
    }

    public static Class<?> defineClass(String name, byte[] data) {
        return ASMCLASSLOADER.defineClass(name, data);
    }

    private static String getASMName(String name){
        return name + "_" + count++;
    }

    private static final ASMClassLoader ASMCLASSLOADER;

    static {
        ASMCLASSLOADER = new ASMClassLoader();
    }

    public interface IClassModifier {

        public ClassWriter modifyClass(String internalClassName, ClassWriter cw, Object... additionalData);

        public static IClassModifier combine(final IClassModifier... modifiers){
            if (modifiers == null || modifiers.length == 0){
                return null;
            }
            return (internalClassName, cw, additionalData) -> {
                for (IClassModifier classModifier : modifiers){
                    if (classModifier != null) {
                        cw = classModifier.modifyClass(internalClassName, cw, additionalData);
                    }
                }
                return cw;
            };
        }

    }

    private static class ASMClassLoader extends ClassLoader {

        private ASMClassLoader() {
            super(ASMClassLoader.class.getClassLoader());
        }

        public Class<?> defineClass(String name, byte[] data) {
            return defineClass(name, data, 0, data.length);
        }

    }

}
