package elec332.core.client.model.renderer;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

/**
 * Created by Elec332 on 6-7-2020
 */
public class ElecModelRenderer extends RendererModel {

    public ElecModelRenderer(Model model, String boxNameIn) {
        super(model, boxNameIn);
    }

    public ElecModelRenderer(Model model) {
        super(model);
    }

    public ElecModelRenderer(Model model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

}
