package ru.chsu.software_product.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.software_product.model.entity.SystemRequirement;

import java.util.List;

public interface SystemRequirementRepository extends JpaRepository<SystemRequirement, Integer> {
    @NotNull
    @EntityGraph(attributePaths = {"product"})
    List<SystemRequirement> findAll();
}
