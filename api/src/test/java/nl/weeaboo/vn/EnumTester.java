package nl.weeaboo.vn;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class EnumTester {

    /**
     * Creates a hash of the available enum constants.
     */
    public static <T extends Enum<T>> int hashEnum(Class<T> enumType) {
        List<String> values = Lists.newArrayList();
        for (Enum<?> enumValue : enumType.getEnumConstants()) {
            values.add(enumValue.name());
        }
        return Joiner.on(',').join(values).hashCode();
    }

}
