package ru.ism.market.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ism.market.service.CartService;
import ru.ism.market.module.enums.Action;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/items")
    public String getItemsInCart(Model model) {
        var cart = cartService.getCart();
        model.addAttribute("items", cart.items());
        model.addAttribute("total", cart.sum());
        return "cart";
    }

    @PostMapping("/items")
    public String changeItemsInCart(Model model,
                                    @RequestParam("id") long itemId,
                                    @RequestParam("action") Action action) {
        var cart = cartService.changeItemsInCart(itemId, action);
        model.addAttribute("items", cart.items());
        model.addAttribute("total", cart.sum());
        return "cart";
    }
}
