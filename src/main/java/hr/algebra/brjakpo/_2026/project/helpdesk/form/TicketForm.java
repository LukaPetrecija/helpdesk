package hr.algebra.brjakpo._2026.project.helpdesk.form;

import hr.algebra.brjakpo._2026.project.helpdesk.model.Priority;
import hr.algebra.brjakpo._2026.project.helpdesk.model.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Form object for creating and editing a ticket (never bind forms directly to entities). */
@Getter
@Setter
@NoArgsConstructor
public class TicketForm {

    /** null when creating, set when editing. */
    private Long id;

    @NotBlank(message = "Title is required.")
    @Size(max = 200, message = "Title can have at most 200 characters.")
    private String title;

    @NotBlank(message = "Description is required.")
    @Size(min = 10, max = 5000, message = "Description must have between 10 and 5000 characters.")
    private String description;

    @NotNull(message = "Please select a category.")
    private Long categoryId;

    @NotNull(message = "Please select a priority.")
    private Priority priority = Priority.MEDIUM;

    /** Temporary until login exists (M3): who raises the ticket. */
    @NotNull(message = "Please select the requester.")
    private Long requesterId;

    /** Edit only. */
    private TicketStatus status;

    /** Optional: may stay empty. */
    private Long assigneeId;

    /** true when editing an existing ticket (used by the template: ${ticketForm.editing}). */
    public boolean isEditing() {
        return id != null;
    }
}
