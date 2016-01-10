package elec332.core.client.model;

import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 27-12-2015.
 */
@SideOnly(Side.CLIENT)
public interface IElecResourceManager extends IReloadableResourceManager {

    public void addListenHook(IElecResourceManager.IResourceHook hook);

    public interface IResourceHook {

        public boolean onRegister(IReloadableResourceManager resourceManager, IResourceManagerReloadListener listener);

    }

}
