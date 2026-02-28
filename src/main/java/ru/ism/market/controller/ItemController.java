package ru.ism.market.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ism.market.module.enums.Action;
import ru.ism.market.module.enums.Sort;
import ru.ism.market.service.ItemService;

@Controller
@RequiredArgsConstructor
public class ItemController {

    public final ItemService itemService;

    @GetMapping("/items/{id}")
    public String item(@PathVariable long id, Model model) {
        model.addAttribute("item", itemService.getItem(id));
        return "item";
    }

    @PostMapping("/items/{id}")
    public String addItemInCart(@PathVariable long id,
                                @RequestParam("action") Action action, Model model) {
        var item = itemService.addItemInCart(id, action);
        model.addAttribute("item", item);
        return "item";
    }

    @GetMapping(value = {"", "/", "/items"})
    public String items(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                        @RequestParam(value = "sort", required = false, defaultValue = "NO") Sort sort,
                        @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize,
                        Model model) {
        var items = itemService.searchItems(search, pageNumber, pageSize);
        model.addAttribute("items", items.items());
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", items.paging());
        return "items";
    }

    @PostMapping(value = {"", "/items"})
    public String addItemsInCart(@RequestParam("id") long itemId,
                                 @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                 @RequestParam(value = "sort", required = false, defaultValue = "NO") Sort sort,
                                 @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                 @RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize,
                                 @RequestParam("action") Action action) {
        itemService.addItemInCart(itemId, action);
        return String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d", search, sort, pageNumber, pageSize);
    }
}
