package elec332.core.java;

import java.util.List;

/**
 * Created by Elec332 on 27-11-2015.
 */
public class JavaHelper {

    public static <O> boolean hasAtLeastOneMatch(List<O> list1, List<O> list2){
        for (O o : list1){
            if (list2.contains(o))
                return true;
        }
        return false;
    }

    public static boolean doesListContainPartially(List<String> list, String check){
        for (String s : list){
            if (s.contains(check))
                return true;
        }
        return false;
    }

}
