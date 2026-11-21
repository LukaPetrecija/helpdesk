package hr.algebra.brjakpo._2026.project.helpdesk.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentForm {

    /** null when adding, set when editing. */
    private Long id;

    @NotBlank(message = "Comment cannot be empty.")
    @Size(max = 5000, message = "Comment can have at most 5000 characters.")
    private String body;

    private boolean internal;

    /** Temporary until login exists (M3): who writes the comment. */
    @NotNull(message = "Please select the author.")
    private Long authorId;
}
