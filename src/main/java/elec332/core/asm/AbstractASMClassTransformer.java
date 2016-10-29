package elec332.core.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by Elec332 on 26-11-2015.
 *
 * Unused
 */
@SuppressWarnings("unused")
public abstract class AbstractASMClassTransformer implements IASMClassTransformer {

    @Override
    public abstract String getDeObfuscatedClassName();

    @Override
    public byte[] transformClass(byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(node, 0);
        transformClass(node);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    /**
     * The method where you transform the class.
     *
     * @param classNode The ClassNode from the class
     * @return Whether changes were made, not checked atm.
     */
    public abstract boolean transformClass(ClassNode classNode);

}
