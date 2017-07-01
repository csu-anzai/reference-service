package ch.admin.seco.service.reference.service.impl;

import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.UUID;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Occupation.
 */
@Service
@Transactional
public class OccupationServiceImpl implements OccupationService{

    private final Logger log = LoggerFactory.getLogger(OccupationServiceImpl.class);

    private final OccupationRepository occupationRepository;

    private final OccupationSearchRepository occupationSearchRepository;

    public OccupationServiceImpl(OccupationRepository occupationRepository, OccupationSearchRepository occupationSearchRepository) {
        this.occupationRepository = occupationRepository;
        this.occupationSearchRepository = occupationSearchRepository;
    }

    /**
     * Save a occupation.
     *
     * @param occupation the entity to save
     * @return the persisted entity
     */
    @Override
    public Occupation save(Occupation occupation) {
        log.debug("Request to save Occupation : {}", occupation);
        Occupation result = occupationRepository.save(occupation);
        occupationSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the occupations.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Occupation> findAll(Pageable pageable) {
        log.debug("Request to get all Occupations");
        return occupationRepository.findAll(pageable);
    }

    /**
     *  Get one occupation by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Occupation> findOne(UUID id) {
        log.debug("Request to get Occupation : {}", id);
        return occupationRepository.findById(id);
    }

    /**
     *  Delete the  occupation by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Occupation : {}", id);
        occupationRepository.deleteById(id);
        occupationSearchRepository.deleteById(id);
    }

    /**
     * Search for the occupation corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Occupation> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Occupations for query {}", query);
        Page<Occupation> result = occupationSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
