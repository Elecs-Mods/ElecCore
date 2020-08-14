package elec332.core.handler.annotations;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import net.minecraft.data.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.*;

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
                    FMLHelper.getFMLModContainer(annotationData.deepSearchOwner(annotation)).getEventBus().addListener((Consumer<GatherDataEvent>) event -> {
                        DataRegistry dataRegistry = new DataRegistry(event);
                        generator.registerDataProviders(dataRegistry);
                        dataRegistry.modelProviders.forEach(event.getGenerator()::addProvider);
                        dataRegistry.tagProviders.forEach(event.getGenerator()::addProvider);
                        dataRegistry.dataProviders.forEach(event.getGenerator()::addProvider);
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static class DataRegistry implements AbstractDataGenerator.DataRegistry {

        private DataRegistry(GatherDataEvent event) {
            this.event = event;
            this.dataProviders = Lists.newArrayList();
            this.modelProviders = Sets.newTreeSet(Comparator.comparingInt(modelProvider -> {
                if (modelProvider instanceof BlockStateProvider) {
                    return 0;
                }
                if (modelProvider instanceof BlockModelProvider) {
                    return 1;
                }
                if (modelProvider instanceof ItemModelProvider) {
                    return 2;
                }
                return 3; //Rest doesn't matter
            }));
            this.tagProviders = Sets.newTreeSet(Comparator.comparingInt(tagProvider -> {
                if (tagProvider instanceof BlockTagsProvider) {
                    return 0;
                }
                if (tagProvider instanceof ItemTagsProvider) {
                    return 1;
                }
                return 2; //Rest doesn't matter
            }));
        }

        private final GatherDataEvent event;
        private final Collection<IDataProvider> dataProviders, modelProviders, tagProviders;

        @Override
        public void register(IDataProvider dataProvider) {
            if (isModelProvider(dataProvider)) {
                modelProviders.add(dataProvider);
            } else if (dataProvider instanceof TagsProvider) {
                tagProviders.add(dataProvider);
            } else {
                dataProviders.add(Preconditions.checkNotNull(dataProvider));
            }
        }

        @Override
        public void registerChecked(IDataProvider dataProvider) {
            if (registerChecked(dataProvider, this::isModelProvider, this::includeClient)) {
                return;
            }
            if (registerChecked(dataProvider, this::isServerProvider, this::includeServer)) {
                return;
            }
            if (registerChecked(dataProvider, this::isDevProvider, this::includeDev)) {
                return;
            }
            if (registerChecked(dataProvider, this::isReportProvider, this::includeReports)) {
                return;
            }
            register(dataProvider);
        }

        private boolean registerChecked(IDataProvider dataProvider, Predicate<IDataProvider> checker, BooleanSupplier include) {
            if (checker.test(dataProvider)) {
                if (include.getAsBoolean()) {
                    register(dataProvider);
                } else {
                    System.out.println("Skipped registration of " + dataProvider);
                }
                return true;
            }
            return false;
        }

        private boolean isModelProvider(IDataProvider dataProvider) {
            return dataProvider instanceof ModelProvider ||
                    dataProvider instanceof BlockStateProvider;
        }

        private boolean isServerProvider(IDataProvider dataProvider) {
            return dataProvider instanceof TagsProvider ||
                    dataProvider instanceof RecipeProvider ||
                    dataProvider instanceof AdvancementProvider ||
                    dataProvider instanceof LootTableProvider;
        }

        private boolean isDevProvider(IDataProvider dataProvider) {
            return false;
        }

        private boolean isReportProvider(IDataProvider dataProvider) {
            return dataProvider.getClass().getName().toLowerCase().contains("report");
        }

        @Override
        public net.minecraft.data.DataGenerator getGenerator() {
            return event.getGenerator();
        }

        @Override
        public ExistingFileHelper getExistingFileHelper() {
            return event.getExistingFileHelper();
        }

        @Override
        public boolean includeServer() {
            return event.includeServer();
        }

        @Override
        public boolean includeClient() {
            return event.includeClient();
        }

        @Override
        public boolean includeDev() {
            return event.includeDev();
        }

        @Override
        public boolean includeReports() {
            return event.includeReports();
        }

    }

}
