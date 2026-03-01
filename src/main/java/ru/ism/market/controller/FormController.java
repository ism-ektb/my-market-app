package ru.ism.market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.ism.market.module.dto.in.ItemInDto;

@Controller
public class FormController {

    @GetMapping("/form")
    public String showForm(Model model,
                           @RequestParam(value = "ok", required = false, defaultValue = "false") boolean ok) {
        model.addAttribute("item", new ItemInDto());
        model.addAttribute("ok", ok);
        return "form";
    }

    @PostMapping("/form")
    public String processForm(@ModelAttribute ItemInDto item, @RequestParam("imageFile") MultipartFile imageFile) {
        System.out.println("Получен студент: " + item);
        System.out.println("Имя файла: " + imageFile.getOriginalFilename());
        return "redirect:/form?ok=true";
    }
}