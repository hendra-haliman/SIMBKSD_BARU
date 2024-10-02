package id.go.jakarta.bksd.dokumen.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Simple test untuk mencheck Playwright bisa menggunakan test runner JUnit
 */
@UsePlaywright
public class TestPlaywright {
    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
    }

    @BeforeEach
    void createContextAndPage() {
        // Agar bisa menjalankan browser dengan SSL certificate self-signed/localhost,
        // kita igone https errors
        Browser.NewContextOptions options = new Browser.NewContextOptions().setIgnoreHTTPSErrors(true);
        context = browser.newContext(options);
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void shouldClickButton() {
        page.navigate("data:text/html,<script>var result;</script><button onclick='result=\"Clicked\"'>Go</button>");
        page.locator("button").click();
        assertEquals("Clicked", page.evaluate("result"));
    }

    @Test
    void shouldCheckTheBox() {
        page.setContent("<input id='checkbox' type='checkbox'></input>");
        page.locator("input").check();
        assertTrue((Boolean) page.evaluate("() => window['checkbox'].checked"));
    }

    @Test
    void shouldSearchWiki() {
        page.navigate("https://www.wikipedia.org/");
        page.locator("input[name=\"search\"]").click();
        page.locator("input[name=\"search\"]").fill("playwright");
        page.locator("input[name=\"search\"]").press("Enter");
        assertEquals("https://en.wikipedia.org/wiki/Playwright", page.url());
    }

    @Test
    void shouldGotoDashboardPage() {
        page.navigate("https://localhost:8443/SIMBKSD/");
        page.locator("#navbarResponsive").getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Login"))
                .click();
        page.getByPlaceholder("Masukan email address").click();
        page.getByPlaceholder("Masukan email address").fill("hendra.haliman@gmail.com");
        page.getByPlaceholder("Masukan email address").press("Tab");
        page.getByPlaceholder("Masukan password").fill("superman");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();

        assertThat(page.locator("h3")).containsText("Dashboard");
    }

}
