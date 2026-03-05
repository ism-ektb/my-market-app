package ru.ism.market.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ism.market.service.OrderService;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public String orders(Model model) {
        var orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String getOrderById(@PathVariable("id") long orderId,
                               @RequestParam(value = "newOrder", required = false, defaultValue = "false") boolean newOrder,
                               Model model) {
        var order = orderService.getOrder(orderId);
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @PostMapping("/buy")
    public String createOrder() {
        var order = orderService.buy();
        return String.format("redirect:/orders/%d?newOrder=true", order.id());
    }

}
