package edu.comillas.icai.gitt.pat.spring.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    @GetMapping({"/", "/pistaPadel", "/pistaPadel/"})
    public String home(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "home";
    }
}
