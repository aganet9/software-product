package ru.chsu.software_product.ui.view;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dashboard.Dashboard;
import com.vaadin.flow.component.dashboard.DashboardSection;
import com.vaadin.flow.component.dashboard.DashboardWidget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chsu.software_product.model.dto.DashboardStats;
import ru.chsu.software_product.service.DashboardService;

import java.util.Map;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Route("")
@PageTitle("Дашборд")
@Menu(order = -1, icon = "vaadin:dashboard", title = "Дашборд")
public class DashboardView extends Main {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardView(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
        initializeUI();
    }

    private void initializeUI() {
        setSizeFull();
        addClassNames(Padding.LARGE);

        DashboardStats stats = dashboardService.getDashboardStats();

        // Создаем основной Dashboard
        Dashboard dashboard = new Dashboard();
        dashboard.setSizeFull();

        // Секция с KPI карточками
        DashboardSection kpiSection = dashboard.addSection("Основные метрики");

        DashboardWidget developersWidget = new DashboardWidget("Разработчики");
        developersWidget.setContent(createKpiCard("Разработчики", stats.getTotalDevelopers(), VaadinIcon.USERS));
        kpiSection.add(developersWidget);

        DashboardWidget productsWidget = new DashboardWidget("Продукты");
        productsWidget.setContent(createKpiCard("Продукты", stats.getTotalProducts(), VaadinIcon.CUBES));
        kpiSection.add(productsWidget);

        DashboardWidget licensesWidget = new DashboardWidget("Лицензии");
        licensesWidget.setContent(createKpiCard("Лицензии", stats.getTotalLicenses(), VaadinIcon.FILE_TEXT));
        kpiSection.add(licensesWidget);

        DashboardWidget patchesWidget = new DashboardWidget("Обновления");
        patchesWidget.setContent(createKpiCard("Обновления", stats.getTotalPatches(), VaadinIcon.UPLOAD));
        kpiSection.add(patchesWidget);

        DashboardWidget requirementWidget = new DashboardWidget("Системные требования");
        requirementWidget.setContent(createKpiCard("Системные требования", stats.getTotalRequirements(), VaadinIcon.CLIPBOARD_CHECK));
        kpiSection.add(requirementWidget);

        // Секция с графиками
        DashboardSection chartsSection = dashboard.addSection("Аналитика");

        DashboardWidget productsByDeveloperWidget = new DashboardWidget("Продукты по разработчикам");
        productsByDeveloperWidget.setContent(createProductsByDeveloperChart(stats.getProductsByDeveloper()));
        chartsSection.add(productsByDeveloperWidget);

        DashboardWidget productsByTypeWidget = new DashboardWidget("Продукты по типам");
        productsByTypeWidget.setContent(createProductsByTypeChart(stats.getProductsByType()));
        chartsSection.add(productsByTypeWidget);

        DashboardWidget licensesByTypeWidget = new DashboardWidget("Лицензии по типам");
        licensesByTypeWidget.setContent(createLicensesByTypeChart(stats.getLicensesByType()));
        chartsSection.add(licensesByTypeWidget);

        DashboardWidget patchesByCriticalWidget = new DashboardWidget("Обновления по критичности");
        patchesByCriticalWidget.setContent(createPatchesByCriticalLevelChart(stats.getPatchesByCriticalLevel()));
        chartsSection.add(patchesByCriticalWidget);

        add(dashboard);
    }

    private Div createKpiCard(String title, Long value, VaadinIcon icon) {
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                Padding.LARGE,
                BorderRadius.LARGE,
                Display.FLEX,
                FlexDirection.COLUMN,
                AlignItems.CENTER,
                Gap.MEDIUM
        );

        Icon cardIcon = icon.create();
        cardIcon.addClassNames(IconSize.LARGE, TextColor.PRIMARY);

        Span valueSpan = new Span(String.valueOf(value));
        valueSpan.addClassNames(FontSize.XXXLARGE, FontWeight.BOLD, TextColor.PRIMARY);

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(FontSize.SMALL, TextColor.SECONDARY);

        card.add(cardIcon, valueSpan, titleSpan);
        return card;
    }

    private Div createChartContainer(Chart chart) {
        Div container = new Div();
        container.addClassNames(LumoUtility.Background.CONTRAST_5, Padding.MEDIUM, BorderRadius.LARGE);
        chart.setWidth("100%");
        chart.setHeight("300px");
        container.add(chart);
        return container;
    }

    private Div createProductsByDeveloperChart(Map<String, Long> data) {
        Chart chart = new Chart(ChartType.PIE);

        Configuration conf = chart.getConfiguration();
        conf.setTitle("Продукты по разработчикам");

        DataSeries series = new DataSeries();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            DataSeriesItem item = new DataSeriesItem(entry.getKey(), entry.getValue());
            series.add(item);
        }

        conf.addSeries(series);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setShowInLegend(true);
        conf.setPlotOptions(plotOptions);

        return createChartContainer(chart);
    }

    private Div createProductsByTypeChart(Map<String, Long> data) {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();
        conf.setTitle("Продукты по типам");

        XAxis xAxis = new XAxis();
        xAxis.setCategories(data.keySet().toArray(new String[0]));
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("Количество продуктов");
        conf.addyAxis(yAxis);

        DataSeries series = new DataSeries("Продукты");
        int index = 0;
        for (Long value : data.values()) {
            DataSeriesItem item = new DataSeriesItem();
            item.setY(value);
            series.add(item);
            index++;
        }
        conf.addSeries(series);

        return createChartContainer(chart);
    }

    private Div createLicensesByTypeChart(Map<String, Long> data) {
        Chart chart = new Chart(ChartType.BAR);

        Configuration conf = chart.getConfiguration();
        conf.setTitle("Лицензии по типам");

        XAxis xAxis = new XAxis();
        xAxis.setCategories(data.keySet().toArray(new String[0]));
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("Количество лицензий");
        conf.addyAxis(yAxis);

        DataSeries series = new DataSeries("Лицензии");
        int index = 0;
        for (Long value : data.values()) {
            DataSeriesItem item = new DataSeriesItem();
            item.setY(value);
            series.add(item);
            index++;
        }
        conf.addSeries(series);

        return createChartContainer(chart);
    }

    private Div createPatchesByCriticalLevelChart(Map<String, Long> data) {
        Chart chart = new Chart(ChartType.PIE);

        Configuration conf = chart.getConfiguration();
        conf.setTitle("Обновления по критичности");

        DataSeries series = new DataSeries();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            DataSeriesItem item = new DataSeriesItem(entry.getKey(), entry.getValue());
            series.add(item);
        }

        conf.addSeries(series);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setShowInLegend(true);
        conf.setPlotOptions(plotOptions);

        return createChartContainer(chart);
    }
}