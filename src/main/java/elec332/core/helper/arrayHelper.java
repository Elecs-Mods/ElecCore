package elec332.core.helper;

import java.util.ArrayList;

/**
 * Created by Elec332 on 16-1-2015.
 */
public class ArrayHelper {
    //public static ArrayList convertStringArrayToArraylist(Object[] string) {
    //    return null;
    //}

    @SuppressWarnings("unchecked")
    public static ArrayList mergeArrays(ArrayList arrayList1, ArrayList arrayList2){
        ArrayList returnArraylist = new ArrayList();
        for (int i = 0; i < arrayList1.size(); i++) {
            returnArraylist.add(arrayList1.get(i));
        }

        for (int i = 0; i < arrayList2.size(); i++) {
            returnArraylist.add(arrayList2.get(i));
        }

        return returnArraylist;
    }
}
