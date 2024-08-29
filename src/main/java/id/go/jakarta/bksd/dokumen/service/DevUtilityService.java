package id.go.jakarta.bksd.dokumen.service;

import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

import id.go.jakarta.bksd.dokumen.domain.Dokumen;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSama;

/**
 * A service only for dev environment.
 * 
 */
@Service
public class DevUtilityService {

    private DokumenKerjaSamaService dokumenKerjaSamaService;

    public DevUtilityService(DokumenKerjaSamaService dokumenKerjaSamaService) {
        this.dokumenKerjaSamaService = dokumenKerjaSamaService;
    }

    /**
     * Expand URL into original.
     * Note: Limited to 100 request per day.
     * 
     * @param url
     * @return
     */
    public String expandUrl(String url) {
        String result = "";
        String urlExpander = "https://urlex.org/";

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
            Page page = context.newPage();
            page.setDefaultTimeout(180000);

            page.navigate(urlExpander);

            page.locator("#shorturl").click();

            page.locator("#shorturl").fill(url);

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Expand")).click();
            Page page1 = page.waitForPopup(() -> {
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("⧉")).click();
            });

            result = page.locator("#r > tbody > tr > td:nth-child(2) > a:nth-child(1)").getAttribute("href");

        } catch (Exception e) {
            return "";
        }

        return result;

    }

    /**
     * Download file from Google drive, using browser automation, without the need
     * of permission of Google Drive API
     * 
     */
    public String downloadFileFromGoogleDrive(String url) {
        String filename = "";

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
            Page page = context.newPage();
            page.setDefaultTimeout(180000);

            page.navigate(url);

            // Wait for the Google Drive file page to load
            page.waitForSelector("div[aria-label='Download']", new Page.WaitForSelectorOptions().setTimeout(180000));

            filename = page.title();
            System.out.println(filename);
            int endIndex = filename.indexOf(".pdf") + 4;
            filename = filename.substring(0, endIndex);

            // Click the download button
            // page.click("div[aria-label='Download']");

            // Wait for the download process to complete
            Download download = page.waitForDownload(() -> {
                // Trigger the download
                // page.click("button[jsname='LgbsSe']");
                page.click("div[aria-label='Download']");
            });

            // Save the download to a specified path
            download.saveAs(Paths.get("d:/test/" + filename));

            System.out.println("Download completed: " + download.suggestedFilename());
        } catch (Exception e) {
            return "";
        }

        return filename;
    }

    /**
     * Download missing Dokumen attachments in DokumenKerjaSama based on docLink
     * attribute
     */
    public void downloadMissingFile() {
        List<DokumenKerjaSama> docList = dokumenKerjaSamaService.findAll();

        for (DokumenKerjaSama doc : docList) {
            List<Dokumen> files = doc.getDokumen();
            String url = doc.getLinkDokumenKerjaSama();

            if ((files != null) && (files.isEmpty()) && (url != null) && (url.length() > 0)
                    && (url.contains("google"))) {
                String filename = downloadFileFromGoogleDrive(url);

                if ((filename != null) && (filename.length() > 0)) {
                    Dokumen dokumen = new Dokumen();
                    dokumen.setFilename(filename);
                    doc.getDokumen().add(dokumen);
                    dokumenKerjaSamaService.save(doc);
                }
            }
        }
    }

}
