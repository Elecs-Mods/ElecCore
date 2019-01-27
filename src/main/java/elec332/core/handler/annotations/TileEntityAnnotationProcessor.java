package elec332.core.handler.annotations;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.util.FMLHelper;
import elec332.core.util.FieldPointer;
import elec332.core.util.ObjectReference;
import elec332.core.util.RegistryHelper;
import elec332.core.util.function.FuncHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 27-1-2019
 */
public class TileEntityAnnotationProcessor implements IObjectRegister<TileEntityType<?>> {

    @APIHandlerInject
    private static IAnnotationDataHandler asmData;
    private static final Map<Class, TileEntityType> typeReference = Maps.newIdentityHashMap();

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> TileEntityType<T> getTileType(Class<T> clazz){
        return typeReference.get(clazz);
    }

    @Override
    public void register(IForgeRegistry<TileEntityType<?>> registry) {
        asmData.getAnnotationList(RegisteredTileEntity.class).forEach(this::accept);
    }

    @SuppressWarnings("all")
    private void accept(IAnnotationData data) {
        String name = (String) data.getAnnotationInfo().get("value");
        try {
            Class<? extends TileEntity> clazz = null;
            Field f = null;
            if (data.isField()) {
                f = data.getField();
                if (f.getGenericType() instanceof ParameterizedType && ((ParameterizedType) f.getGenericType()).getRawType() == TileEntityType.class) {
                    java.lang.reflect.Type[] t = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
                    if (t.length == 1) {
                        clazz = (Class<? extends TileEntity>) t[0];
                    }
                }
            } else {
                clazz = (Class<? extends TileEntity>) Class.forName(data.getClassName());
            }
            if (!name.contains(":")) {
                String mod = (String) data.getAnnotationInfo().get("mod");
                if (Strings.isNullOrEmpty(mod)) {
                    mod = FMLHelper.getOwnerName(clazz);
                }
                name = mod + ":" + name;
            }
            ObjectReference<TileEntityType<?>> ref = new ObjectReference<>();
            ref.set(RegistryHelper.getTileEntities().getValue(new ResourceLocation(name)));

            if (ref.get() == null) {
                Supplier<? extends TileEntity> s = null;
                try {
                    Constructor<? extends TileEntity> c = clazz.getDeclaredConstructor(TileEntityType.class);
                    s = FuncHelper.safeSupplier(() -> c.newInstance(ref.get()));
                } catch (Exception e) {
                    //
                }
                if (s == null) {
                    try {
                        Constructor<? extends TileEntity> c = clazz.getDeclaredConstructor();
                        s = FuncHelper.safeSupplier(() -> {
                            TileEntity ret = c.newInstance();
                            if (ret instanceof RegisteredTileEntity.TypeSetter) {
                                ((RegisteredTileEntity.TypeSetter) ret).setTileEntityType(ref.get());
                            }
                            return ret;
                        });
                    } catch (Exception e) {
                        //
                    }
                }
                if (s == null) {
                    throw new RuntimeException();
                }
                TileEntityType tt = new TileType(s, clazz);
                typeReference.put(clazz, tt);
                ref.set(RegistryHelper.registerTileEntity(new ResourceLocation(name), tt));
            }
            if (f != null) {
                TileEntityType<?> type = ref.get();
                if (type instanceof TileType && ((TileType) type).getTileType() != clazz){
                    throw new RuntimeException("Field type mismatch!");
                }
                new FieldPointer(f).set(null, type);
            }
        } catch (Exception e) {
            ElecCore.logger.error("Error registering tile: " + name, e);
        }
    }

    private class TileType<T extends TileEntity> extends TileEntityType<T>{

        @SuppressWarnings("all")
        private TileType(Supplier<? extends T> factoryIn, Class<T> clazz) {
            super(factoryIn, null);
            this.clazz = clazz;
        }

        private final Class<T> clazz;

        private Class<T> getTileType(){
            return clazz;
        }

    }

}
