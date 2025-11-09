package ru.chsu.software_product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@Theme("default")
public class SoftwareProductApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(SoftwareProductApplication.class, args);
    }
}
