package elec332.core.loader;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.EnumSet;

/**
 * Created by Elec332 on 30-6-2020
 */
public class WorldEventListenerHook implements ILaunchPluginService, Opcodes {

    private final Type type = Type.getType("Lnet/minecraft/world/server/ServerWorld;");
    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public String name() {
        return "markBlockChangedHook";
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return !isEmpty && classType.equals(type) ? YAY : NAY;
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType) {
        if (classType.equals(type)) {
            transformServerChunkProvider(classNode);
            return true;
        }
        return false;
    }

    private void transformServerChunkProvider(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.desc.equals("(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V")) { // BlockPos, BlockPos, BlockState, int
                InsnList extraInstructions = new InsnList();
                extraInstructions.add(new VarInsnNode(ALOAD, 0));
                extraInstructions.add(new VarInsnNode(ALOAD, 1));
                extraInstructions.add(new VarInsnNode(ALOAD, 2));
                extraInstructions.add(new VarInsnNode(ALOAD, 3));
                extraInstructions.add(new MethodInsnNode(INVOKESTATIC, "elec332/core/loader/WorldEventListenerHook", "markBlockChanged", "(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V"));

                methodNode.instructions.insert(extraInstructions);
            }
        }
    }

    public static void markBlockChanged(IWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        WorldGenManager.INSTANCE.markBlockChanged(world, pos, oldState, newState);
    }

}
