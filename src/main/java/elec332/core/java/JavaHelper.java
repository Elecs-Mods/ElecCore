package elec332.core.java;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 27-11-2015.
 */
public class JavaHelper {

    public static <K, V> Map<K, V> sort(Map<K, V> original, Comparator<Map.Entry<K, V>> comparator){
        return original.entrySet().stream().sorted(comparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static String getFirstEntryContaining(List<String > list, String check){
        for (String s : list){
            if (s.contains(check)) {
                return s;
            }
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
            if (list2.contains(o)) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesListContainPartially(List<String> list, String check){
        return getFirstEntryContaining(list, check) != null;
    }

}
