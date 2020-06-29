package elec332.core.handler.annotations;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.registration.IItemRegister;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.util.FMLHelper;
import elec332.core.util.FieldPointer;
import elec332.core.util.ObjectReference;
import elec332.core.util.RegistryHelper;
import elec332.core.util.function.FuncHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 27-1-2019
 */
public class TileEntityAnnotationProcessor implements IItemRegister {

    @APIHandlerInject
    private static IAnnotationDataHandler asmData;
    private static final Map<Class<?>, TileEntityType<?>> typeReference = Maps.newIdentityHashMap();
    private static final Map<String, Set<Runnable>> toRegister = Maps.newHashMap();

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> TileEntityType<T> getTileType(Class<T> clazz) {
        return (TileEntityType<T>) typeReference.get(clazz);
    }

    @Override
    public void register(IForgeRegistry<Item> registry) { //Because items are registered before tiles
        asmData.getAnnotationList(RegisteredTileEntity.class).forEach(this::accept);
        FMLModContainer elecCore = (FMLModContainer) FMLHelper.getActiveModContainer();
        toRegister.keySet().forEach(name -> {
            FMLModContainer reg;
            ModContainer mc = FMLHelper.findMod(name);
            if (FMLHelper.hasFMLModContainer(mc)) {
                reg = FMLHelper.getFMLModContainer(mc);
            } else {
                reg = elecCore;
            }
            reg.getEventBus().addGenericListener(TileEntityType.class, (Consumer<RegistryEvent.Register<TileEntityType<?>>>) evt -> {
                Set<Runnable> toRun = toRegister.remove(name);
                toRun.forEach(Runnable::run);
            });
        });
    }

    @SuppressWarnings("all")
    private void accept(IAnnotationData data) {
        String name = (String) data.getAnnotationInfo().get("value");
        try {
            Class<? extends TileEntity> clazz_ = null;
            Field f;
            if (data.isField()) {
                f = data.getField();
                if (f.getGenericType() instanceof ParameterizedType && ((ParameterizedType) f.getGenericType()).getRawType() == TileEntityType.class) {
                    java.lang.reflect.Type[] t = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
                    if (t.length == 1) {
                        clazz_ = (Class<? extends TileEntity>) t[0];
                    }
                }
            } else {
                clazz_ = (Class<? extends TileEntity>) Class.forName(data.getClassName());
                f = null;
            }
            final Class<? extends TileEntity> clazz = clazz_;
            name = checkName(name, (String) data.getAnnotationInfo().get("mod"), clazz);

            final String finalName = name;
            toRegister.computeIfAbsent(name.split(":")[0], o -> Sets.newHashSet()).add(() -> {
                ObjectReference<TileEntityType<?>> ref = new ObjectReference<>();
                ref.set(RegistryHelper.getTileEntities().getValue(new ResourceLocation(finalName)));

                if (ref.get() == null) {
                    registerTileEntity(clazz, new ResourceLocation(finalName), ref);
                }
                if (f != null) {
                    TileEntityType<?> type = ref.get();
                    if (type instanceof TileType && ((TileType) type).getTileType() != clazz) {
                        throw new RuntimeException("Field type mismatch!");
                    }
                    new FieldPointer(f).set(null, type);
                }
            });
        } catch (Exception e) {
            ElecCore.logger.error("Error pre-registering tile: " + name, e);
        }
    }

    public static String checkName(String name, String mod, Class<? extends TileEntity> clazz) {
        if (!name.contains(":")) {
            if (Strings.isNullOrEmpty(mod)) {
                mod = FMLHelper.getOwnerName(clazz);
            }
            name = mod + ":" + name;
        }
        return name;
    }

    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(Class<T> clazz, ResourceLocation rl) {
        return registerTileEntity(clazz, rl, new ObjectReference<>());
    }

    private static <T extends TileEntity> TileEntityType<T> registerTileEntity(Class<T> clazz, ResourceLocation rl, ObjectReference<TileEntityType<?>> ref) {
        @SuppressWarnings("rawtypes")
        Supplier s = null;
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
        @SuppressWarnings("unchecked")
        TileEntityType<T> tt = new TileType<T>(s, clazz);
        typeReference.put(clazz, tt);
        ref.set(tt);
        return RegistryHelper.registerTileEntity(rl, tt);
    }

    private static class TileType<T extends TileEntity> extends TileEntityType<T> {

        @SuppressWarnings("all")
        private TileType(Supplier<? extends T> factoryIn, Class<T> clazz) {
            super(factoryIn, null, null);
            this.clazz = clazz;
        }

        private final Class<T> clazz;

        private Class<T> getTileType() {
            return clazz;
        }

        @Override
        public boolean isValidBlock(@Nonnull Block block) {
            return true;
        }

    }

}
