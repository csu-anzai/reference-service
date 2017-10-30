package ch.admin.seco.service.reference.repository;

import static org.hibernate.jpa.QueryHints.HINT_CACHE_MODE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import ch.admin.seco.service.reference.domain.Occupation;

public interface OccupationRepository extends JpaRepository<Occupation, UUID> {
    @QueryHints({
        @QueryHint(name = HINT_FETCH_SIZE, value = "1000"),
        @QueryHint(name = HINT_CACHE_MODE, value = "IGNORE")})
    @Query("select o from Occupation o")
    Stream<Occupation> streamAll();

    @QueryHints({
        @QueryHint(name = HINT_FETCH_SIZE, value = "1000"),
        @QueryHint(name = HINT_CACHE_MODE, value = "IGNORE")})
    @Query("select o from Occupation o where o.code in (select m.code from OccupationMapping m)")
    Stream<Occupation> streamAllAvam();

    Optional<Occupation> findOneByCode(int code);
}
