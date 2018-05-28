package cn.panda.ronda.base.remoting.helper;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class EnumFunction {

    /**
     * 需要key和enum的key是同类型
     */
    public static <E extends Enum<E> & EnumKeyInterface> Optional<E> findByKey(Class<E> clazz, Object key) {
        return Arrays.stream(clazz.getEnumConstants())
                .filter(e -> Objects.equals(e.key(), key))
                .findFirst();
    }
}
