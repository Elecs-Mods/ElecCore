package elec332.core.client.util;

import com.google.common.collect.Sets;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 27-12-2019
 */
public abstract class InternalResourcePack implements IResourcePack {

    public InternalResourcePack(String name, String nameSpace) {
        this(name, Collections.singletonList(nameSpace));
    }

    public InternalResourcePack(String name, Collection<String> nameSpaces) {
        this.name = name;
        this.nameSpaces = Sets.newHashSet(nameSpaces);
    }

    private final String name;
    private final Set<String> nameSpaces;

    @Nonnull
    @Override
    public abstract InputStream getResourceStream(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location);

    @Override
    public abstract boolean resourceExists(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location);

    @Nonnull
    @Override
    public InputStream getRootResourceStream(@Nonnull String fileName) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getAllResourceLocations(@Nonnull ResourcePackType type, @Nonnull String nameSpace, @Nonnull String pathIn, int maxDepth, @Nonnull Predicate<String> filter) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Set<String> getResourceNamespaces(@Nonnull ResourcePackType type) {
        return nameSpaces;
    }

    @Nullable
    @Override
    public <T> T getMetadata(@Nonnull IMetadataSectionSerializer<T> deserializer) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() {
    }

}
