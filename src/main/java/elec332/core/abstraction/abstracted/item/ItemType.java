package elec332.core.abstraction.abstracted.item;

import elec332.core.abstraction.*;
import elec332.core.abstraction.abstracted.IAbstractionType;
import elec332.core.util.ASMHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBlock;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nullable;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by Elec332 on 23-12-2016.
 */
public enum ItemType implements IAbstractionType<IItem, Item> {

    ITEM(IItem.class, Item.class, null, null),
    ARROW(IItemArrow.class, ItemArrow.class, AbstractedItemArrow.class, null),
    ARMOR(IItemArmor.class, ItemArmor.class, AbstractedItemArmor.class, null) {
        @Override
        public Object[] getParams(IItem iItem) {
            super.getParams(iItem);
            return new Object[]{
                    ((IItemArmor) iItem).getArmorMaterial(), ((IItemArmor) iItem).getRenderIndex(), ((IItemArmor) iItem).getEquipmentSlot()
            };
        }

    },
    ITEMBLOCK(IItemBlock.class, ItemBlock.class, AbstractedItemBlock.class, new ASMHelper.IClassModifier() {

        @Override
        public ClassWriter modifyClass(String internalClassName, ClassWriter cw, Object... additionalData) {
            ClassNode cn = new ClassNode(ASM5);
            ClassReader cr = new ClassReader(cw.toByteArray());
            cr.accept(cn, 0);
            for (MethodNode mn : cn.methods) {
                if (mn.name.equals("<init>")) {
                    mn.instructions.remove(mn.instructions.getLast());
                    mn.visitVarInsn(ALOAD, 0);
                    mn.visitVarInsn(ALOAD, 0);
                    mn.visitFieldInsn(GETFIELD, internalClassName, "instance", Type.getDescriptor(Object.class));
                    mn.visitTypeInsn(CHECKCAST, Type.getInternalName(IItemBlock.class));
                    mn.visitMethodInsn(INVOKESTATIC, Type.getInternalName(DefaultInstances.class), "getDefaultBlockPlacementBehaviour", "(" + Type.getDescriptor(IItemBlock.class) + ")" + Type.getDescriptor(IItemBlock.IDefaultBlockPlaceBehaviour.class), false);
                    mn.visitFieldInsn(PUTFIELD, internalClassName, "defaultBlockPlaceBehaviour", Type.getDescriptor(IItemBlock.IDefaultBlockPlaceBehaviour.class));
                    mn.visitInsn(RETURN);
                    mn.maxStack += 2;
                    mn.maxLocals += 2;
                }
            }
            ClassWriter ret = new ClassWriter(0);
            cn.accept(ret);
            return ret;
        }

    }) {
        @Override
        public Object[] getParams(IItem item) {
            super.getParams(item);
            return new Object[]{
                    ((IItemBlock) item).getBlock()
            };
        }

    };

    <V extends Item & IAbstractedItem<T>, I extends Item, T extends IItem> ItemType(Class<T> itemType, Class<I> baseType, @Nullable Class<V> copyType, @Nullable ASMHelper.IClassModifier modifier, Object... params) {
        this.itemType = itemType;
        this.baseType = baseType;
        this.copyType = copyType;
        if (copyType != null && !baseType.isAssignableFrom(copyType)) {
            throw new IllegalArgumentException();
        }
        this.params = params == null ? new Object[0] : params;
        this.modifier = modifier;
    }

    private final Class<? extends IItem> itemType;
    private final Class<? extends Item> baseType, copyType;
    private final ASMHelper.IClassModifier modifier;
    private final Object[] params;

    @Override
    public Class<? extends IItem> getAbstractionType() {
        return itemType;
    }

    @Override
    public Class<? extends Item> getBaseType() {
        return baseType;
    }

    @Override
    public Object[] getParams(IItem item) {
        if (item == null || !(itemType.isInstance(item))) {
            throw new IllegalArgumentException();
        }
        return params;
    }

    @Override
    public ASMHelper.IClassModifier getClassModifier() {
        return ASMHelper.IClassModifier.combine(ItemClassModifier.DEFAULT, (copyType == null ? null : new ItemClassModifier(copyType)), modifier);
    }

    @Override
    public Class<IItem> getBaseAbstractionType() {
        return IItem.class;
    }

    @Override
    public Class<?> getInterfaceExclusion() {
        return IAbstractedItem.class;
    }

}
