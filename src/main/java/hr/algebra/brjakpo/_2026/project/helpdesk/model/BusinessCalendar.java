package hr.algebra.brjakpo._2026.project.helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "business_calendar")
@Getter
@Setter
@NoArgsConstructor
public class BusinessCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "time_zone", nullable = false, length = 50)
    private String timeZone;

    @Column(name = "work_day_start", nullable = false)
    private LocalTime workDayStart;

    @Column(name = "work_day_end", nullable = false)
    private LocalTime workDayEnd;

    /** Comma-separated DayOfWeek names, e.g. MONDAY,TUESDAY,... */
    @Column(name = "working_days", nullable = false, length = 50)
    private String workingDays;

    @OneToMany(mappedBy = "businessCalendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("holidayDate ASC")
    private List<Holiday> holidays = new ArrayList<>();
}
