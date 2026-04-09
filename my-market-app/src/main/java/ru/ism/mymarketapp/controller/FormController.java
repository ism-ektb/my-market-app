package ru.ism.mymarketapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.ism.mymarketapp.module.dto.in.ItemInDto;
import ru.ism.mymarketapp.service.ImageService;
import ru.ism.mymarketapp.service.ItemService;

import java.io.IOException;

/**
 * Вспомогательный контроллер для пополнения данными БД магазина
 */
@Controller
@RequiredArgsConstructor
public class FormController {

    private final ImageService imageService;
    private final ItemService itemService;

    @GetMapping("/form")
    public Mono<String> showForm(Model model,
                                 @RequestParam(value = "ok", required = false, defaultValue = "") String ok) {
        model.addAttribute("item", new ItemInDto());
        model.addAttribute("ok", ok);
        return Mono.empty().thenReturn("form");
    }

    @PostMapping("/form")
    public Mono<String> processForm(@ModelAttribute ItemInDto item,
                                    @RequestPart("imageFile") FilePart photo) {
        try {
            return itemService.createItem(item)
                    .flatMap(item_id ->
                            imageService.savePhoto(item_id, photo))
                    .thenReturn("redirect:/form?ok=ok");
        } catch (Exception e) {
            return Mono.empty().thenReturn("redirect:/form?ok=error");
        }
    }

    @GetMapping(value = "image/{id}", produces = MediaType.ALL_VALUE)
    public Mono<ResponseEntity<byte[]>> getPhoto(@PathVariable Long id) {
        return imageService.getPhoto(id)
                .map(photo -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(photo.getImage())
                );
    }
}