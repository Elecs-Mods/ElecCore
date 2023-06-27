package elec332.core.data.custom;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import joptsimple.internal.Strings;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by Elec332 on 28-7-2020
 */
public abstract class AbstractDataProvider implements IDataProvider {

    public AbstractDataProvider(DataGenerator generatorIn) {
        this(generatorIn, "");
    }

    public AbstractDataProvider(DataGenerator generatorIn, String folder) {
        this.generator = generatorIn;
        this.folder = folder;
    }

    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    protected final DataGenerator generator;
    private final String folder;

    @Override
    public void act(@Nonnull DirectoryCache cache) {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        registerObjects((rl, obj) -> {
            if (!set.add(Preconditions.checkNotNull(rl))) {
                throw new IllegalStateException("Duplicate object: " + rl);
            }
            String folder = Strings.isNullOrEmpty(this.folder) ? "" : this.folder + "/";
            Path jsonPath = path.resolve(getType().toString() + "/" + rl.getNamespace() + "/" + folder + rl.getPath() + ".json");
            try {
                String s = GSON.toJson(Preconditions.checkNotNull(obj));
                @SuppressWarnings("UnstableApiUsage")
                String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
                if (!Objects.equals(cache.getPreviousHash(jsonPath), s1) || !Files.exists(jsonPath)) {
                    Files.createDirectories(jsonPath.getParent());

                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(jsonPath)) {
                        bufferedwriter.write(s);
                    }
                }

                cache.recordHash(jsonPath, s1);
            } catch (IOException ioexception) {
                System.out.println("Couldn't save object " + jsonPath);
                ioexception.printStackTrace(System.out);
            }
        });
    }

    public abstract void registerObjects(BiConsumer<ResourceLocation, JsonObject> registry);

    @Nonnull
    @Override
    public final String getName() {
        return providerName();
    }

    @Nonnull
    public abstract String providerName();

    protected Type getType() {
        return Type.DATA;
    }

    public enum Type {

        ASSETS("assets"),
        DATA("data");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
