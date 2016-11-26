package elec332.core.client.util;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainerHolder;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by Elec332 on 25-11-2016.
 */
public class WrappedResourcePack implements IResourcePack, FMLContainerHolder {

    public WrappedResourcePack(@Nonnull IResourcePack parent, @Nonnull ModContainer owner){
        this.parent = parent;
        this.owner = owner;
    }

    private final IResourcePack parent;
    private final ModContainer owner;

    @Override
    @Nonnull
    public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
        return parent.getInputStream(location);
    }

    @Override
    public boolean resourceExists(@Nonnull ResourceLocation location) {
        return parent.resourceExists(location);
    }

    @Override
    @Nonnull
    public Set<String> getResourceDomains() {
        return parent.getResourceDomains();
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(@Nonnull MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return parent.getPackMetadata(metadataSerializer, metadataSectionName);
    }

    @Override
    @Nonnull
    public BufferedImage getPackImage() throws IOException {
        return parent.getPackImage();
    }

    @Override
    @Nonnull
    public String getPackName() {
        return parent.getPackName();
    }

    @Override
    public ModContainer getFMLContainer() {
        return owner;
    }

}
