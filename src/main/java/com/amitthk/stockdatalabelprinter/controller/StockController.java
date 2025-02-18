package com.amitthk.stockdatalabelprinter.controller;

import com.amitthk.stockdatalabelprinter.entity.StockData;
import com.amitthk.stockdatalabelprinter.service.PDFGenerationService;
import com.amitthk.stockdatalabelprinter.service.StockAnalysisService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/stocks")
public class StockController {

    private final StockAnalysisService stockAnalysisService;
    private final PDFGenerationService pdfGenerationService;

    public StockController(StockAnalysisService stockAnalysisService, PDFGenerationService pdfGenerationService) {
        this.stockAnalysisService = stockAnalysisService;
        this.pdfGenerationService = pdfGenerationService;
    }

    @GetMapping
    public String getAllStocks(Model model) {
        List<StockData> stocks = stockAnalysisService.getAllStocks();
        model.addAttribute("stocks", stocks);
        return "stocks";
    }

    @PostMapping("/fetch")
    public String fetchStock(@RequestParam String symbol) {
        CompletableFuture<StockData> future = stockAnalysisService.fetchFundamentalData(symbol);
        return "redirect:/stocks";
    }

    @GetMapping("/{symbol}/preview")
    public String previewStock(@PathVariable String symbol, Model model) {
        StockData stock = stockAnalysisService.getStockBySymbol(symbol);
        model.addAttribute("stock", stock);
        return "preview";
    }

    @GetMapping("/download-reports")
    public ResponseEntity<ByteArrayResource> downloadReports(@RequestParam List<String> symbols) {
        try {
            List<File> pdfFiles = symbols.stream()
                    .map(stockAnalysisService::getStockBySymbol)
                    .map(pdfGenerationService::generateStockAnalysisPdf)
                    .toList();

            ByteArrayResource zipResource = new ByteArrayResource(zipFiles(pdfFiles));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock_reports.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipResource);

        } catch (Exception e) {
            throw new RuntimeException("Error generating ZIP file", e);
        }
    }

    /**
     * Utility method to create a ZIP archive from a list of files.
     */
    private byte[] zipFiles(List<File> files) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }
                    zos.closeEntry();
                }
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error creating ZIP file", e);
        }
    }
}
