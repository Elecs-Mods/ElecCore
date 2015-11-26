package elec332.core.asm;

import elec332.core.client.model.replace.ElecBlockRendererDispatcher;
import elec332.core.client.model.replace.ElecItemModelMesher;
import elec332.core.client.model.replace.ElecItemRenderer;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import elec332.core.asm.asmload.IASMClassTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 18-11-2015.
 */
public class ASM implements IClassTransformer {

    public ASM(){
        try {
            Set<ClassPath.ClassInfo> list = ClassPath.from(getClass().getClassLoader()).getTopLevelClasses("elec332.core.asm.asmload");
            classSearch:
            for (ClassPath.ClassInfo classInfo : list){
                Class clazz = classInfo.load();
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
                    continue;
                for (Class interfaceClass : clazz.getInterfaces()){
                    if (IASMClassTransformer.class.isAssignableFrom(interfaceClass)){
                        classTransformers.add((IASMClassTransformer) clazz.newInstance());
                        continue classSearch; //Just to make sure...
                    }
                }
            }
        } catch (Exception e){
            throw new RuntimeException("ElecCore-ASM failed to load properly.", e);
        }
    }

    private static final List<IASMClassTransformer> classTransformers;

    @Override
    public byte[] transform(String obf, String deobf, byte[] bytes) {
        /*for (IASMClassTransformer classTransformer : classTransformers){
            if (classTransformer.getDeObfuscatedClassName().equals(deobf)){
                bytes = classTransformer.transformClass(bytes);
            }
        }
        return bytes;*/

        if (deobf.equals("net.minecraft.client.Minecraft")) {
            ClassNode node = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(node, 0);
            transFormMain(node);
            //transformBlockRenderStuff(node);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            return writer.toByteArray();
        } else if (deobf.equals("net.minecraft.client.renderer.entity.RenderItem")){
            ClassNode node = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(node, 0);
            transformRenderItem(node);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            return writer.toByteArray();
        }
        return bytes;
    }

    private void transFormMain(ClassNode classNode){
        for (MethodNode methodNode : classNode.methods){
            if (methodNode.exceptions.contains("org/lwjgl/LWJGLException")){
                System.out.println("Found init method!");
                if (!methodNode.tryCatchBlocks.isEmpty() && methodNode.tryCatchBlocks.get(0).type.equals("org/lwjgl/opengl/OpenGLException")){
                    System.out.println("Really found it now...");
                    transformSetup(methodNode);
                    transformBlockRenderStuff(methodNode);
                }

            }
        }
        //FMLCommonHandler.instance().exitJava(0, true);
    }

    private void transformSetup(MethodNode mn){
        InsnList instructions = mn.instructions;
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode ain = instructions.get(i);
            if (isLine(ain, 513)){
                //LabelNode l = injectRenderItemLoader(ain, instructions);
                AbstractInsnNode gt = ain.getNext();
                while (!isLine(gt, 516)){
                    gt = gt.getNext();
                }
                //LabelNode label = new LabelNode();
                injectRenderItemLoader(ain, instructions, ((LineNumberNode)gt).start);
                //instructions.insertBefore(gt, label);
                /*AbstractInsnNode prev = ain.getPrevious();
                ain = ain.getPrevious();
                for (int j = 0; j < 28; j++) {
                    ain = ain.getNext();
                    instructions.remove(ain.getPrevious());
                }
                injectRenderItemLoader(prev, instructions);*/
            }

            /*if (isOpCode(ain, Opcodes.GETFIELD) && ((FieldInsnNode)ain).name.equals("renderItem")) {
                ain = ain.getNext();
                if (ain.getOpcode() == Opcodes.INVOKEINTERFACE && ((MethodInsnNode) ain).desc.equals("(Lnet/minecraft/client/resources/IResourceManagerReloadListener;)V")) {
                    System.out.println("found place");
                    injectRenderItemLoader(ain, instructions);
                }
            }*/
        }
    }

    @Nullable
    private LabelNode findLabel(InsnList insnList, int line){
        return findLabelAfter(insnList.get(0), line);
    }

    @Nullable
    private LabelNode findLabelAfter(final AbstractInsnNode from, int line) {
        AbstractInsnNode gt = from.getNext();
        while (!isLine(gt, line)) {
            if (gt == null){
                return null;
            }
            gt = gt.getNext();
        }
        return ((LineNumberNode) gt).start;
    }

    private boolean isLine(AbstractInsnNode node, int line){
        return isType(node, AbstractInsnNode.LINE) && ((LineNumberNode)node).line == line;
    }

    private boolean isType(AbstractInsnNode node, int type){
        return node != null && node.getType() == type;
    }

    private void injectRenderItemLoader(AbstractInsnNode afterNode, InsnList insnList, LabelNode jumpToAfter){
        InsnList toAdd = new InsnList();
        toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Methods.class), "initItemRender", "()V", false));
        toAdd.add(new JumpInsnNode(Opcodes.GOTO, jumpToAfter));
        insnList.insert(afterNode, toAdd);
        System.out.println("Injected");
    }

    private void transformRenderItem(ClassNode node){
        System.out.println("Found RenderItem");
        for (MethodNode mn : node.methods){
            if (mn.name.equals("<init>")){
                InsnList instructions = mn.instructions;
                AbstractInsnNode ain = findLabel(instructions, 87);
                if (ain != null) {
                    for (int i = 0; i < 3; i++) {
                        ain = ain.getNext();
                    }
                    for (int i = 0; i < 4; i++) {
                        ain = ain.getNext();
                        instructions.remove(ain.getPrevious());
                    }
                    //ain = ain.getNext();
                    if (ain.getPrevious().getOpcode() != Opcodes.ALOAD || ain.getOpcode() != Opcodes.PUTFIELD){
                        FMLCommonHandler.instance().exitJava(0, true);
                    }
                    instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/example/examplemod/test/ASM$Methods", "newModelMesher", "()L"+Type.getInternalName(ItemModelMesher.class)+";", false));
                    //ain = ain.getNext();
                    //instructions.remove(ain.getPrevious());
                    //instructions.insert(ain, new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/entity/RenderItem", "itemModelMesher", "Lnet/minecraft/client/renderer/ItemModelMesher"));
                }
            }
        }
        System.out.println("Transformed");
    }

    private void transformBlockRenderStuff(MethodNode mn){
        InsnList instructions = mn.instructions;
        AbstractInsnNode ain = findLabel(instructions, 520);
        if (ain != null) {
            for (int i = 0; i < 3; i++) {
                ain = ain.getNext();
            }
            for (int i = 0; i < 8; i++) {
                ain = ain.getNext();
                instructions.remove(ain.getPrevious());
            }
            if (ain.getPrevious().getOpcode() != Opcodes.ALOAD || ain.getOpcode() != Opcodes.PUTFIELD){
                FMLCommonHandler.instance().exitJava(0, true);
            }
            instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/example/examplemod/test/ASM$Methods", "newBlockRendererDispatcher", "()Lnet/minecraft/client/renderer/BlockRendererDispatcher;", false));
        }
    }

    private AbstractInsnNode replaceModelMesher(){
        return null;
    }

    public static class Methods{

        private BlockRendererDispatcher qqq;

        private void doS(){
            //Sring.valueOf("");
            qqq = newBlockRendererDispatcher();
            //String.valueOf(false);
        }

        public static void initItemRender(){
            System.out.println("SSetrenderer");
            Minecraft mc = Minecraft.getMinecraft();
            mc.renderItem = new RenderItem(mc.renderEngine, mc.modelManager);
            mc.renderManager = new RenderManager(mc.renderEngine, mc.renderItem);
            mc.itemRenderer = new ElecItemRenderer(mc);
        }

        public static ItemModelMesher newModelMesher(){
            System.out.println("Initiated Modelmesher");
            return new ElecItemModelMesher(Minecraft.getMinecraft().modelManager);
        }

        public static BlockRendererDispatcher newBlockRendererDispatcher(){
            System.out.println("Transformed BRD");
            return new ElecBlockRendererDispatcher(Minecraft.getMinecraft().modelManager.getBlockModelShapes(), Minecraft.getMinecraft().gameSettings);
        }

    }

    static {
        classTransformers = Lists.newArrayList();
    }

}
