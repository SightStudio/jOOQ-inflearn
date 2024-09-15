package org.sight.jooqstart.config;

import lombok.Builder;
import org.jooq.RecordContext;
import org.jooq.RecordListener;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.function.Consumer;

@Builder
public class RecordAuditListenerGenerator<R extends UpdatableRecordImpl<R>> {

    private Class<R> recordClass;
    private Consumer<R> insertStart;
    private Consumer<R> updateStart;
    private Consumer<R> storeStart;

    public RecordListener generate() {
        if (recordClass == null) {
            throw new IllegalArgumentException("레코드 클래스가 없습니다.");
        }

        return new RecordListener() {
            @Override
            public void insertStart(RecordContext ctx) {
                insertStart.accept(recordClass.cast(ctx.record()));
            }

            @Override
            public void storeStart(RecordContext ctx) {
                updateStart.accept(recordClass.cast(ctx.record()));
            }

            @Override
            public void updateStart(RecordContext ctx) {
                storeStart.accept(recordClass.cast(ctx.record()));
            }
        };
    }
}
