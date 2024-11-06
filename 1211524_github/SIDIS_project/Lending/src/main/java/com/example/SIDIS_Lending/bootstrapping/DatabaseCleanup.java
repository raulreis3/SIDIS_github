package com.example.SIDIS_Lending.bootstrapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(0)
@Profile("bootstrap")
public class DatabaseCleanup implements CommandLineRunner {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        entityManager.createQuery("DELETE FROM Lending").executeUpdate();
        entityManager.createQuery("DELETE FROM LendingNumber").executeUpdate();
        entityManager.createQuery("DELETE FROM Reader").executeUpdate();
        entityManager.createQuery("DELETE FROM Forbiddenword").executeUpdate();

        entityManager.createQuery("DELETE FROM User").executeUpdate();
        entityManager.createQuery("DELETE FROM Book").executeUpdate();
        entityManager.createQuery("DELETE FROM Author").executeUpdate();
        entityManager.createQuery("DELETE FROM Genre").executeUpdate();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}