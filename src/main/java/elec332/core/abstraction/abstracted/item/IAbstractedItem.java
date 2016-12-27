package elec332.core.abstraction.abstracted.item;

import elec332.core.abstraction.IItem;

/**
 * Created by Elec332 on 24-12-2016.
 */
public interface IAbstractedItem<T extends IItem> {

    static final String GET_ITEM_METHOD = "getLinkedItem_INTERNAL_ELEC";

    public T getLinkedItem_INTERNAL_ELEC();

}
