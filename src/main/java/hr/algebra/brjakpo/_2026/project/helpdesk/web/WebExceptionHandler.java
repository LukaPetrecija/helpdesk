package hr.algebra.brjakpo._2026.project.helpdesk.web;

import hr.algebra.brjakpo._2026.project.helpdesk.service.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Turns exceptions from the service layer into friendly error pages with the right HTTP status. */
@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(NotFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("heading", "Not found");
        model.addAttribute("message", ex.getMessage());
        return "error/page";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflict(Model model) {
        model.addAttribute("status", 409);
        model.addAttribute("heading", "Could not save");
        model.addAttribute("message", "The change conflicts with existing data (a database constraint rejected it).");
        return "error/page";
    }
}
