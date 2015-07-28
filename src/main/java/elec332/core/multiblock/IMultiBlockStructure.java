package elec332.core.multiblock;

/**
 * Created by Elec332 on 26-7-2015.
 */
public interface IMultiBlockStructure {

    public BlockStructure getStructure();

    public BlockStructure.IStructureFiller replaceUponCreated();

    public BlockData getTriggerBlock();

}
