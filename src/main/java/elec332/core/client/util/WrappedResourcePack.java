package elec332.core.client.util;

import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 25-11-2016.
 */
@SuppressWarnings("NullableProblems") //Too lazy, its a wrapper...
public class WrappedResourcePack implements IResourcePack {

    public WrappedResourcePack(@Nonnull IResourcePack parent, @Nonnull ModContainer owner) {
        this.parent = parent;
        this.owner = owner;
    }

    private final IResourcePack parent;
    private final ModContainer owner;

    @Override
    public InputStream getResourceStream(ResourcePackType resourcePackType, ResourceLocation resourceLocation) throws IOException {
        return parent.getResourceStream(resourcePackType, resourceLocation);
    }

    @Override
    public InputStream getRootResourceStream(String s) throws IOException {
        return parent.getRootResourceStream(s);
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
        return parent.getAllResourceLocations(type, namespaceIn, pathIn, maxDepthIn, filterIn);
    }

    @Override
    public boolean resourceExists(ResourcePackType resourcePackType, ResourceLocation resourceLocation) {
        return parent.resourceExists(resourcePackType, resourceLocation);
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType resourcePackType) {
        return parent.getResourceNamespaces(resourcePackType);
    }

    @Nullable
    @Override
    public <T> T getMetadata(IMetadataSectionSerializer<T> iMetadataSectionSerializer) throws IOException {
        return parent.getMetadata(iMetadataSectionSerializer);
    }

    @Override
    public String getName() {
        return parent.getName();
    }

    @Override
    public void close() {
        parent.close();
    }

    public ModContainer getContainer() {
        return owner;
    }

}
