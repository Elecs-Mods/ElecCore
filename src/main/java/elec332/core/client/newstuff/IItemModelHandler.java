package elec332.core.client.newstuff;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 11-3-2016.
 */
public interface IItemModelHandler {

    public boolean handleItem(Item item);

    public String getIdentifier(Item item);

    public IBakedModel getModelFor(Item item, String identifier, ModelResourceLocation fullResourceLocation);

}
