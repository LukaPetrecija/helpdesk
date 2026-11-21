package hr.algebra.brjakpo._2026.project.helpdesk.service;

import hr.algebra.brjakpo._2026.project.helpdesk.form.TicketForm;
import hr.algebra.brjakpo._2026.project.helpdesk.model.*;
import hr.algebra.brjakpo._2026.project.helpdesk.repository.AppUserRepository;
import hr.algebra.brjakpo._2026.project.helpdesk.repository.CategoryRepository;
import hr.algebra.brjakpo._2026.project.helpdesk.repository.SlaPolicyRepository;
import hr.algebra.brjakpo._2026.project.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // reads by default; write methods override it
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AppUserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SlaPolicyRepository slaPolicyRepository;

    // ---------- reads ----------

    public List<Ticket> findAll() {
        return ticketRepository.findAllByOrderByCreatedAtDesc();
    }

    public Ticket findById(Long id) {
        return ticketRepository.findWithDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Ticket", id));
    }

    public TicketForm toForm(Long id) {
        Ticket ticket = findById(id);
        TicketForm form = new TicketForm();
        form.setId(ticket.getId());
        form.setTitle(ticket.getTitle());
        form.setDescription(ticket.getDescription());
        form.setCategoryId(ticket.getCategory().getId());
        form.setPriority(ticket.getPriority());
        form.setRequesterId(ticket.getRequester().getId());
        form.setStatus(ticket.getStatus());
        form.setAssigneeId(ticket.getAssignee() != null ? ticket.getAssignee().getId() : null);
        return form;
    }

    // reference data for the form drop-downs
    public List<Category> findAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public List<AppUser> findAllUsers() {
        return userRepository.findAllByOrderByFullNameAsc();
    }

    public List<AppUser> findAllAgents() {
        return userRepository.findAllByRoleOrderByFullNameAsc(Role.AGENT);
    }

    // ---------- writes ----------

    @Transactional
    public Ticket create(TicketForm form) {
        Ticket ticket = new Ticket();
        ticket.setTitle(form.getTitle().trim());
        ticket.setDescription(form.getDescription().trim());
        ticket.setStatus(TicketStatus.NEW);
        ticket.setRequester(findUser(form.getRequesterId()));

        Category category = findCategory(form.getCategoryId());
        ticket.setCategory(category);
        ticket.setTeam(category.getDefaultTeam());   // may be null: optional relation

        ticket.setCreatedAt(LocalDateTime.now());
        applyPriority(ticket, form.getPriority());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket update(Long id, TicketForm form) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket", id));

        ticket.setTitle(form.getTitle().trim());
        ticket.setDescription(form.getDescription().trim());
        ticket.setRequester(findUser(form.getRequesterId()));
        ticket.setCategory(findCategory(form.getCategoryId()));
        ticket.setAssignee(form.getAssigneeId() != null ? findUser(form.getAssigneeId()) : null);

        if (form.getPriority() != ticket.getPriority()) {
            applyPriority(ticket, form.getPriority());
        }
        if (form.getStatus() != null) {
            changeStatus(ticket, form.getStatus());
        }
        return ticket;   // managed entity: changes are flushed on commit, no save() needed
    }

    @Transactional
    public void delete(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket", id));
        ticketRepository.delete(ticket);   // comments, attachments, escalations go with it (cascade)
    }

    // ---------- business rules ----------

    /**
     * Picks the SLA policy for the priority and sets the due dates.
     * TODO (AR1): replace the plain clock-time calculation with business-hours arithmetic
     *             that uses the policy's BusinessCalendar and its holidays.
     */
    private void applyPriority(Ticket ticket, Priority priority) {
        SlaPolicy policy = slaPolicyRepository.findByPriority(priority)
                .orElseThrow(() -> new IllegalStateException("No SLA policy for priority " + priority));
        ticket.setPriority(priority);
        ticket.setSlaPolicy(policy);
        LocalDateTime start = ticket.getCreatedAt() != null ? ticket.getCreatedAt() : LocalDateTime.now();
        ticket.setFirstResponseDueAt(start.plusMinutes(policy.getFirstResponseMinutes()));
        ticket.setResolutionDueAt(start.plusMinutes(policy.getResolutionMinutes()));
    }

    private void changeStatus(Ticket ticket, TicketStatus newStatus) {
        boolean wasDone = ticket.getStatus() == TicketStatus.RESOLVED || ticket.getStatus() == TicketStatus.CLOSED;
        boolean isDone = newStatus == TicketStatus.RESOLVED || newStatus == TicketStatus.CLOSED;
        if (isDone && !wasDone) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (!isDone && wasDone) {
            ticket.setResolvedAt(null);   // re-opened
        }
        ticket.setStatus(newStatus);
    }

    private AppUser findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category", id));
    }
}
