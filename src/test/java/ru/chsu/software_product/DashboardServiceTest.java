package ru.chsu.software_product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.chsu.software_product.model.dto.DashboardStats;
import ru.chsu.software_product.model.entity.Developer;
import ru.chsu.software_product.model.entity.License;
import ru.chsu.software_product.model.entity.Patch;
import ru.chsu.software_product.model.entity.SoftwareProduct;
import ru.chsu.software_product.repository.*;
import ru.chsu.software_product.service.DashboardService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private DeveloperRepository developerRepository;
    @Mock private SoftwareProductRepository softwareProductRepository;
    @Mock private LicenseRepository licenseRepository;
    @Mock private PatchRepository patchRepository;
    @Mock private SystemRequirementRepository systemRequirementRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("getDashboardStats: собирает агрегированную статистику")
    void getDashboardStats_aggregates() {
        // counts
        given(developerRepository.count()).willReturn(2L);
        given(softwareProductRepository.count()).willReturn(3L);
        given(licenseRepository.count()).willReturn(4L);
        given(patchRepository.count()).willReturn(5L);
        given(systemRequirementRepository.count()).willReturn(6L);

        // products
        Developer acme = new Developer(); acme.setId(1L); acme.setCompanyName("Acme");
        Developer newco = new Developer(); newco.setId(2L); newco.setCompanyName("NewCo");

        SoftwareProduct p1 = new SoftwareProduct(); p1.setId(10L); p1.setName("P1"); p1.setDeveloper(acme); p1.setSoftwareType("Desktop"); p1.setReleaseDate(LocalDate.now().minusMonths(1));
        SoftwareProduct p2 = new SoftwareProduct(); p2.setId(11L); p2.setName("P2"); p2.setDeveloper(acme); p2.setSoftwareType("Web"); p2.setReleaseDate(LocalDate.now().minusMonths(7)); // out of window
        SoftwareProduct p3 = new SoftwareProduct(); p3.setId(12L); p3.setName("P3"); p3.setDeveloper(newco); p3.setSoftwareType(null); p3.setReleaseDate(LocalDate.now());
        given(softwareProductRepository.findAll()).willReturn(List.of(p1, p2, p3));

        // licenses
        License l1 = new License(); l1.setId(20L); l1.setType("Trial"); l1.setPurchaseDate(LocalDate.now().minusMonths(1));
        License l2 = new License(); l2.setId(21L); l2.setType(null); l2.setPurchaseDate(LocalDate.now());
        given(licenseRepository.findAll()).willReturn(List.of(l1, l2));

        // patches
        Patch c1 = new Patch(); c1.setId(30L); c1.setCriticalLevel("HIGH");
        Patch c2 = new Patch(); c2.setId(31L); c2.setCriticalLevel(null);
        given(patchRepository.findAll()).willReturn(List.of(c1, c2));

        DashboardStats stats = dashboardService.getDashboardStats();

        assertThat(stats.getTotalDevelopers()).isEqualTo(2L);
        assertThat(stats.getTotalProducts()).isEqualTo(3L);
        assertThat(stats.getTotalLicenses()).isEqualTo(4L);
        assertThat(stats.getTotalPatches()).isEqualTo(5L);
        assertThat(stats.getTotalRequirements()).isEqualTo(6L);

        // by developer
        assertThat(stats.getProductsByDeveloper()).containsEntry("Acme", 2L).containsEntry("NewCo", 1L);
        // by type (null -> "Не указан")
        assertThat(stats.getProductsByType()).containsEntry("Desktop", 1L).containsEntry("Web", 1L).containsEntry("Не указан", 1L);
        // licenses by type (null -> "Не указан")
        assertThat(stats.getLicensesByType()).containsEntry("Trial", 1L).containsEntry("Не указан", 1L);
        // patches by critical level (null -> "Не указан")
        assertThat(stats.getPatchesByCriticalLevel()).containsEntry("HIGH", 1L).containsEntry("Не указан", 1L);

        // monthly product releases: only within 6 months
        String m1 = p1.getReleaseDate().getYear() + "-" + String.format("%02d", p1.getReleaseDate().getMonthValue());
        String m3 = p3.getReleaseDate().getYear() + "-" + String.format("%02d", p3.getReleaseDate().getMonthValue());
        assertThat(stats.getMonthlyProductReleases()).containsEntry(m1, 1L).containsEntry(m3, 1L);
        // monthly license purchases: both inside 6 months
        String l1m = l1.getPurchaseDate().getYear() + "-" + String.format("%02d", l1.getPurchaseDate().getMonthValue());
        String l2m = l2.getPurchaseDate().getYear() + "-" + String.format("%02d", l2.getPurchaseDate().getMonthValue());
        assertThat(stats.getMonthlyLicensePurchases()).containsEntry(l1m, 1L).containsEntry(l2m, 1L);
    }
}
