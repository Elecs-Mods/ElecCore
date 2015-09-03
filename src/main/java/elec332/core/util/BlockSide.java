package elec332.core.util;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 2-9-2015.
 */
public enum BlockSide {

    DOWN(0), UP(1), FRONT(2), BACK(3), RIGHT(4), LEFT(5);

    private BlockSide(int defaultSide){
        this.side = defaultSide;
    }

    private final int side;

    public int getDefaultSide() {
        return side;
    }

    public ForgeDirection getDefaultDirection(){
        return ForgeDirection.getOrientation(getDefaultSide());
    }

}
