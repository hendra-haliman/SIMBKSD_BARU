package id.go.jakarta.bksd.dokumen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.xml.sax.SAXException;

import id.go.jakarta.bksd.dokumen.domain.Dokumen;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSama;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.DokumenLuarNegeri;
import id.go.jakarta.bksd.dokumen.service.DataUploaderService;
//import id.go.jakarta.bksd.dokumen.service.DevUtilityService;
import id.go.jakarta.bksd.dokumen.service.DokumenKerjaSamaLuarNegeriService;
import id.go.jakarta.bksd.dokumen.service.DokumenKerjaSamaService;
import id.go.jakarta.bksd.dokumen.service.FileReadWriteService;

@ShellComponent
// @Profile("shell")
public class DokumenShellCommands {
    Logger logger = LoggerFactory.getLogger(getClass());

    private Set<String> abbreviations = new HashSet<String>(
            Arrays.asList("DKI", "PT", "BIG", "PMO", "TRILOGI", "PSKD", "PLN", "STTD", "PUPR", "LRT", "GPIB", "HKBP",
                    "BMKG", "RI", "BPSDM", "RSU", "CV", "YPO", "BJB", "BNN", "KUKM", "SD", "SDN", "SMP", "SMA", "KB",
                    "TJSL", "SMU", "TPS", "BSF", "KCJB", "UMKM", "NAT", "II", "III", "IV", "WGS", "TPST", "SDM"));

    private Set<String> lowerCase = new HashSet<String>(Arrays.asList("di", "dan", "of", "atau"));

    @Autowired
    private DokumenKerjaSamaService dokumenKerjaSamaService;

    @Autowired
    private DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService;

    @Autowired
    private DataUploaderService dataUploaderService;

    @Autowired
    private FileReadWriteService fileReadWriteService;

    @Value("${upload.folder}")
    private String uploadFolder;

    private String convertToLatin1(String utf8String) {
        Charset utf8Charset = Charset.forName("UTF-8");
        Charset latin1Charset = Charset.forName("ISO-8859-1");

        CharsetEncoder encoder = utf8Charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .replaceWith(new byte[] { (byte) '?' });

        byte[] latin1Bytes;
        String latin1String = "";
        try {
            latin1Bytes = encoder.encode(CharBuffer.wrap(utf8String)).array();
            latin1String = new String(latin1Bytes, latin1Charset);
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
        }
        return latin1String;
    }

    @ShellMethod(key = "extractTextFromPDF")
    public void extractTextFromPDF() {
        System.out.println("Extract Text From PDF");

        List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjaSamaService.findAllFetchDokumen();

        System.out.println("There are " + dokumenKerjaSamaList.size() + " dokuments");
        int count = 0;

        Parser parser = new AutoDetectParser();

        TesseractOCRConfig config = new TesseractOCRConfig();

        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);
        pdfConfig.setExtractUniqueInlineImagesOnly(false);

        ParseContext parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        parseContext.set(PDFParserConfig.class, pdfConfig);
        parseContext.set(Parser.class, parser);

        for (DokumenKerjaSama dokumenKerjaSama : dokumenKerjaSamaList) {
            count++;
            System.out.println("Processing dokumen number : " + count);

            List<Dokumen> dokumenList = dokumenKerjaSama.getDokumen();

            String fileContent = dokumenKerjaSama.getFileContent();

            if ((fileContent == null) || (fileContent.isEmpty())) {

                for (Dokumen dokumen : dokumenList) {
                    String filename = dokumen.getFilename();

                    System.out.println("Processing file: [" + count + "] : " + filename);

                    BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);

                    try (InputStream inputStream = new FileInputStream(uploadFolder + "/" + filename)) {
                        parser.parse(inputStream, handler, new Metadata(), parseContext);
                    } catch (SAXException s) {
                        s.printStackTrace();
                    } catch (TikaException t) {
                        t.printStackTrace();
                    } catch (FileNotFoundException e) {
                        String number = dokumenKerjaSama.getNoDokumenPerjanjian();
                        Long id = dokumenKerjaSama.getId();

                        logger.info("File not found : [{}, {}]", id, number);

                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                    dokumenKerjaSama.setFileContent(handler.toString());
                    dokumenKerjaSamaService.save(dokumenKerjaSama);

                }
            }
        }

    }

    @ShellMethod(key = "extractTextFromPDFLN")
    public void extractTextFromPDFLN() {
        System.out.println("Extract Text From PDF - Dokumen Luar Negeri");

        List<DokumenKerjaSamaLuarNegeri> dokumenKerjaSamaList = dokumenKerjaSamaLuarNegeriService.findAllJoined();

        System.out.println("There are " + dokumenKerjaSamaList.size() + " dokuments");
        int count = 0;

        Parser parser = new AutoDetectParser();

        TesseractOCRConfig config = new TesseractOCRConfig();

        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);
        pdfConfig.setExtractUniqueInlineImagesOnly(false);

        ParseContext parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        parseContext.set(PDFParserConfig.class, pdfConfig);
        parseContext.set(Parser.class, parser);

        for (DokumenKerjaSamaLuarNegeri dokumenKerjaSama : dokumenKerjaSamaList) {
            count++;
            System.out.println("Processing dokumen number : " + count);

            List<DokumenLuarNegeri> dokumenList = dokumenKerjaSama.getDokumen();

            String fileContent = dokumenKerjaSama.getFileContent();

            if ((fileContent == null) || (fileContent.isEmpty())) {

                for (DokumenLuarNegeri dokumen : dokumenList) {
                    String filename = dokumen.getFilename();

                    System.out.println("Processing file: [" + count + "] : " + filename);

                    BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);

                    try (InputStream inputStream = new FileInputStream(uploadFolder + "/" + filename)) {

                        parser.parse(inputStream, handler, new Metadata(), parseContext);
                    } catch (SAXException s) {
                        s.printStackTrace();
                    } catch (TikaException t) {
                        t.printStackTrace();
                    } catch (FileNotFoundException e) {
                        System.out.println("File no found");
                    } catch (IOException i) {
                        i.printStackTrace();
                    }

                    dokumenKerjaSama.setFileContent(handler.toString());
                    dokumenKerjaSamaLuarNegeriService.save(dokumenKerjaSama);

                }
            }
        }

    }

    // Make judul naskah camel case
    @ShellMethod(key = "makeCamelCase")
    public void makeCamelCase() {

        List<DokumenKerjaSama> dokList = dokumenKerjaSamaService.findAll();

        System.out.println("Processing " + dokList.size() + "dokumen...");

        for (int i = 0; i < dokList.size(); i++) {
            System.out.println("Processing dok no: " + i);

            DokumenKerjaSama currDok = dokList.get(i);
            String judul = currDok.getJudulDokumenPerjanjian();
            judul = toPascalCaseWithSpacesSimple(judul);

            String mitra = currDok.getMitraKerjaSama();
            mitra = toPascalCaseWithSpacesSimple(mitra);

            currDok.setJudulDokumenPerjanjian(judul);
            currDok.setMitraKerjaSama(mitra);
            dokumenKerjaSamaService.save(currDok);
        }
    }

    /**
     * Simple transform text to Pascal-case without NLP
     * 
     * @param input
     * @return
     */
    private String toPascalCaseWithSpacesSimple(String input) {
        StringBuilder pascalCase = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                if (capitalizeNext) {
                    pascalCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    pascalCase.append(Character.toLowerCase(c));
                }
            } else {
                pascalCase.append(c); // Preserve all other characters, including spaces
                capitalizeNext = true;
            }
        }

        // Replace abbreviations back to their original form
        String result = pascalCase.toString();
        for (String abbr : abbreviations) {
            result = result.replaceAll("\\b" + Pattern.quote(toPascalCase(abbr)) + "\\b", abbr);
        }

        // Non pascal case
        for (String abbr : lowerCase) {
            result = result.replaceAll("\\b" + Pattern.quote(toPascalCase(abbr)) + "\\b", abbr);
        }

        return result;
    }

    private String toPascalCase(String word) {
        StringBuilder sb = new StringBuilder();
        boolean capitalize = true;
        for (char c : word.toCharArray()) {
            if (capitalize) {
                sb.append(Character.toUpperCase(c));
                capitalize = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    private String ANSI_RESET = "\u001B[0m";
    private String ANSI_BLACK = "\u001B[30m";
    private String ANSI_RED = "\u001B[31m";
    private String ANSI_GREEN = "\u001B[32m";

    private void printColor(String color, String text) {
        System.out.print(color);
        System.out.print(text);
        System.out.print(ANSI_RESET);
    }

    @ShellMethod(key = "testColor")
    public void testColor() {
        printColor(ANSI_RED, "This text is red!");
        System.out.println();
        printColor(ANSI_GREEN, "This text is green!");
    }

    // @ShellMethod(key = "testTinyURL")
    // public void testTinyURL() {
    // String url = "https://tinyurl.com/DKIParu";
    // String realURL = devUtilityService.expandUrl(url);
    // logger.info("TINY : [{}], REAL : [{}]", url, realURL);
    // }

    @ShellMethod(key = "testTinyURL2")
    public void testTinyURL2() {
        String url = "https://preview.tinyurl.com/DKIParu";
        String realURL = dataUploaderService.expandUrl2(url);
        logger.info("TINY : [{}], REAL : [{}]", url, realURL);
    }

    @ShellMethod(key = "testSCP")
    public void testSCP() {

        fileReadWriteService.scpTransferFile("hendra", "toor", "192.168.1.100", 22, "d:/testrekap/report2021.txt",
                "/home/hendra");

    }

    @ShellMethod(key = "testSCPtoLocal")
    public void testSCPtoLocal() {

        fileReadWriteService.sftpRemoteToLocal("hendra", "toor", "192.168.1.100", 22, "/home/hendra/test.txt",
                "d:/testrekap/test.txt");

    }

}
