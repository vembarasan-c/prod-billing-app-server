package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.DateRange;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class DateFilterService {

    public DateRange getDateRange(String filter) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        if (filter == null) {
            filter = "today";
        }

        switch (filter.toLowerCase()) {
            case "yesterday":
                startDate = now.minusDays(1);
                endDate = now.minusDays(1);
                break;
            case "this_week":
                startDate = now.with(DayOfWeek.MONDAY);
                endDate = now.with(DayOfWeek.SUNDAY);
                break;
            case "last_week":
                startDate = now.minusWeeks(1).with(DayOfWeek.MONDAY);
                endDate = now.minusWeeks(1).with(DayOfWeek.SUNDAY);
                break;
            case "this_month":
                startDate = now.with(TemporalAdjusters.firstDayOfMonth());
                endDate = now.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "last_month":
                startDate = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                endDate = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "this_year":
                startDate = now.with(TemporalAdjusters.firstDayOfYear());
                endDate = now.with(TemporalAdjusters.lastDayOfYear());
                break;
            case "today":
            default:
                startDate = now;
                endDate = now;
                break;
        }

        return DateRange.builder()
                .startDate(Date.valueOf(startDate))
                .endDate(Date.valueOf(endDate))
                .build();
    }
}
