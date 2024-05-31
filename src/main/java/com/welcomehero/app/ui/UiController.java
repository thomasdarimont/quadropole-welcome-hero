package com.welcomehero.app.ui;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class UiController {

    @GetMapping("/")
    String index() {
        return "index.html";
    }
}
