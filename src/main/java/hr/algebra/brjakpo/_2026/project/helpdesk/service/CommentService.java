package hr.algebra.brjakpo._2026.project.helpdesk.service;

import hr.algebra.brjakpo._2026.project.helpdesk.form.CommentForm;
import hr.algebra.brjakpo._2026.project.helpdesk.model.AppUser;
import hr.algebra.brjakpo._2026.project.helpdesk.model.Comment;
import hr.algebra.brjakpo._2026.project.helpdesk.model.Ticket;
import hr.algebra.brjakpo._2026.project.helpdesk.repository.AppUserRepository;
import hr.algebra.brjakpo._2026.project.helpdesk.repository.CommentRepository;
import hr.algebra.brjakpo._2026.project.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final AppUserRepository userRepository;

    public CommentForm toForm(Long ticketId, Long commentId) {
        Comment comment = findInTicket(ticketId, commentId);
        CommentForm form = new CommentForm();
        form.setId(comment.getId());
        form.setBody(comment.getBody());
        form.setInternal(comment.isInternal());
        form.setAuthorId(comment.getAuthor().getId());
        return form;
    }

    @Transactional
    public Comment add(Long ticketId, CommentForm form) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket", ticketId));
        Comment comment = new Comment();
        comment.setBody(form.getBody().trim());
        comment.setInternal(form.isInternal());
        comment.setAuthor(findUser(form.getAuthorId()));
        comment.setCreatedAt(LocalDateTime.now());
        ticket.addComment(comment);              // keeps both sides of the relation in sync
        return commentRepository.save(comment);
    }

    @Transactional
    public void update(Long ticketId, Long commentId, CommentForm form) {
        Comment comment = findInTicket(ticketId, commentId);
        comment.setBody(form.getBody().trim());
        comment.setInternal(form.isInternal());
        comment.setEditedAt(LocalDateTime.now());
    }

    @Transactional
    public void delete(Long ticketId, Long commentId) {
        Comment comment = findInTicket(ticketId, commentId);
        comment.getTicket().removeComment(comment);   // orphanRemoval deletes the row
    }

    /** A comment is only reachable through its own ticket (/tickets/1/comments/99 must not touch ticket 2). */
    private Comment findInTicket(Long ticketId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment", commentId));
        if (!comment.getTicket().getId().equals(ticketId)) {
            throw new NotFoundException("Comment", commentId);
        }
        return comment;
    }

    private AppUser findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }
}
