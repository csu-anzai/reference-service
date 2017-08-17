package ch.admin.seco.service.reference.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.admin.seco.service.reference.domain.Occupation;

public interface OccupationRepository extends JpaRepository<Occupation, UUID> {
    Optional<Occupation> findOneByCode(int code);
}
