package hr.algebra.brjakpo._2026.project.helpdesk.web;

import hr.algebra.brjakpo._2026.project.helpdesk.form.CommentForm;
import hr.algebra.brjakpo._2026.project.helpdesk.service.CommentService;
import hr.algebra.brjakpo._2026.project.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Comments are always reached through their ticket: /tickets/{ticketId}/comments/... */
@Controller
@RequestMapping("/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TicketService ticketService;

    @PostMapping
    public String add(@PathVariable Long ticketId,
                      @Valid @ModelAttribute("commentForm") CommentForm form,
                      BindingResult result,
                      Model model,
                      RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("ticket", ticketService.findById(ticketId));
            model.addAttribute("users", ticketService.findAllUsers());
            return "tickets/detail";
        }
        commentService.add(ticketId, form);
        redirect.addFlashAttribute("flash", "Comment added.");
        return "redirect:/tickets/" + ticketId + "#comments";
    }

    @GetMapping("/{commentId}/edit")
    public String editForm(@PathVariable Long ticketId, @PathVariable Long commentId, Model model) {
        model.addAttribute("ticket", ticketService.findById(ticketId));
        model.addAttribute("commentForm", commentService.toForm(ticketId, commentId));
        return "comments/form";
    }

    @PostMapping("/{commentId}/edit")
    public String update(@PathVariable Long ticketId,
                         @PathVariable Long commentId,
                         @Valid @ModelAttribute("commentForm") CommentForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        form.setId(commentId);
        if (result.hasErrors()) {
            model.addAttribute("ticket", ticketService.findById(ticketId));
            return "comments/form";
        }
        commentService.update(ticketId, commentId, form);
        redirect.addFlashAttribute("flash", "Comment updated.");
        return "redirect:/tickets/" + ticketId + "#comments";
    }

    @PostMapping("/{commentId}/delete")
    public String delete(@PathVariable Long ticketId, @PathVariable Long commentId, RedirectAttributes redirect) {
        commentService.delete(ticketId, commentId);
        redirect.addFlashAttribute("flash", "Comment deleted.");
        return "redirect:/tickets/" + ticketId + "#comments";
    }
}
