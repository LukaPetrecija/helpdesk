package hr.algebra.brjakpo._2026.project.helpdesk.repository;

import hr.algebra.brjakpo._2026.project.helpdesk.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import hr.algebra.brjakpo._2026.project.helpdesk.model.Role;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAllByOrderByFullNameAsc();

    List<AppUser> findAllByRoleOrderByFullNameAsc(Role role);
}
