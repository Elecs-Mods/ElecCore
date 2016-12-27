package elec332.core.abstraction;

import com.google.common.primitives.Primitives;
import elec332.core.abstraction.abstracted.IAbstractionType;
import elec332.core.util.ASMHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import java.lang.reflect.Constructor;

/**
 * Created by Elec332 on 22-12-2016.
 */
public class AbstractionHandler {

    //todo: ItemArmor, ItemAxe, itemblock, itembow, itemfood, itemhoe, itemrecord, itemword, itemtool

    public static  <T, I extends IForgeRegistryEntry<I>> I registerAbstractionObject(T item, ResourceLocation name, IAbstractionType<T, I> type){
        return registerAbstractionObject(item, name, type, null);
    }

    public static <T, I extends IForgeRegistryEntry<I>> I registerAbstractionObject(T item, ResourceLocation name, IAbstractionType<T, I> type, Class<? extends I> baseTypeP, Object... params){
        if (!type.getAbstractionType().isInstance(item)){
            throw new IllegalArgumentException();
        }
        Class<? extends I> baseType = baseTypeP == null ? type.getBaseType() : baseTypeP;
        Object[] constructorParams = (params == null || params.length == 0) ? type.getParams(item) : params;
        for (Class<?> c : item.getClass().getInterfaces()){
            if (!(c == type.getAbstractionType() || c.isAssignableFrom(type.getAbstractionType())) && c.isAssignableFrom(type.getAbstractionType())){
                throw new IllegalArgumentException();
            }
        }
        Class<? extends I> clazz = ASMHelper.makeImplementInterfaces(baseType, item.getClass(), type.getClassModifier(), type.getAbstractionType(), type.getInterfaceExclusion(), type.getAbstractionType());
        I ret;
        try {
            Class[] ctorz = new Class[constructorParams.length + 1];
            Object[] objz = new Object[constructorParams.length + 1];
            ctorz[0] = Object.class;
            objz[0] = item;
            for (int i = 0; i < constructorParams.length; i++) {
                ctorz[i + 1] = Primitives.unwrap(constructorParams[i].getClass());
                objz[i + 1] = constructorParams[i];
            }
            Constructor<? extends I> cTor = clazz.getDeclaredConstructor(ctorz);
            ret = cTor.newInstance(objz);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        ret.setRegistryName(name);
        GameRegistry.register(ret);
        return ret;
    }

}
