package com.navi.education.controller.web;

import com.navi.education.dto.response.ImportResult;
import com.navi.education.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ImportController {

    private static final String XLSX_MIME =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ExcelImportService importService;

    @GetMapping("/import")
    public String importPage(Model model) {
        model.addAttribute("activePage", "import");
        return "import";
    }

    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] file = importService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"navi-import-template.xlsx\"")
                .contentType(MediaType.parseMediaType(XLSX_MIME))
                .body(file);
    }

    @PostMapping("/import")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {
        model.addAttribute("activePage", "import");
        if (file == null || file.isEmpty()) {
            model.addAttribute("uploadError", "يرجى اختيار ملف Excel أولاً.");
            return "import";
        }
        try {
            ImportResult result = importService.importWorkbook(file.getInputStream());
            model.addAttribute("result", result);
        } catch (Exception e) {
            log.error("Échec de lecture du fichier Excel importé", e);
            model.addAttribute("uploadError",
                    "تعذّرت قراءة الملف. تأكد أنه ملف Excel صالح بصيغة ‎.xlsx‎ ومطابق للنموذج.");
        }
        return "import";
    }
}
