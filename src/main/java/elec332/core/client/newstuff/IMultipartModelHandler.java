package elec332.core.client.newstuff;

import mcmultipart.multipart.IMultipart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

/**
 * Created by Elec332 on 27-4-2016.
 */
public interface IMultipartModelHandler {

    public boolean handlePart(IMultipart multipart);

    public String getIdentifier(IBlockState state, IMultipart multipart);

    public IBakedModel getModelFor(IMultipart part, IBlockState state, String identifier, ModelResourceLocation fullResourceLocation);

}
