package elec332.core.api.client.model.template;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Created by Elec332 on 11-3-2016.
 */
@OnlyIn(Dist.CLIENT)
public interface IQuadProviderTemplate {

    List<IQuadTemplate> getGeneralQuads();

    IQuadTemplateSidedMap getSidedQuads();

}
