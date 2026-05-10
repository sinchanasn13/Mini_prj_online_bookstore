package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the single-page frontend.
 * All routes redirect to index.html which handles routing via JavaScript.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}
