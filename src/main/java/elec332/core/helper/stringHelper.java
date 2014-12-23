package elec332.core.helper;

import java.util.ArrayList;

/**
 * Created by Elec332 on 23-12-2014.
 */
public class stringHelper {

    public static ArrayList<String> convertStringArrayToArraylist(String[] Ores) {
        ArrayList<String> stringList = new ArrayList<String>();
        for (String s : Ores) {
            stringList.add(s);
        }
        return stringList;
    }
}
