package elec332.core.inventory;

import com.google.common.collect.Lists;
import elec332.core.inventory.widget.Widget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import java.util.List;

/**
 * Created by Elec332 on 4-4-2015.
 */
public class BaseContainer extends Container implements IWidgetContainer{

    public BaseContainer(EntityPlayer player, int offset){
        this(player);
        this.offset = offset;
    }

    public BaseContainer(EntityPlayer player){
        //this.tileEntity = tile;
        this.thePlayer = player;
        this.offset = 0;
        this.widgets = Lists.newArrayList();
    }

    //private BaseTileWithInventory tileEntity;
    protected final EntityPlayer thePlayer;
    protected final List<Widget> widgets;
    private int offset;
    private int hotBarFactor = 0;

    public List<Widget> getWidgets(){
        return this.widgets;
    }

    @Override
    public void addWidget(Widget widget) {
        this.widgets.add(widget);
        widget.setContainer(this);
    }

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
    public void addCraftingToCrafters(ICrafting iCrafting) {
        for (Widget widget : widgets)
            widget.initWidget(iCrafting);
        super.addCraftingToCrafters(iCrafting);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Widget widget : widgets){
            for (Object obj : crafters){
                widget.detectAndSendChanges((ICrafting)obj);
            }
        }
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
