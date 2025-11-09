package ru.chsu.software_product.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.software_product.model.entity.Developer;

import java.util.List;
import java.util.Optional;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    @EntityGraph(attributePaths = {"softwareProducts"})
    @NotNull List<Developer> findAll();

    @EntityGraph(attributePaths = {"softwareProducts"})
    @NotNull Optional<Developer> findById(@NotNull Long id);

    boolean existsDeveloperByCompanyName(String companyName);

    Optional<Developer> findByCompanyName(@NotNull String companyName);
}
