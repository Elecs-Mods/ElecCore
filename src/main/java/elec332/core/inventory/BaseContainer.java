package elec332.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * Created by Elec332 on 4-4-2015.
 */
public class BaseContainer extends Container {

    public BaseContainer(EntityPlayer player, int offset){
        this(player);
        this.offset = offset;
    }

    public BaseContainer(EntityPlayer player){
        //this.tileEntity = tile;
        this.thePlayer = player;
        this.offset = 0;
    }

    //private BaseTileWithInventory tileEntity;
    protected EntityPlayer thePlayer;
    private int offset;
    private int hotBarFactor = 0;

    public void addPlayerInventoryToContainer(){
        /*for(int j = 0; j < 3; j++) {
            for(int i1 = 0; i1 < 9; i1++) {
                addSlotToContainer(new Slot(thePlayer.inventory, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18));
            }
        }

        for(int i3 = 0; i3 < 9; i3++) {
            addSlotToContainer(new Slot(thePlayer.inventory, i3, 8 + i3 * 18, 211));
        }*/
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(thePlayer.inventory, j + i * 9 + 9, 8 + j * 18, (84 + this.offset) + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(thePlayer.inventory, i, 8 + i * 18, 142 + this.offset + hotBarFactor()));
        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public Slot addSlotToContainer(Slot slot) {
        return super.addSlotToContainer(slot);
    }

    public void setHotBarFactor(int hotBarFactor) {
        this.hotBarFactor = hotBarFactor;
    }

    protected int hotBarFactor(){
        return hotBarFactor;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return //this.tileEntity.isUseableByPlayer(player);
        true;
    }
}
