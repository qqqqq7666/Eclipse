package com.example.shop_project.admin.controller;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    private final ProductService productService;
    private final MemberService memberService;

    public AdminProductController(ProductService productService, MemberService memberService) {
        this.productService = productService;
        this.memberService = memberService;
    }

    @GetMapping("/products")
    public String productList(@RequestParam(value = "search", required = false, defaultValue = "") String search,
                              @RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                              @RequestParam(value = "size", required = false, defaultValue = "8") int size,
                              Model model,
                              Principal principal) {
        Page<ProductResponseDto> productPage = productService.getProductList(null, search, sort, page, size);

        // 페이지네이션 블록 설정
        int blockSize = 5; // 페이지 블록 크기 설정
        int totalPages = productPage.getTotalPages();

        int currentBlock = page / blockSize; // 현재 블록 계산
        int startPage = currentBlock * blockSize + 1; // 시작 페이지 계산
        int endPage = Math.min(startPage + blockSize - 1, totalPages); // 끝 페이지 계산

        String email = principal.getName();
        Member member = memberService.findByEmail(email);
        String currentNickname = member.getNickname();

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("search", search);
        model.addAttribute("currentNickname", currentNickname);
        return "admin/productManage";
    }
}
