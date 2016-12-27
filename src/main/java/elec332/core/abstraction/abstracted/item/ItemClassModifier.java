package elec332.core.abstraction.abstracted.item;

import elec332.core.abstraction.IItem;
import elec332.core.abstraction.abstracted.AbstractClassModifier;
import net.minecraft.item.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by Elec332 on 23-12-2016.
 */
public class ItemClassModifier extends AbstractClassModifier {

    public ItemClassModifier(Class<? extends Item> original){
        this.original = original;
    }

    public static final ItemClassModifier DEFAULT = new ItemClassModifier(AbstractedItem.class);

    private final Class<? extends Item> original;

    @Override
    public ClassWriter modifyClass(String internalClassName, ClassWriter cw, Object... additionalData) {
        copyMethodsFrom(internalClassName, cw, original, IMethodModifier.combine(new MethodFixer(original)));
        copyFieldsFrom(internalClassName, cw, original, null);
        if (this == DEFAULT) {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, AbstractedItem.GET_ITEM_METHOD, "()" + Type.getDescriptor(IItem.class), null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, internalClassName, "instance", Type.getDescriptor(Object.class));
            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(IItem.class));
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        return cw;
    }

}
