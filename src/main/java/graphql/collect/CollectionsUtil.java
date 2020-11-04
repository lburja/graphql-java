package graphql.collect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import graphql.Internal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Internal
public final class CollectionsUtil {

    @SuppressWarnings("unchecked")
    public static <T> ImmutableList<T> emptyList() {
        return ImmutableList.of();
    }

    public static <T> ImmutableList<T> nonNullCopyOf(Collection<T> collection) {
        return collection == null ? emptyList() : ImmutableList.copyOf(collection);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> ImmutableMap<K, V> emptyMap() {
        return ImmutableMap.of();
    }


    public static <K, V> ImmutableMap<K, V> addToMap(Map<K, V> existing, K newKey, V newVal) {
        return ImmutableMap.<K, V>builder().putAll(existing).put(newKey, newVal).build();
    }

    public static <K, V> ImmutableMap<K, V> mergeMaps(Map<K, V> m1, Map<K, V> m2) {
        return ImmutableMap.<K, V>builder().putAll(m1).putAll(m2).build();
    }

    public static <T> ImmutableList<T> concatLists(List<T> l1, List<T> l2) {
        return ImmutableList.<T>builder().addAll(l1).addAll(l2).build();
    }

    public static <T, R> ImmutableList<R> listMap(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        ImmutableList.Builder<R> builder = ImmutableList.<R>builder();
        for (T t : collection) {
            R r = mapper.apply(t);
            builder.add(r);
        }
        return builder.build();
    }
}
