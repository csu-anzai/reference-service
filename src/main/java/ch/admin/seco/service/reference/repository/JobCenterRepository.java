package ch.admin.seco.service.reference.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.admin.seco.service.reference.domain.JobCenter;

@Repository
public interface JobCenterRepository extends JpaRepository<JobCenter, UUID> {

    Optional<JobCenter> findOneByCode(String code);

}
