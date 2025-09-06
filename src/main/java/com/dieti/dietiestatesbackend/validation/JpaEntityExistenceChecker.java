package com.dieti.dietiestatesbackend.validation;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Implementazione JPA di EntityExistenceChecker usando Criteria API.
 * Si occupa esclusivamente di interrogare il datastore (SRP).
 */
@Component
public class JpaEntityExistenceChecker implements EntityExistenceChecker {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(Class<?> entityClass, String fieldName, Object value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<?> root = cq.from(entityClass);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get(fieldName), value));
        Long count = em.createQuery(cq).getSingleResult();
        return count != null && count > 0;
    }
}