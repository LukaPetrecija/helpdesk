package hr.algebra.brjakpo._2026.project.helpdesk.web;

import hr.algebra.brjakpo._2026.project.helpdesk.form.CommentForm;
import hr.algebra.brjakpo._2026.project.helpdesk.form.TicketForm;
import hr.algebra.brjakpo._2026.project.helpdesk.model.Priority;
import hr.algebra.brjakpo._2026.project.helpdesk.model.Ticket;
import hr.algebra.brjakpo._2026.project.helpdesk.model.TicketStatus;
import hr.algebra.brjakpo._2026.project.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Ticket screens: list, detail, create, edit, delete. No business logic here, only HTTP. */
@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tickets", ticketService.findAll());
        return "tickets/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", ticketService.findById(id));
        model.addAttribute("users", ticketService.findAllUsers());
        if (!model.containsAttribute("commentForm")) {
            model.addAttribute("commentForm", new CommentForm());
        }
        return "tickets/detail";
    }

    // ---------- create ----------

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("ticketForm", new TicketForm());
        addReferenceData(model);
        return "tickets/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("ticketForm") TicketForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            addReferenceData(model);
            return "tickets/form";                       // same view, errors shown next to the fields
        }
        Ticket ticket = ticketService.create(form);
        redirect.addFlashAttribute("flash",
                "Ticket #" + ticket.getId() + " '" + ticket.getTitle() + "' was created.");
        return "redirect:/tickets/" + ticket.getId();   // POST-redirect-GET
    }

    // ---------- edit ----------

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("ticketForm", ticketService.toForm(id));
        addReferenceData(model);
        return "tickets/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("ticketForm") TicketForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        form.setId(id);
        if (result.hasErrors()) {
            addReferenceData(model);
            return "tickets/form";
        }
        ticketService.update(id, form);
        redirect.addFlashAttribute("flash", "Ticket #" + id + " was updated.");
        return "redirect:/tickets/" + id;
    }

    // ---------- delete ----------

    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", ticketService.findById(id));
        return "tickets/confirm-delete";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        ticketService.delete(id);
        redirect.addFlashAttribute("flash", "Ticket #" + id + " was deleted.");
        return "redirect:/tickets";
    }

    private void addReferenceData(Model model) {
        model.addAttribute("categories", ticketService.findAllCategories());
        model.addAttribute("users", ticketService.findAllUsers());
        model.addAttribute("agents", ticketService.findAllAgents());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TicketStatus.values());
    }
}
