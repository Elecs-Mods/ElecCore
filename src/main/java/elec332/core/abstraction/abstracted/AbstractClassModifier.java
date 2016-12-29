package elec332.core.abstraction.abstracted;

import elec332.core.util.ASMHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.FileInputStream;

/**
 * Created by Elec332 on 23-12-2016.
 */
public abstract class AbstractClassModifier implements ASMHelper.IClassModifier {

    public void copyMethodsFrom(String internalClassName, ClassWriter cw, Class<?> copyFrom, IMethodModifier modifier){
        try {
            ClassReader cr = ASMHelper.getClassReaderFrom(copyFrom);
            ClassNode cv = new ClassNode();
            cr.accept(cv, 0);
            for (MethodNode mn : cv.methods) {
                if (mn.visibleAnnotations != null) {
                    for (AnnotationNode an : mn.visibleAnnotations) {
                        if (an.desc.equals(Type.getDescriptor(CopyMarker.class))) {
                            if (modifier != null){
                                modifier.modifyMethod(internalClassName, mn);
                            }
                            mn.accept(cw);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface IMethodModifier {

        public void modifyMethod(String internalClassName, MethodNode mn);

        public static IMethodModifier combine(IMethodModifier... modifiers){
            if (modifiers == null || modifiers.length == 0){
                return null;
            }
            return new IMethodModifier() {

                @Override
                public void modifyMethod(String internalClassName, MethodNode mn) {
                    for (IMethodModifier modifier : modifiers){
                        if (modifier != null){
                            modifier.modifyMethod(internalClassName, mn);
                        }
                    }
                }

            };
        }

    }

    public void copyFieldsFrom(String internalClassName, ClassWriter cw, Class<?> copyFrom, IFieldModifier modifier){
        try {
            ClassReader cr = ASMHelper.getClassReaderFrom(copyFrom);
            ClassNode cv = new ClassNode();
            cr.accept(cv, 0);
            for (FieldNode fn : cv.fields) {
                if (fn.visibleAnnotations != null) {
                    for (AnnotationNode an : fn.visibleAnnotations) {
                        if (an.desc.equals(Type.getDescriptor(CopyMarker.class))) {
                            if (modifier != null){
                                modifier.modifyField(internalClassName, fn);
                            }
                            fn.accept(cw);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface IFieldModifier {

        public void modifyField(String internalClassName, FieldNode mn);

    }

    public class MethodFixer implements IMethodModifier {

        public MethodFixer(Class<?> original){
            this.original = original;
        }

        private final Class<?> original;

        @Override
        @SuppressWarnings("all")
        public void modifyMethod(String internalClassName, MethodNode mn) {
            InsnList ins = mn.instructions;
            for (int i = 0; i < ins.size(); i++) {
                AbstractInsnNode ain = ins.get(i);
                if (ain.getType() == AbstractInsnNode.METHOD_INSN && ((MethodInsnNode)ain).owner.equals(Type.getInternalName(original))) {
                    MethodInsnNode min = (MethodInsnNode) ain;
                    AbstractInsnNode nxt = ain.getNext();
                    ins.remove(ain);
                    ins.insertBefore(nxt, new MethodInsnNode(min.getOpcode(), internalClassName, min.name, min.desc, min.itf));
                } else if (ain.getType() == AbstractInsnNode.FIELD_INSN && ((FieldInsnNode)ain).owner.equals(Type.getInternalName(original))){
                    FieldInsnNode fin = (FieldInsnNode) ain;
                    AbstractInsnNode nxt = ain.getNext();
                    ins.remove(ain);
                    ins.insertBefore(nxt, new FieldInsnNode(fin.getOpcode(), internalClassName, fin.name, fin.desc));
                }
            }
            for (LocalVariableNode lvn : mn.localVariables){
                if (lvn.name.equals("this")){
                    lvn.desc = "L"+internalClassName+";";
                }
            }
        }

    }

}
