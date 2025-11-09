package ru.chsu.software_product.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.software_product.model.entity.SoftwareProduct;

import java.util.List;
import java.util.Optional;

public interface SoftwareProductRepository extends JpaRepository<SoftwareProduct, Long> {
    @NotNull
    @EntityGraph(attributePaths = {"developer"})
    List<SoftwareProduct> findAll();

    @EntityGraph(attributePaths = {"developer"})
    @NotNull Optional<SoftwareProduct> findById(@NotNull Long id);

    boolean existsSoftwareProductByName(String name);

    Optional<SoftwareProduct> findByName(@NotNull String name);
}
