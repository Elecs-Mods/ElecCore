package elec332.core.java;

import java.util.List;

/**
 * Created by Elec332 on 27-11-2015.
 */
public class JavaHelper {

    public static String getFirstEntryContaining(List<String > list, String check){
        for (String s : list){
            if (s.contains(check))
                return s;
        }
        return null;
    }

    public static List<String> replaceAll(List<String> list, String initial, String replace){
        for (int i = 0; i < list.size(); i++) {
            list.add(i, list.get(i).replace(initial, replace));
        }
        return list;
    }

    public static <O> boolean hasAtLeastOneMatch(List<O> list1, List<O> list2){
        for (O o : list1){
            if (list2.contains(o))
                return true;
        }
        return false;
    }

    public static boolean doesListContainPartially(List<String> list, String check){
        return getFirstEntryContaining(list, check) != null;
    }

}
