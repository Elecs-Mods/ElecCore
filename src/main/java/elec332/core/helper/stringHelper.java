package elec332.core.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 23-12-2014.
 */
public class stringHelper {

    @SuppressWarnings("unchecked")
    public static ArrayList<String> convertStringArrayToArraylist(String[] string) {
        ArrayList<String> stringList = new ArrayList<String>();
        for (String s : string) {
            stringList.add(s);
        }
        return stringList;
    }
    public static String[] convertStringListToStringArray(List Arraylist){
        String[] Def = new String[Arraylist.size()];
        Arraylist.toArray(Def);

        return Def;
    }

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
