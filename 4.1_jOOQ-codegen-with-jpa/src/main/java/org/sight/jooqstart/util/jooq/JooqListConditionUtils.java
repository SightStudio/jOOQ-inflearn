package org.sight.jooqstart.util.jooq;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class JooqListConditionUtils {

    public static <T> Condition inIfNotEmpty(Field<T> field, List<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            return DSL.noCondition();
        }

        return field.in(values);
    }
}
