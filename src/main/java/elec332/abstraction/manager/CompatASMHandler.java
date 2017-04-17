package elec332.abstraction.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import elec332.abstraction.handlers.IAbstractedClassProvider;
import elec332.core.abstraction.abstracted.AbstractClassModifier;
import elec332.core.api.annotations.AbstractionMarker;
import elec332.core.api.annotations.CopyMarker;
import elec332.core.asm.ASMTransformer;
import elec332.core.asm.IASMClassTransformer;
import elec332.core.main.ElecCore;
import elec332.core.util.ASMHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Elec332 on 26-1-2017.
 */
public class CompatASMHandler implements IASMClassTransformer {

    @Override
    public String getDeObfuscatedClassName() {
        return "elec332.core.";
    }

    private static final String annType = Type.getDescriptor(AbstractionMarker.class);
    private static final String copyType = Type.getDescriptor(CopyMarker.class);

    @Override
    public byte[] transformClass(byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(node, 0);
        node = transformClass(node);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    @SuppressWarnings("all")
    public ClassNode transformClass(ClassNode classNode) {
        AnnotationNode ann = null;
        boolean copy = false;
        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode an : classNode.visibleAnnotations) {
                if (an.desc.equals(annType)){
                    ann = an;
                }
                if (an.desc.equals(copyType)){
                    copy = true;
                }
            }
        }
        if (ann != null){
            String mS = (String) ann.values.get(1); //value of first key (key = 0)
            IAbstractedClassProvider cp = AbstractionManager.getAbstractionLayer().getClassProvider();
            Class impl = null;
            ClassReader linkedCLR = null;
            ClassNode cn = new ClassNode();
            linkedCLR.accept(cn, 0);
            try {
                Method m = cp.getClass().getDeclaredMethod(mS);
                impl = (Class) m.invoke(cp);
                linkedCLR = ASMHelper.getClassReaderFrom(impl);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
            if (linkedCLR == null){
                throw new RuntimeException();
            }
            if (copy){
                for (MethodNode mn : cn.methods){
                    if (mn.visibleAnnotations != null) {
                        for (AnnotationNode an : mn.visibleAnnotations) {
                            if (an.desc.equals(copyType)) {
                                AbstractClassModifier.IMethodModifier modifier = new AbstractClassModifier.MethodFixer(impl);
                                modifier.modifyMethod(classNode.name, mn);
                                classNode.methods.add(mn);
                                break;
                            }
                        }
                    }
                }
                return classNode;
            } else {
                cn.name = classNode.name;
                return cn;
            }
        }
        Map<MethodNode, MethodNode> rep = Maps.newHashMap();
        for (MethodNode mn : classNode.methods){
            if (mn.visibleAnnotations == null){
                continue;
            }
            String m = null;
            for (AnnotationNode an : mn.visibleAnnotations){
                if (an.desc.equals(annType)){
                    m = (String) an.values.get(1); //value of first key (key = 0)
                    break;
                }
            }
            if (!Strings.isNullOrEmpty(m)){
                try {
                    String[] fiS = m.split(":");
                    if (fiS.length < 1 || fiS.length > 2){
                        throw new IllegalArgumentException();
                    }
                    String methodN = fiS.length == 2 ? fiS[1] : mn.name;
                    m = fiS[0];
                    Method me = AbstractionManager.getAbstractionLayer().getClass().getDeclaredMethod(m);
                    Class<?> clz = (Class<?>) me.invoke(AbstractionManager.getAbstractionLayer());
                    ClassReader cr = ASMHelper.getClassReaderFrom(clz);
                    ClassNode cn = new ClassNode();
                    cr.accept(cn, 0);
                    for (MethodNode mnI : cn.methods){
                        if (mnI.name.equals(methodN) && mnI.desc.equals(mn.desc)){
                            MethodNode nn = new MethodNode(Opcodes.ASM5, mnI.access, mn.name, mnI.desc, mnI.signature, mnI.exceptions.toArray(new String[0]));
                            mnI.accept(nn);
                            mnI = nn;
                            //mnI.access += Opcodes.ACC_STATIC; //Make method static
                            mnI.access = mn.access; //Same access as old method
                            mnI.localVariables.remove(0);
                            for (int i = 0; i < mnI.instructions.size(); i++) {
                                AbstractInsnNode ain = mnI.instructions.get(i);
                                if (ain instanceof VarInsnNode){
                                    ((VarInsnNode) ain).var--; //Remove "this" variable
                                }
                            }
                            for (LocalVariableNode lv : mnI.localVariables){
                                lv.index--; //Remove "this" variable
                            }
                            rep.put(mn, mnI);
                            break;
                        }
                    }

                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        classNode.methods.replaceAll(methodNode -> {
            if (rep.containsKey(methodNode)){
                return rep.get(methodNode);
            }
            return methodNode;
        });
        if (ElecCore.developmentEnvironment && false) {
            File file = new File(CompatASMHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace(CompatASMHandler.class.getSimpleName(), "testBackup.class"));

            System.out.println(file.toString());
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                ClassWriter cw = new ClassWriter(0);
                classNode.accept(cw);
                fos.write(cw.toByteArray());
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return classNode;
    }

}
