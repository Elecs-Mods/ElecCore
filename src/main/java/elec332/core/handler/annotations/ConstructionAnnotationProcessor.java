package elec332.core.handler.annotations;

import com.google.common.base.Strings;
import elec332.core.ElecCore;
import elec332.core.api.discovery.AnnotationDataProcessor;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.discovery.IAnnotationDataProcessor;
import elec332.core.api.mod.SidedProxy;
import elec332.core.api.registration.DataGenerator;
import elec332.core.api.util.IMemberPointer;
import elec332.core.data.AbstractDataGenerator;
import elec332.core.util.FMLHelper;
import elec332.core.util.FieldPointer;
import elec332.core.util.MethodPointer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 2-1-2019
 */
@AnnotationDataProcessor(ModLoadingStage.CONSTRUCT)
public class ConstructionAnnotationProcessor implements IAnnotationDataProcessor {

    @Override
    public void processASMData(final IAnnotationDataHandler annotationData, ModLoadingStage state) {
        ElecCore.logger.info("Injecting proxies...");
        FMLHelper.getMods().forEach(mod -> {
            Function<Type, Set<IAnnotationData>> modData = annotationData.getAnnotationsFor(mod);
            Set<IAnnotationData> proxies = modData.apply(Type.getType(SidedProxy.class));
            proxies.forEach(ann -> {

                final Map<String, Object> annotationInfo = ann.getAnnotationInfo();
                Supplier<String> modIdGetter = () -> {
                    String modId = (String) annotationInfo.get("modId");
                    if (Strings.isNullOrEmpty(modId)) {
                        modId = annotationData.deepSearchOwnerName(ann);
                        if (Strings.isNullOrEmpty(modId)) {
                            throw new RuntimeException("Unable to determine owner of " + ann.getClassName());
                        }
                    }
                    return modId;
                };
                Consumer<Object> injector;
                IMemberPointer<?, ?, ?> member;
                if (ann.isField()) {
                    final FieldPointer<Object, Object> proxyField = new FieldPointer<>(ann.getField());
                    if (proxyField.isStatic()) {
                        injector = p -> proxyField.set(null, p);
                    } else {
                        injector = p -> proxyField.set(FMLHelper.getModList().getModObjectById(modIdGetter.get()).orElseThrow(NullPointerException::new), p);
                    }
                    member = proxyField;
                } else if (ann.isField()) {
                    final MethodPointer<Object, Void> proxyMethod = new MethodPointer<>(ann.getMethod());
                    if (proxyMethod.isStatic()) {
                        injector = p -> proxyMethod.invoke(null, p);
                    } else {
                        injector = p -> proxyMethod.invoke(FMLHelper.getModList().getModObjectById(modIdGetter.get()).orElseThrow(NullPointerException::new), p);
                    }
                    member = proxyMethod;
                } else {
                    throw new IllegalArgumentException();
                }
                String proxyClass = FMLHelper.getLogicalSide() == LogicalSide.CLIENT ? member.getAnnotation(SidedProxy.class).clientSide() : member.getAnnotation(SidedProxy.class).serverSide();
                Object proxy;
                try {
                    proxy = FMLHelper.loadClass(proxyClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (mod instanceof FMLModContainer) {
                    ((FMLModContainer) mod).getEventBus().register(proxy);
                }
                MinecraftForge.EVENT_BUS.register(proxy);
                ElecCore.logger.info("Injecting proxy into " + member.getName() + "@" + member.getParentType().getCanonicalName());
                injector.accept(proxy);
            });

        });

        annotationData.getAnnotationList(DataGenerator.class).forEach(annotation -> {
            Class<?> clazz = annotation.tryLoadClass();
            if (clazz != null && AbstractDataGenerator.class.isAssignableFrom(clazz)) {
                try {
                    AbstractDataGenerator generator = (AbstractDataGenerator) clazz.newInstance();
                    FMLHelper.getFMLModContainer(annotationData.deepSearchOwner(annotation)).getEventBus().addListener(AbstractDataGenerator.toEventListener(generator));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
