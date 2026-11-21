package com.library.librarymanagement.controller;

import com.library.librarymanagement.exception.LibraryBusinessException;
import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.service.ReaderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Controller
public class ReaderController {

    private static final String ADMIN_SESSION_ATTRIBUTE = "adminLoggedIn";
    private static final String CSRF_SESSION_ATTRIBUTE = "adminCsrfToken";

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    // ===== Danh sách + tìm kiếm + lọc =====
    @GetMapping("/readers")
    public String readers(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String type,
            HttpSession session,
            Model model) {

        model.addAttribute("readers", readerService.search(search, type));
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("csrfToken", isAdmin(session) ? csrfToken(session) : "");
        return "readers";
    }

    // ===== Thêm =====
    @GetMapping("/readers/new")
    public String newReaderPage(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!model.containsAttribute("readerForm")) {
            model.addAttribute("readerForm", new ReaderForm());
        }
        model.addAttribute("editing", false);
        model.addAttribute("csrfToken", csrfToken(session));
        return "reader-form";
    }

    @PostMapping("/readers")
    public String createReader(
            @Valid @ModelAttribute("readerForm") ReaderForm form,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!matches(csrfToken, csrfToken(session))) {
            bindingResult.reject("admin.csrf", "Phiên làm việc không hợp lệ. Hãy tải lại trang và thử lại.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", false);
            model.addAttribute("csrfToken", csrfToken(session));
            return "reader-form";
        }

        Reader saved = readerService.create(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Đã thêm độc giả " + saved.getId() + " - " + saved.getFullName() + ".");
        return "redirect:/readers";
    }

    // ===== Sửa =====
    @GetMapping("/readers/{id}/edit")
    public String editReaderPage(@PathVariable String id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!model.containsAttribute("readerForm")) {
            Reader reader = readerService.getById(id);
            ReaderForm form = new ReaderForm();
            form.setId(reader.getId());
            form.setFullName(reader.getFullName());
            form.setPhone(reader.getPhone());
            form.setEmail(reader.getEmail());
            form.setAddress(reader.getAddress());
            form.setStudentCode(reader.getStudentCode());
            form.setReaderType(reader.getReaderType());
            model.addAttribute("readerForm", form);
        }
        model.addAttribute("editing", true);
        model.addAttribute("csrfToken", csrfToken(session));
        return "reader-form";
    }

    @PostMapping("/readers/{id}")
    public String updateReader(
            @PathVariable String id,
            @Valid @ModelAttribute("readerForm") ReaderForm form,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!matches(csrfToken, csrfToken(session))) {
            bindingResult.reject("admin.csrf", "Phiên làm việc không hợp lệ. Hãy tải lại trang và thử lại.");
        }
        if (bindingResult.hasErrors()) {
            form.setId(id);
            model.addAttribute("editing", true);
            model.addAttribute("csrfToken", csrfToken(session));
            return "reader-form";
        }

        readerService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật độc giả " + id + ".");
        return "redirect:/readers";
    }

    // ===== Xóa =====
    @PostMapping("/readers/{id}/delete")
    public String deleteReader(
            @PathVariable String id,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!matches(csrfToken, csrfToken(session))) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Phiên làm việc không hợp lệ. Hãy tải lại trang và thử lại.");
            return "redirect:/readers";
        }

        try {
            readerService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa độc giả " + id + ".");
        } catch (LibraryBusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/readers";
    }

    // ===== Helpers (cùng cơ chế với AdminController) =====
    private boolean isAdmin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_ATTRIBUTE));
    }

    private String csrfToken(HttpSession session) {
        Object token = session.getAttribute(CSRF_SESSION_ATTRIBUTE);
        if (token instanceof String value) {
            return value;
        }
        String newToken = UUID.randomUUID().toString();
        session.setAttribute(CSRF_SESSION_ATTRIBUTE, newToken);
        return newToken;
    }

    private boolean matches(String provided, String expected) {
        return MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8)
        );
    }
}
