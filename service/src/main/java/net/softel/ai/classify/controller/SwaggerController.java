package net.softel.ai.classify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping(value = {"/", "/swagger", "/sandbox", "/swagger-ui.html"})
    public String index(Model model) {
        return "/api-docs-ui";
    }
}
