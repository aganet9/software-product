package ru.chsu.software_product.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.software_product.model.entity.License;

import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    @NotNull
    @EntityGraph(attributePaths = {"product"})
    List<License> findAll();

    @EntityGraph(attributePaths = {"product"})
    @NotNull Optional<License> findById(@NotNull Long id);
}
