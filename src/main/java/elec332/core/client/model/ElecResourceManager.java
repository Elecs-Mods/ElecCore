package elec332.core.client.model;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;

import java.util.List;

/**
 * Created by Elec332 on 26-12-2015.
 */
public final class ElecResourceManager extends SimpleReloadableResourceManager {

    public ElecResourceManager(SimpleReloadableResourceManager resourceManager) {
        super(resourceManager.rmMetadataSerializer);
        this.domainResourceManagers = resourceManager.domainResourceManagers;
        this.reloadListeners = resourceManager.reloadListeners;
        this.setResourceDomains = resourceManager.setResourceDomains;

        this.hooks = Lists.newArrayList();
    }

    private final List<IResourceHook> hooks;

    public void addListenHook(IResourceHook hook){
        hooks.add(hook);
    }

    @Override
    public void registerReloadListener(IResourceManagerReloadListener reloadListener) {
        boolean register = true;
        for (IResourceHook hook : hooks){
            if (!hook.onRegister(this, reloadListener)){
                register = false;
            }
        }
        if (register) {
            System.out.println("registering: "+reloadListener.getClass());
            super.registerReloadListener(reloadListener);
        } else {
            System.out.println("Not registering: "+reloadListener.getClass());
        }
    }

    public interface IResourceHook {

        public boolean onRegister(IReloadableResourceManager resourceManager, IResourceManagerReloadListener listener);

    }

}
