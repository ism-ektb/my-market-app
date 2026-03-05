package ru.ism.market.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.ism.market.module.dto.in.ItemInDto;
import ru.ism.market.service.ItemService;

/**
 * Вспомогательный контроллер для пополнения данными БД магазина
 */
@Controller
@RequiredArgsConstructor
public class FormController {

    private final ItemService itemService;

    @GetMapping("/form")
    public String showForm(Model model,
                           @RequestParam(value = "ok", required = false, defaultValue = "") String ok) {
        model.addAttribute("item", new ItemInDto());
        model.addAttribute("ok", ok);
        return "form";
    }

    @PostMapping("/form")
    public String processForm(@ModelAttribute ItemInDto item,
                              @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            itemService.createItem(item, imageFile);
        } catch (Exception e) {
            return "redirect:/form?ok=error";
        }
        return "redirect:/form?ok=ok";
    }
}