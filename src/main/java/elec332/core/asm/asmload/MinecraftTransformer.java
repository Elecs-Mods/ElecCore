package elec332.core.asm.asmload;

import elec332.core.asm.ASMHelper;
import elec332.core.asm.ASMLoader;
import elec332.core.asm.AbstractASMClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Created by Elec332 on 26-11-2015.
 */
public class MinecraftTransformer extends AbstractASMClassTransformer {

    @Override
    public String getDeObfuscatedClassName() {
        return "net.minecraft.client.Minecraft";
    }

    /**
     * The method where you transform the class.
     *
     * @param classNode The ClassNode from the class
     * @return Whether changes were made, not checked atm.
     */
    @Override
    public boolean transformClass(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods){
            if (methodNode.exceptions.contains("org/lwjgl/LWJGLException")){
                if (!methodNode.tryCatchBlocks.isEmpty() && methodNode.tryCatchBlocks.get(0).type.equals("org/lwjgl/opengl/OpenGLException")){
                    transformSetup(methodNode);
                    transformBlockRenderStuff(methodNode);
                    return true;
                }
            }
        }
        return false;
    }

    private void transformSetup(MethodNode mn){
        InsnList instructions = mn.instructions;
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode ain = instructions.get(i);
            if (ASMHelper.isType(ain, AbstractInsnNode.LDC_INSN) && ((LdcInsnNode)ain).cst.toString().equals("Loading Item Renderer")){
                LineNumberNode line = ASMHelper.findFirstLineAfter(ain);
                if (line != null){
                    ASMHelper.removeInsnUntil(instructions, line.getNext(), line.line + 4);
                    instructions.insert(line, new MethodInsnNode(Opcodes.INVOKESTATIC, ASMLoader.getAsmHooksClassClient(), "initItemRender", "()V", false));
                    return;
                }
            }
        }
    }

    private void transformBlockRenderStuff(MethodNode mn){
        InsnList instructions = mn.instructions;
        for (int i = 0; i < instructions.size(); i++) {
            AbstractInsnNode ain = instructions.get(i);
            if (ASMHelper.isType(ain, AbstractInsnNode.LDC_INSN) && ((LdcInsnNode)ain).cst.toString().equals("Loading Entity Renderer")){
                LineNumberNode l1 = ASMHelper.findFirstLineAfter(ain);
                if (l1 != null) {
                    LabelNode label = ASMHelper.findLabelAfter(l1, l1.line+2);
                    if (label != null) {
                        AbstractInsnNode ain2 = label.getNext().getNext().getNext();
                        AbstractInsnNode ain3 = ASMHelper.removeInsnNodes(instructions, ain2, 8);
                        instructions.insertBefore(ain3, new MethodInsnNode(Opcodes.INVOKESTATIC, ASMLoader.getAsmHooksClassClient(), "newBlockRendererDispatcher", ASMHelper.getDescription("()Lnet/minecraft/client/renderer/BlockRendererDispatcher;"), false));
                        return;
                    }
                }
                throw new RuntimeException();
            }
        }
    }

}
