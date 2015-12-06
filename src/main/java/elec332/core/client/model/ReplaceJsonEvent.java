package elec332.core.client.model;

import elec332.core.client.model.template.ElecTemplateBakery;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Elec332 on 21-11-2015.
 */
public class ReplaceJsonEvent extends Event {

    public ReplaceJsonEvent(ElecQuadBakery quadBakery, ElecModelBakery modelBakery, ElecTemplateBakery templateBakery){
        this.quadBakery = quadBakery;
        this.modelBakery = modelBakery;
        this.templateBakery = templateBakery;
    }

    public final ElecQuadBakery quadBakery;
    public final ElecModelBakery modelBakery;
    public final ElecTemplateBakery templateBakery;

}
