package id.go.jakarta.bksd.dokumen.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.PemakaiDTO;
import id.go.jakarta.bksd.dokumen.service.DataUploaderService;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;

@RestController
@RequestMapping("/api/pemakai")
public class PemakaiRestController {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final PemakaiService pemakaiService;
    private final DataUploaderService dataUploaderService;

    public PemakaiRestController(PemakaiService pemakaiService, DataUploaderService dataUploaderService) {
        this.pemakaiService = pemakaiService;
        this.dataUploaderService = dataUploaderService;
    }

    @PostMapping("/list")
    public Page<PemakaiDTO> list(@RequestBody PagingRequest pagingRequest) {
        Page<PemakaiDTO> pemakai = pemakaiService.getPemakai(pagingRequest);
        return pemakai;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file) throws IOException {
        dataUploaderService.uploadData(file);
        return ResponseEntity.ok("Data uploaded successfully!");
    }

}
