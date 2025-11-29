package ru.chsu.software_product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chsu.software_product.model.dto.DashboardStats;
import ru.chsu.software_product.repository.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DeveloperRepository developerRepository;
    private final SoftwareProductRepository softwareProductRepository;
    private final LicenseRepository licenseRepository;
    private final PatchRepository patchRepository;
    private final SystemRequirementRepository systemRequirementRepository;

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // Основная статистика
        stats.setTotalDevelopers(developerRepository.count());
        stats.setTotalProducts(softwareProductRepository.count());
        stats.setTotalLicenses(licenseRepository.count());
        stats.setTotalPatches(patchRepository.count());
        stats.setTotalRequirements(systemRequirementRepository.count());

        // Статистика по разработчикам
        stats.setProductsByDeveloper(getProductsByDeveloper());

        // Статистика по типам ПО
        stats.setProductsByType(getProductsByType());

        // Статистика по лицензиям
        stats.setLicensesByType(getLicensesByType());

        // Статистика по критичности обновлений
        stats.setPatchesByCriticalLevel(getPatchesByCriticalLevel());

        // Временные статистики
        stats.setMonthlyProductReleases(getMonthlyProductReleases());
        stats.setMonthlyLicensePurchases(getMonthlyLicensePurchases());

        return stats;
    }

    private Map<String, Long> getProductsByDeveloper() {
        return softwareProductRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        product -> product.getDeveloper().getCompanyName(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getProductsByType() {
        return softwareProductRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        product -> product.getSoftwareType() != null ? product.getSoftwareType() : "Не указан",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getLicensesByType() {
        return licenseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        license -> license.getType() != null ? license.getType() : "Не указан",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getPatchesByCriticalLevel() {
        return patchRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        patch -> patch.getCriticalLevel() != null ? patch.getCriticalLevel() : "Не указан",
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getMonthlyProductReleases() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

        return softwareProductRepository.findAll().stream()
                .filter(product -> product.getReleaseDate() != null &&
                        product.getReleaseDate().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        product -> YearMonth.from(product.getReleaseDate()).toString(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getMonthlyLicensePurchases() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

        return licenseRepository.findAll().stream()
                .filter(license -> license.getPurchaseDate() != null &&
                        license.getPurchaseDate().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        license -> YearMonth.from(license.getPurchaseDate()).toString(),
                        Collectors.counting()
                ));
    }
}