package elec332.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IDataProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 6-8-2020
 */
public abstract class AbstractDataGenerator {

    public static Consumer<GatherDataEvent> toEventListener(AbstractDataGenerator dataGenerator) {
        return new DataGeneratorWrapper(dataGenerator);
    }

    public abstract void registerDataProviders(DataRegistry registry);

    public interface DataRegistry {

        void register(IDataProvider dataProvider);

        default void register(Function<net.minecraft.data.DataGenerator, IDataProvider> dataProvider) {
            register(dataProvider.apply(getGenerator()));
        }

        default void register(BiFunction<net.minecraft.data.DataGenerator, ExistingFileHelper, IDataProvider> dataProvider) {
            register(gen -> dataProvider.apply(gen, getExistingFileHelper()));
        }

        void registerChecked(IDataProvider dataProvider);

        default void registerChecked(Function<net.minecraft.data.DataGenerator, IDataProvider> dataProvider) {
            registerChecked(dataProvider.apply(getGenerator()));
        }

        default void registerChecked(BiFunction<net.minecraft.data.DataGenerator, ExistingFileHelper, IDataProvider> dataProvider) {
            registerChecked(gen -> dataProvider.apply(gen, getExistingFileHelper()));
        }

        DataGenerator getGenerator();

        ExistingFileHelper getExistingFileHelper();

        boolean includeServer();

        boolean includeClient();

        boolean includeDev();

        boolean includeReports();

    }

}
