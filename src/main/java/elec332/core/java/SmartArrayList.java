package elec332.core.java;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Elec332 on 22-3-2015.
 */
public class SmartArrayList<E> extends ArrayList<E> {

    public SmartArrayList<E> ArrayToSmartArray(Collection<E> collection){
        addAll(collection);
        return this;
    }

    public E getNext(E obj){
        return getNext(indexOf(obj));
    }

    public E getNext(int i){
        if (i == size()-1)
            return get(0);
        else
            return get(i+1);
    }

    public E getPrevious(E obj){
        return getPrevious(indexOf(obj));
    }

    public E getPrevious(int i){
        if (i == 0)
            return get(size()-1);
        else
            return get(i-1);
    }
}
