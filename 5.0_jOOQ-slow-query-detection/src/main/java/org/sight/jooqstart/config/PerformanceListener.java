package org.sight.jooqstart.config;

import lombok.extern.slf4j.Slf4j;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Query;
import org.jooq.tools.StopWatch;

import java.time.Duration;

@Slf4j
public class PerformanceListener implements ExecuteListener {
    private long startTime;

    StopWatch watch;

    static class SQLPerformanceWarning extends Exception {
    }

    @Override
    public void executeStart(ExecuteContext ctx) {
        watch = new StopWatch();
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        int slowQuerySecond = 3;
        Duration slowQueryLimit = Duration.ofSeconds(slowQuerySecond);
        if (watch.split() > slowQueryLimit.getNano()) {
            Query query = ctx.query();

            log.warn(
                    "Slow SQL 탐지 >> \n" +
                            "jOOQ에서 실행된 쿼리 중 "
                            + slowQuerySecond + "초 이상 실행된 쿼리가 있습니다. --> \n"
                            + query,
                    new SQLPerformanceWarning()
            );
        }
    }
}