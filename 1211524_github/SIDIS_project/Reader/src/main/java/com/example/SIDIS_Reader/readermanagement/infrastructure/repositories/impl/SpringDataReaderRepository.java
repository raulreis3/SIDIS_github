package com.example.SIDIS_Reader.readermanagement.infrastructure.repositories.impl;

import com.example.SIDIS_Reader.exceptions.NotFoundException;
import com.example.SIDIS_Reader.readermanagement.model.Reader;
import com.example.SIDIS_Reader.readermanagement.repositories.ReaderRepository;
import com.example.SIDIS_Reader.readermanagement.services.Page;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = "readers")
public interface SpringDataReaderRepository extends ReaderRepository, ReaderRepoCustom, CrudRepository<Reader,Long> {

    @Override
    @CacheEvict(allEntries = true)
    <S extends Reader> List<S> saveAll(Iterable<S> entities);

    <S extends Reader> S save(S entity);

    /**
     * findById searches a specific user and returns an optional
     */
    @Override
    @Cacheable
    Optional<Reader> findById(Long objectId);

    /**
     * getById explicitly loads a user or throws an exception if the user does not
     * exist or the account is not enabled
     *
     * @param id
     * @return
     */
    @Cacheable
    default Reader getById(final Long id) {
        final Optional<Reader> maybeReader = findById(id);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybeReader.filter(Reader::isEnabled).orElseThrow(() -> new NotFoundException(Reader.class, id));
    }

    @Cacheable
    Optional<Reader> findByUsername(String username);

    @Cacheable
    default Reader getByUsername(final String username) {
        final Optional<Reader> maybeReader = findByUsername(username);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybeReader.filter(Reader::isEnabled).orElseThrow(() -> new NotFoundException(Reader.class, username));
    }

    @Cacheable
    Optional<Reader> findByReaderNumber(String readerNumber);

    @Cacheable
    default Reader getByReaderNumber(final String readerNumber) {
        final Optional<Reader> maybeReader = findByReaderNumber(readerNumber);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybeReader.filter(Reader::isEnabled).orElseThrow(() -> new NotFoundException(Reader.class, readerNumber));
    }

    @Override
    @Query("SELECT r.readerNumber FROM Reader r WHERE r.readerNumber LIKE %:year% ORDER BY r.id DESC LIMIT 1")
    String findLastReaderNumber(final String year);

    @Cacheable
    default String getLastReaderNumber(final String year){
        final String lastReaderNumber = findLastReaderNumber(year);

        if(lastReaderNumber == null)
        {
            return year+"/0";
        }else
        {
            return lastReaderNumber;
        }
    }
    @Override
    @Query("SELECT r FROM Reader r WHERE r.phoneNumber LIKE :phoneNumber")
    List<Reader> getByPhoneNumber(final String phoneNumber, Pageable page);
}

interface ReaderRepoCustom {

    List<Reader> getReaderByName(Page page, String name);
}



@RequiredArgsConstructor
class ReaderRepoCustomImpl implements ReaderRepoCustom {

    // get the underlying JPA Entity Manager via spring thru constructor dependency
    // injection
    private final EntityManager em;

    @Override
    public List<Reader> getReaderByName(final Page page, String name) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Reader> cq = cb.createQuery(Reader.class);
        final Root<Reader> root = cq.from(Reader.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();
        if (StringUtils.hasText(name)) {
            where.add(cb.like(root.get("name"), "%"+name+"%"));
        }

        // search using OR
        if (!where.isEmpty()) {
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }

        final TypedQuery<Reader> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }
}
