package elec332.core.compat.forestry.bee;

import elec332.core.asm.ASMTransformer;
import elec332.core.asm.AbstractASMClassTransformer;
import elec332.core.util.MCVersion;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Created by Elec332 on 17-6-2017.
 */
@ASMTransformer
public class ForestryCompatFixer extends AbstractASMClassTransformer {

    private static final String owner = "forestry/api/apiculture/IHiveDrop";
    private static final String name = "getExtraItems";
    private static final String newDesc = "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;I)Ljava/util/List;";

    @Override
    public String getDeObfuscatedClassName() {
        return "elec332.core.compat.forestry.bee.IHiveEnum";
    }

    @Override
    public boolean transformClass(ClassNode classNode) {
        if (!MCVersion.getCurrentVersion().isLowerThan(MCVersion.MC_1_11)){
            return false;
        }
        for (MethodNode mn : classNode.methods){
            InsnList insnList = mn.instructions;
            for (int i = 0; i < insnList.size(); i++) {
                AbstractInsnNode ain = insnList.get(i);
                if (ain.getOpcode() == Opcodes.INVOKEINTERFACE){
                    MethodInsnNode min = (MethodInsnNode) ain;
                    if (min.owner.equals(owner) && min.name.equals(name)){
                        min.desc = newDesc;
                    }
                }
            }
        }
        return true;
    }

}
