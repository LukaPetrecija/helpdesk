package hr.algebra.brjakpo._2026.project.helpdesk.service;

/** Thrown when a requested record does not exist; the web layer turns it into a 404 page. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String entity, Long id) {
        super(entity + " with id " + id + " does not exist.");
    }
}
