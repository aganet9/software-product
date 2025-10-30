package ru.chsu.software_product.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.software_product.model.entity.Patch;

import java.util.List;

public interface PatchRepository extends JpaRepository<Patch, Long> {
    @NotNull
    @EntityGraph(attributePaths = {"product"})
    List<Patch> findAll();
}
