package hr.algebra.brjakpo._2026.project.helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sla_policy")
@Getter
@Setter
@NoArgsConstructor
public class SlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    /** Minutes of business time. */
    @Column(name = "first_response_minutes", nullable = false)
    private int firstResponseMinutes;

    /** Minutes of business time. */
    @Column(name = "resolution_minutes", nullable = false)
    private int resolutionMinutes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_calendar_id", nullable = false)
    private BusinessCalendar businessCalendar;
}
