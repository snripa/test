package org.rii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class StatsUpdater {
    private static final Logger STATS_LOGGER = LoggerFactory.getLogger("stats");
    private ZonedDateTime lastDowntime, lastFailure;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss ");


    public void update(ServiceHealth h) {
        switch (h) {
            case DOWN:
                lastFailure = ZonedDateTime.now();
                break;
            case UP:
                break;
            case NOT_AVAILABLE:
                lastDowntime = ZonedDateTime.now();

        }
        String formattedLastDT = lastDowntime != null ? lastDowntime.format(formatter) : "Never";
        String formattedLastFT = lastFailure != null ? lastFailure.format(formatter) : "Never";
        STATS_LOGGER.info("Server status: {}, last downtime: {}, last failure: {}",
                h, formattedLastDT, formattedLastFT);
    }
}
