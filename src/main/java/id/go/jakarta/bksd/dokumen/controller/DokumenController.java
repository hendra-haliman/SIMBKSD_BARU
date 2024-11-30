package id.go.jakarta.bksd.dokumen.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;
import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.ColumnOrder;
import id.go.jakarta.bksd.dokumen.domain.Dokumen;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSama;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.SelectedColumnOrder;
import id.go.jakarta.bksd.dokumen.domain.SelectedTableColumn;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaDTO;
import id.go.jakarta.bksd.dokumen.service.AsistenSekretarisDaerahService;
import id.go.jakarta.bksd.dokumen.service.BagianService;
import id.go.jakarta.bksd.dokumen.service.ColumnOrderService;
import id.go.jakarta.bksd.dokumen.service.DokumenKerjaSamaService;
import id.go.jakarta.bksd.dokumen.service.FileReadWriteService;
import id.go.jakarta.bksd.dokumen.service.JenisMitraService;
import id.go.jakarta.bksd.dokumen.service.JenisNaskahService;
import id.go.jakarta.bksd.dokumen.service.OPDService;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/dokumen")
public class DokumenController {
    Logger logger = LoggerFactory.getLogger(getClass());

    private DokumenKerjaSamaService dokumenKerjaSamaService;
    private JenisMitraService jenisMitraService;
    private JenisNaskahService jenisNaskahService;
    private OPDService opdService;
    private BagianService bagianService;
    private FileReadWriteService fileReadWriteService;
    private AsistenSekretarisDaerahService asistenService;
    private PemakaiService pemakaiService;
    private TableDefinitionService tableDefinitionService;
    private ColumnOrderService columnOrderService;
    private ModelMapper mapper;

    public DokumenController(DokumenKerjaSamaService dokumenKerjaSamaService,
            JenisMitraService jenisMitraService,
            JenisNaskahService jenisNaskahService,
            OPDService opdService,
            BagianService bagianService,
            FileReadWriteService fileReadWriteService,
            AsistenSekretarisDaerahService asistenSekretarisDaerahService,
            PemakaiService pemakaiService,
            TableDefinitionService tableDefinitionService,
            ColumnOrderService columnOrderService,
            ModelMapper mapper) {
        this.dokumenKerjaSamaService = dokumenKerjaSamaService;
        this.jenisMitraService = jenisMitraService;
        this.jenisNaskahService = jenisNaskahService;
        this.opdService = opdService;
        this.bagianService = bagianService;
        this.fileReadWriteService = fileReadWriteService;
        this.asistenService = asistenSekretarisDaerahService;
        this.pemakaiService = pemakaiService;
        this.tableDefinitionService = tableDefinitionService;
        this.columnOrderService = columnOrderService;
        this.mapper = mapper;
    }

    @PostMapping("/list")
    @ResponseBody
    public Page<DokumenKerjaSamaDTO> list(@RequestBody PagingRequest pagingRequest) {
        logger.info("Pagingrequest : [{}]", pagingRequest);
        Page<DokumenKerjaSamaDTO> page = dokumenKerjaSamaService.getDokumen(pagingRequest);
        return page;
    }

    @GetMapping("/view/{id}")
    public String lihat(@PathVariable Long id, Model model) {
        DokumenKerjaSama dok = dokumenKerjaSamaService.findById(id).get();
        model.addAttribute("dokumen", dok);
        return "pelaksanaan/dokumen-lihat";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        DokumenKerjaSama dok = dokumenKerjaSamaService.findById(id).get();
        List<JenisMitra> jenisMitra = jenisMitraService.findAll();
        List<JenisNaskah> jenisNaskah = jenisNaskahService.findAll();
        List<Bagian> bagianList = bagianService.findAll();
        List<OPD> opdList = opdService.findAll();
        List<AsistenSekretarisDaerah> rumpunOpdList = asistenService.findAll();

        model.addAttribute("jenisMitraList", jenisMitra);
        model.addAttribute("jenisNaskahList", jenisNaskah);
        model.addAttribute("bagianList", bagianList);
        model.addAttribute("opdList", opdList);
        model.addAttribute("rumpunOpdList", rumpunOpdList);

        model.addAttribute("dokumenKerjaSama", dok);

        return "pelaksanaan/dokumen-edit";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id) {
        DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaService.findById(id).get();

        if (dokumenKerjaSama != null) {
            dokumenKerjaSama.setDeleted(true);
            dokumenKerjaSamaService.save(dokumenKerjaSama);
        }

        return "redirect:/pelaksanaan";
    }

    @GetMapping("/add")
    public String showAddDocument(Model model) {
        model.addAttribute("dokumenKerjaSama", new DokumenKerjaSama());
        List<JenisMitra> jenisMitra = jenisMitraService.findAll();
        List<JenisNaskah> jenisNaskah = jenisNaskahService.findAll();
        List<OPD> opd = opdService.findAll();
        List<Bagian> bagianList = bagianService.findAll();
        List<AsistenSekretarisDaerah> rumpunOpdList = asistenService.findAll();

        model.addAttribute("jenisMitraList", jenisMitra);
        model.addAttribute("jenisNaskahList", jenisNaskah);
        model.addAttribute("opdList", opd);
        model.addAttribute("bagianList", bagianList);
        model.addAttribute("rumpunOpdList", rumpunOpdList);

        return "pelaksanaan/dokumen-add";
    }

    @PostMapping("saveNew")
    public String saveNewDokumen(@RequestParam("files") MultipartFile[] files, Model model,
            @ModelAttribute("dokumenKerjaSama") DokumenKerjaSama dokumenKerjaSama, RedirectAttributes redirectAttributes) {

        for (MultipartFile file : files) {
            if (!file.getOriginalFilename().isEmpty()) {

                try {
                    fileReadWriteService.saveFileNoHash(file);
                    Dokumen dokumen = new Dokumen();
                    dokumen.setFilename(file.getOriginalFilename());

                    dokumenKerjaSama.getDokumen().add(dokumen);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        dokumenKerjaSamaService.save(dokumenKerjaSama);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pelaksanaan";
    }

    @PostMapping("saveEdit")
    public String saveEditDokumen(@RequestParam("files") MultipartFile[] files, Model model,
            @ModelAttribute("dokumenKerjaSama") DokumenKerjaSama dokumenKerjaSama,
            RedirectAttributes redirectAttributes) {
        // for (Dokumen doc : dokumenKerjaSama.getDokumen()) {
        // String filename = doc.getFilename();
        // }

        // Catatan: tidak menghapus file - file yg telah diupload.

        for (MultipartFile file : files) {
            if (!file.getOriginalFilename().isEmpty()) {

                try {
                    fileReadWriteService.saveFileNoHash(file);
                    Dokumen dokumen = new Dokumen();
                    dokumen.setFilename(file.getOriginalFilename());

                    dokumenKerjaSama.getDokumen().add(dokumen);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // Ambil lagi dari db, lalu save
        Long id = dokumenKerjaSama.getId();
        DokumenKerjaSama existingDok = dokumenKerjaSamaService.findById(id).get();
        dokumenKerjaSama.setFileContent(existingDok.getFileContent());
        dokumenKerjaSamaService.save(dokumenKerjaSama);

        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pelaksanaan";
    }

    @PostMapping("/exportExcel2")
    public void exportExcel2(@RequestBody PagingRequest pagingRequest, HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("AUTH:::[{}]", auth);
        if (auth == null || !auth.isAuthenticated()) {
            response.reset();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter()
                    .write("{\"errorCode\":\"401 UNAUTHORIZED\",\"errorMessage\":\"Authentication failed\"}");
            return;
        }

        String baseUrl = request.getRequestURL().toString()
                .replace(request.getRequestURI(), request.getContextPath());

        pagingRequest.setUrl(baseUrl);

        ByteArrayInputStream isr = dokumenKerjaSamaService.exportExcel(pagingRequest);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");
        IOUtils.copy(isr, response.getOutputStream());

    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        fileName = fileName.replace("+", "%2B");
        fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
        logger.info("DECODED FILENAME:::[{}]", fileName);

        Resource resource = fileReadWriteService.loadFileAsResource(fileName);
        String contentType = getContentTypeByFilename(fileName); // Get content type based on filename extension

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    private String getContentTypeByFilename(String fileName) {
        // Implement logic to determine content type based on filename extension
        // You can use a library like Apache Commons MimeUtils
        // or a map of extensions to content types
        return "application/octet-stream"; // Default content type
    }

    @PostMapping("/saveTableOption")
    @ResponseStatus(HttpStatus.OK)
    public void saveTableOption(@RequestBody SelectedTableColumn options,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Selected :[{}]", options);
        // return ResponseEntity.ok("Received");
        String userName = userDetails.getUsername();
        logger.info("Username : [{}]", userName);
        Pemakai pemakai = pemakaiService.findByUserName(userName);

        Set<TableDefinition> kerjasamaDefs = tableDefinitionService.findByNamaTable("kerjasama");
        List<TableDefinition> defs = tableDefinitionService.findByIds(options.getSelectedOption());

        pemakai.getTableDefinition().removeAll(kerjasamaDefs);
        pemakai.getTableDefinition().addAll(defs);
        // pemakai.setTableDefinition(defs);

        pemakaiService.save(pemakai);
    }

    @PostMapping("/save-column-order")
    public ResponseEntity<?> saveColumnOrder(@RequestBody SelectedColumnOrder orders,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername();
        Pemakai pemakai = pemakaiService.findByUserName(userName);
        List<Integer> columnNumbers = orders.getOrders();

        List<ColumnOrder> kerjasamaColumnOrder = columnOrderService.findByNamaTable("kerjasama");
        List<ColumnOrder> columnOrder = columnOrderService.findByNamaTableAndColumnNumber("kerjasama", columnNumbers);

        logger.info("COL ORDER:::[{}]", columnOrder);

        pemakai.getColumnOrder().removeAll(kerjasamaColumnOrder);
        pemakai.getColumnOrder().addAll(columnOrder);

        pemakaiService.save(pemakai);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-column-order")
    public ResponseEntity<List<Integer>> getColumnOrder(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername();
        Pemakai pemakai = pemakaiService.findByUserName(userName);

        List<ColumnOrder> order = pemakai.getColumnOrder();
        // List<ColumnOrder> kerjasamaOrder = order.stream().filter(each ->
        // each.getNamaTable().equals("kerjasama"))
        // .collect(Collectors.toList());

        // List<Integer> colNumbers =
        // kerjasamaOrder.stream().map(ColumnOrder::getColumnNumber)
        // .collect(Collectors.toList());

        List<Integer> colNumbers = order.stream().filter(each -> each.getNamaTable().equals("kerjasama"))
                .map(ColumnOrder::getColumnNumber).collect(Collectors.toList());

        return ResponseEntity.ok(colNumbers);
    }

}
