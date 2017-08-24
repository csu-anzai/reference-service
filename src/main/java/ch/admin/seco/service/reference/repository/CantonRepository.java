package ch.admin.seco.service.reference.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.admin.seco.service.reference.domain.Canton;


/**
 * Spring Data JPA repository for the Canton entity.
 */
@Repository
public interface CantonRepository extends JpaRepository<Canton, UUID> {
}
