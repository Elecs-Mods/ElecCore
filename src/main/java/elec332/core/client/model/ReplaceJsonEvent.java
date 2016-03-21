package elec332.core.client.model;

import elec332.core.client.model.template.ElecTemplateBakery;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 21-11-2015.
 */
@SideOnly(Side.CLIENT)
public final class ReplaceJsonEvent extends Event {

    public ReplaceJsonEvent(ElecQuadBakery quadBakery, ElecModelBakery modelBakery, ElecTemplateBakery templateBakery){
        this.quadBakery = quadBakery;
        this.modelBakery = modelBakery;
        this.templateBakery = templateBakery;
    }

    public final ElecQuadBakery quadBakery;
    public final ElecModelBakery modelBakery;
    public final ElecTemplateBakery templateBakery;

}
