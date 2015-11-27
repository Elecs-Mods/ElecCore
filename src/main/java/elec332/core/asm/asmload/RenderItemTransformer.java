package elec332.core.asm.asmload;

import elec332.core.asm.ASMHelper;
import elec332.core.asm.ASMLoader;
import elec332.core.asm.AbstractASMClassTransformer;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

/**
 * Created by Elec332 on 26-11-2015.
 */
public class RenderItemTransformer extends AbstractASMClassTransformer {

    @Override
    public String getDeObfuscatedClassName() {
        return "net.minecraft.client.renderer.entity.RenderItem";
    }

    /**
     * The method where you transform the class.
     *
     * @param classNode The ClassNode from the class
     * @return Whether changes were made, not checked atm.
     */
    @Override
    public boolean transformClass(ClassNode classNode) {
        MethodNode mn = ASMHelper.getConstructorNode(classNode);
        InsnList instructions = mn.instructions;
        AbstractInsnNode ain = instructions.getFirst();
        while (!ASMHelper.isType(ain, AbstractInsnNode.TYPE_INSN) || ain.getOpcode() != Opcodes.NEW){
            if (ain == null)
                throw new RuntimeException();
            ain = ain.getNext();
        }
        ain = ASMHelper.removeInsnNodes(instructions, ain, 4);
        instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, ASMLoader.getAsmHooksClassClient(), "newModelMesher", ASMHelper.getDescription("()Lnet/minecraft/client/renderer/ItemModelMesher;"), false));
        return true;
    }

}
