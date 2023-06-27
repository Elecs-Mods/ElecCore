package elec332.core.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.api.data.IDataGenerator;
import elec332.core.util.FieldPointer;
import net.minecraft.data.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.*;

/**
 * Created by Elec332 on 20-8-2020
 */
public class DataGeneratorWrapper implements Consumer<GatherDataEvent> {

    public static Consumer<GatherDataEvent> toEventListener(IDataGenerator dataGenerator) {
        return new DataGeneratorWrapper(dataGenerator);
    }

    public static Consumer<GatherDataEvent> withDataConfig(BiConsumer<GatherDataEvent, GatherDataEvent.DataGeneratorConfig> consumer) {
        return event -> consumer.accept(event, CONFIG_GETTER.apply(event));
    }

    DataGeneratorWrapper(IDataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    private static final Function<GatherDataEvent, GatherDataEvent.DataGeneratorConfig> CONFIG_GETTER;
    private final IDataGenerator dataGenerator;

    @Override
    public void accept(GatherDataEvent event) {
        DataRegistry dataRegistry = new DataRegistry(event);
        dataGenerator.registerDataProviders(dataRegistry);
        dataRegistry.modelProviders.forEach(event.getGenerator()::addProvider);
        dataRegistry.tagProviders.forEach(event.getGenerator()::addProvider);
        dataRegistry.dataProviders.forEach(event.getGenerator()::addProvider);
    }

    private static class DataRegistry implements IDataGenerator.Registry {

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

    static {
        FieldPointer<GatherDataEvent, GatherDataEvent.DataGeneratorConfig> fp = new FieldPointer<>(GatherDataEvent.class, "config");
        CONFIG_GETTER = fp::get;
    }

}
