package com.example.shop_project.order.controller;

import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/order")
public class OrderViewController {
    @Autowired
    OrderService orderService;
    @Autowired
    MemberService memberService;

    @GetMapping("/create")
    public String createOrder(){
        return "order/order_create";
    }

    @GetMapping("/{orderNo}")
    public String orderDetail(@PathVariable @ModelAttribute Long orderNo, Model model, Principal principal){
        model.addAttribute("detailList", orderService.getOrderDetailList(orderNo));
        model.addAttribute("order", orderService.getOrderByOrderNo(orderNo));
        model.addAttribute("member", memberService.findByEmail(principal.getName()));
        return "order/order_detail";
    }

    @GetMapping
    public String orderList(Model model, Principal principal){
        model.addAttribute("orderMap", orderService.getOrderAndDetailMap(principal));
        return "order/order_list";
    }

    @GetMapping("/{orderNo}/update")
    public String orderUpdate(@ModelAttribute @PathVariable Long orderNo){
        return "order/order_update";
    }

    @GetMapping("checkout")
    public String checkout(){
        return "order/checkout";
    }

    @GetMapping("cart")
    public String cart(){
        return "order/cart/cart";
    }
}
