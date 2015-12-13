package elec332.core.asm;

import elec332.core.loader.ElecCoreLoader;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Elec332 on 26-11-2015.
 */
public class ASMHelper {

    public static String getClassName(String deobf){
        if (ElecCoreLoader.isObfuscated)
            return FMLDeobfuscatingRemapper.INSTANCE.unmap(deobf.replace('.', '/')).replace('/', '.');
        return deobf;
    }

    /**
     * Thanks to squeek502 for this method
     */
    public static String getDescription(String deobf){
        if (ElecCoreLoader.isObfuscated){
            Matcher classNameMatcher = Pattern.compile("L([^;]+);").matcher(deobf);
            StringBuffer obfDescBuffer = new StringBuffer(deobf.length());
            while (classNameMatcher.find()) {
                classNameMatcher.appendReplacement(obfDescBuffer, getDescriptor(classNameMatcher.group(1).replace('/', '.')));
            }
            classNameMatcher.appendTail(obfDescBuffer);
            return obfDescBuffer.toString();
        }
        return deobf;
    }

    public static String getDescriptor(String deobfClass){
        return "L"+getClassName(deobfClass).replace('.', '/')+";";
    }

    public static String getInternalName(Class clazz){
        return Type.getInternalName(clazz);
    }

    public static AbstractInsnNode removeInsnNodes(InsnList insnList, AbstractInsnNode start, int amount){
        AbstractInsnNode n = start;
        for (int i = 0; i < amount; i++) {
            n = n.getNext();
            insnList.remove(n.getPrevious());
        }
        return n;
    }

    @Nullable
    public static LineNumberNode removeInsnUntilNextLabel(InsnList insnList, AbstractInsnNode from){
        LineNumberNode to = findFirstLineAfter(from);
        if (to != null) {
            return removeInsnUntil(insnList, from, to.line);
        }
        throw new RuntimeException();
    }

    @Nullable
    public static LineNumberNode removeInsnUntil(InsnList insnList, AbstractInsnNode from, int untilLine){
        AbstractInsnNode nxt = from;
        while (!isLine(nxt, untilLine) && !isLine(nxt.getNext(), untilLine)){
            nxt = nxt.getNext();
            insnList.remove(nxt.getPrevious());
        }
        return (LineNumberNode) nxt.getNext();
    }

    @Nonnull
    public static MethodNode getConstructorNode(ClassNode classNode){
        for (MethodNode mn : classNode.methods){
            if (mn.name.equals("<init>"))
                return mn;
        }
        throw new RuntimeException("Class "+classNode.name+" has no constructor?!?");
    }

    @Nullable
    public static LabelNode findLabel(InsnList insnList, int line){
        return findLabelAfter(insnList.get(0), line);
    }

    @Nullable
    public static LabelNode findLabelAfter(final AbstractInsnNode from, int line) {
        AbstractInsnNode gt = from.getNext();
        while (!isLine(gt, line)) {
            if (gt == null){
                return null;
            }
            gt = gt.getNext();
        }
        return ((LineNumberNode) gt).start;
    }

    @Nullable
    public static LineNumberNode findFirstLineAfter(final AbstractInsnNode node){
        AbstractInsnNode nxt = node.getNext();
        while (!isType(nxt, AbstractInsnNode.LINE)){
            if (nxt == null){
                return null;
            }
            nxt = nxt.getNext();
        }
        return (LineNumberNode) nxt;
    }

    public static boolean isLine(AbstractInsnNode node, int line){
        return isType(node, AbstractInsnNode.LINE) && ((LineNumberNode)node).line == line;
    }

    public static boolean isType(AbstractInsnNode node, int type){
        return node != null && node.getType() == type;
    }

}
