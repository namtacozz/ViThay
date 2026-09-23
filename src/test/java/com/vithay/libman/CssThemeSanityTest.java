package com.vithay.libman;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

public class CssThemeSanityTest {
    @Test
    public void testNewCssClassesExistInStyleSheet() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/com/vithay/libman/css/style.css")) {
            assertNotNull(is, "style.css must exist in resources");
            String css = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(css.contains(".segmented-view-btn"), "Must contain segmented-view-btn");
            assertTrue(css.contains(".breadcrumb-container"), "Must contain breadcrumb-container");
            assertTrue(css.contains(".book-grid-card"), "Must contain book-grid-card");
            assertTrue(css.contains(".desk-canvas-zone"), "Must contain desk-canvas-zone");
            assertTrue(css.contains(".wizard-stepper-bar"), "Must contain wizard-stepper-bar");
            assertTrue(css.contains(".wizard-card-container"), "Must contain wizard-card-container");
            assertTrue(css.contains(".wizard-info-callout"), "Must contain wizard-info-callout");
            assertTrue(css.contains(".wizard-slip-preview"), "Must contain wizard-slip-preview");
            assertTrue(css.contains(".theme-pink-light .wizard-stepper-bar"), "Must contain light theme wizard stepper bar");
            assertTrue(css.contains(".theme-pink-light .wizard-card-container"), "Must contain light theme wizard card container");
        }
    }
}
