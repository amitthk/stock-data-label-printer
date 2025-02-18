package com.amitthk.stockdatalabelprinter.service;

import com.amitthk.stockdatalabelprinter.entity.StockData;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class PDFGenerationService {

    private final VelocityEngine velocityEngine;

    public PDFGenerationService() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
    }

    public File generateStockAnalysisPdf(StockData stock) {
        try {
            // Generate HTML content using Velocity
            String htmlContent = generateHtml(stock);

            // Define output directory
            Files.createDirectories(Paths.get("generated_reports"));
            String filename = "generated_reports/" + stock.getSymbol() + "_fundamental_analysis.pdf";
            File pdfFile = new File(filename);

            // Convert HTML to PDF
            try (OutputStream os = new FileOutputStream(pdfFile)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(htmlContent, new File(".").toURI().toString());
                builder.toStream(os);
                builder.run();
            }

            return pdfFile;

        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private String generateHtml(StockData stock) {
        VelocityContext context = new VelocityContext();
        context.put("symbol", stock.getSymbol());
        context.put("companyName", stock.getCompanyName());
        context.put("peRatio", stock.getPeRatio());
//        context.put("eps", stock.getEarningsPerShare());
        context.put("marketCap", stock.getMarketCap());
        context.put("dividendYield", stock.getDividendYield());

        StringWriter writer = new StringWriter();
        Template template = velocityEngine.getTemplate("templates/stock_analysis.vm");
        template.merge(context, writer);
        return writer.toString();
    }
}
