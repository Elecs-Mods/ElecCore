package elec332.core.api.client.model.loading;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 11-3-2016.
 * <p>
 * Model handler for items
 */
public interface IItemModelHandler {

    /**
     * Whether this handler can handle this item
     *
     * @param item The item
     * @return Whether this handler can handle this item
     */
    public boolean handleItem(Item item);

    /**
     * Used to create an identifier for this Item
     * (This will also be the variant in the ModelResourceLocation)
     *
     * @param item The item
     * @return The identifier for this Item
     */
    public String getIdentifier(Item item);

    /**
     * Used to create/fetch the model for this {@param item}
     *
     * @param item                 The item
     * @param identifier           The identifier of this item
     * @param fullResourceLocation The full ModelResourceLocation for this model
     * @return The model for this {@param item}
     */
    public IBakedModel getModelFor(Item item, String identifier, ModelResourceLocation fullResourceLocation);

}
