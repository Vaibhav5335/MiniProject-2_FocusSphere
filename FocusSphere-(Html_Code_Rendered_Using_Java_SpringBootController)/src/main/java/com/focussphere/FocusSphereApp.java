package com.focussphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class FocusSphereApp {

    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(FocusSphereApp.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
                System.out.println("FocusSphere launched successfully in your browser!");
            }
        } catch (Exception e) {
            System.out.println("Could not open browser automatically. Please go to http://localhost:8080");
        }
    }
}