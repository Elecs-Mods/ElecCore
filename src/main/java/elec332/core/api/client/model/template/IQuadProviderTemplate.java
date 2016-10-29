package elec332.core.api.client.model.template;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Elec332 on 11-3-2016.
 */
@SideOnly(Side.CLIENT)
public interface IQuadProviderTemplate {

    public List<IQuadTemplate> getGeneralQuads();

    public IQuadTemplateSidedMap getSidedQuads();

}
