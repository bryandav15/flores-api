package com.plantas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() { return "forward:/index.html"; }

    @GetMapping("/lista")
    public String lista() { return "forward:/lista.html"; }

    @GetMapping("/crear")
    public String crear() { return "forward:/crear.html"; }

    @GetMapping("/buscar")
    public String buscar() { return "forward:/buscar.html"; }
}
