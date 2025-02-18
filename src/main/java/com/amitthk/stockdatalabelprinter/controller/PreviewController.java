package com.amitthk.stockdatalabelprinter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ui")
public class PreviewController {

    @GetMapping("/{id}/preview")
    public String previewLabelPage(
            @PathVariable("id") Long id, // Required Path Variable
            @RequestParam(value = "width", required = false, defaultValue = "100") String width,
            @RequestParam(value = "height", required = false, defaultValue = "150") String height,
            Model model) {

        // Add attributes to Thymeleaf model
        model.addAttribute("id", id);
        model.addAttribute("width", width);
        model.addAttribute("height", height);

        return "preview"; // Maps to src/main/resources/templates/preview.html
    }
}

