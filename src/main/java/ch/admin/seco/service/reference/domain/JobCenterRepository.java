package ch.admin.seco.service.reference.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobCenterRepository extends JpaRepository<JobCenter, UUID> {

    Optional<JobCenter> findOneByCode(String code);

    Optional<JobCenter> findOneByPostalCodes(String postalCode);

}
