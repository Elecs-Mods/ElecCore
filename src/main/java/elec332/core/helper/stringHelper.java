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
    public static String[] convertStringListToStringArray(ArrayList Arraylist){
        String[] Def = new String[Arraylist.size()];
        Arraylist.toArray(Def);

        return Def;
    }
}
