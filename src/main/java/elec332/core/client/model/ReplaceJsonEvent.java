package elec332.core.client.model;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Elec332 on 21-11-2015.
 */
public class ReplaceJsonEvent extends Event {

    public ReplaceJsonEvent(ElecQuadBakery quadBakery, RenderingRegistry renderingRegistry){
        this.quadBakery = quadBakery;
        this.renderingRegistry = renderingRegistry;
    }

    public final ElecQuadBakery quadBakery;
    public final RenderingRegistry renderingRegistry;

}
