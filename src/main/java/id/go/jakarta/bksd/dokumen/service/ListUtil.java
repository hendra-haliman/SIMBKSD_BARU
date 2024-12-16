package id.go.jakarta.bksd.dokumen.service;

import java.util.List;

public class ListUtil {
 /**
     * Returns the first element of a list or null if the list is null or empty.
     *
     * @param <T> the type of elements in the list
     * @param list the list from which to retrieve the first element
     * @return the first element of the list, or null if the list is null or empty
     */
    public static <T> T getFirstOrNull(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
