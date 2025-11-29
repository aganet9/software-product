package ru.chsu.software_product.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DashboardStats {
    private Long totalDevelopers;
    private Long totalProducts;
    private Long totalLicenses;
    private Long totalPatches;
    private Long totalRequirements;

    private Map<String, Long> productsByDeveloper;
    private Map<String, Long> productsByType;
    private Map<String, Long> licensesByType;
    private Map<String, Long> patchesByCriticalLevel;

    private Map<String, Long> monthlyProductReleases;
    private Map<String, Long> monthlyLicensePurchases;
}