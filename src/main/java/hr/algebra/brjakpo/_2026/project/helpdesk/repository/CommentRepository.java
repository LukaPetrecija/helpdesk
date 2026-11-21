package hr.algebra.brjakpo._2026.project.helpdesk.repository;

import hr.algebra.brjakpo._2026.project.helpdesk.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
