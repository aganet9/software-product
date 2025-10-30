package ru.chsu.software_product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.software_product.model.entity.Developer;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
}
