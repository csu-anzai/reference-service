package ch.admin.seco.service.reference.infrastructure.batch.importer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobCenterRepository extends JpaRepository<JobCenter, Long> {
}
