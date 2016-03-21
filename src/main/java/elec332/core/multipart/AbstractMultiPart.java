package elec332.core.multipart;

import elec332.core.main.ElecCore;
import elec332.core.world.WorldHelper;
import mcmultipart.multipart.Multipart;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 9-2-2016.
 */
public abstract class AbstractMultiPart extends Multipart {

    @Override
    public void onLoaded() {
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                onPartValidated();
            }
        }, getWorld());
    }

    @Override
    public void onUnloaded() {
        onPartInvalidated();
    }

    @Override
    public void onAdded() {
        onPartValidated();
    }

    @Override
    public void onRemoved() {
        onPartInvalidated();
    }

    public void onPartValidated(){
    }

    public void onPartInvalidated(){
    }

    public TileEntity getTile(){
        return WorldHelper.getTileAt(getWorld(), getPos());
    }

}
