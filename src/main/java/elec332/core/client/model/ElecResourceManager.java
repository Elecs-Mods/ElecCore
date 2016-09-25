package elec332.core.client.model;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 26-12-2015.
 */
@SideOnly(Side.CLIENT)
public final class ElecResourceManager extends SimpleReloadableResourceManager implements IElecResourceManager {

    public ElecResourceManager(SimpleReloadableResourceManager resourceManager) {
        super(resourceManager.rmMetadataSerializer);
        this.resourceManager = resourceManager;
        this.hooks = Lists.newArrayList();
        this.setResourceDomains = resourceManager.setResourceDomains;
        this.reloadListeners = resourceManager.reloadListeners;
        this.domainResourceManagers = resourceManager.domainResourceManagers;
    }

    private final SimpleReloadableResourceManager resourceManager;
    private final List<IElecResourceManager.IResourceHook> hooks;

    public void addListenHook(IElecResourceManager.IResourceHook hook){
        hooks.add(hook);
    }

    @Override
    public void registerReloadListener(@Nonnull IResourceManagerReloadListener reloadListener) {
        boolean register = true;
        for (IElecResourceManager.IResourceHook hook : hooks){
            if (!hook.onRegister(this, reloadListener)){
                register = false;
            }
        }
        if (register) {
            resourceManager.registerReloadListener(reloadListener);
        }
    }

    /* Link-through */

    @Override
    public void reloadResources(@Nonnull List<IResourcePack> resourcePacks) {
        resourceManager.reloadResources(resourcePacks);
    }

    @Override
    public Set<String> getResourceDomains() {
        return resourceManager.getResourceDomains();
    }

    @Override
    public IResource getResource(@Nonnull ResourceLocation location) throws IOException {
        return resourceManager.getResource(location);
    }

    @Override
    public List<IResource> getAllResources(@Nonnull ResourceLocation location) throws IOException {
        return resourceManager.getAllResources(location);
    }

    @Override
    public void reloadResourcePack(IResourcePack resourcePack) {
        resourceManager.reloadResourcePack(resourcePack);
    }

}
