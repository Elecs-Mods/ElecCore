package elec332.core.api.client.model;

import elec332.core.api.client.ITextureLoader;

/**
 * Created by Elec332 on 21-11-2015.
 * <p>
 * A combination of {@link IModelLoader} and {@link ITextureLoader}
 * Needs to be registered by calling
 * {@link IElecRenderingRegistry#registerLoader(IModelAndTextureLoader)}
 */
public interface IModelAndTextureLoader extends IModelLoader, ITextureLoader {

}
