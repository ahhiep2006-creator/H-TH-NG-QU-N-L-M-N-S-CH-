package com.library.librarymanagement.controller;

import com.library.librarymanagement.model.SupportFeedback;
import com.library.librarymanagement.repository.SupportFeedbackRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Controller
public class SupportController {

    private final SupportFeedbackRepository feedbackRepository;

    public SupportController(SupportFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping("/contact")
    public String contact(@RequestParam(defaultValue = "") String lookupEmail, HttpSession session, Model model) {
        Object sessionEmail = session.getAttribute("supportFeedbackEmail");
        String emailToLookup = lookupEmail.isBlank() && sessionEmail instanceof String savedEmail ? savedEmail : lookupEmail.trim();
        if (!emailToLookup.isBlank()) {
            model.addAttribute("myFeedbackItems", feedbackRepository.findAllByEmailIgnoreCaseOrderByCreatedAtDesc(emailToLookup));
            model.addAttribute("trackedEmail", emailToLookup);
        }
        model.addAttribute("lookupEmail", emailToLookup);
        return "contact";
    }

    @PostMapping("/contact/feedback")
    public String submitFeedback(
            @RequestParam(defaultValue = "") String studentName,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String message,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String name = studentName.trim();
        String replyEmail = email.trim();
        String topic = category.trim();
        String details = message.trim();

        if (name.isBlank() || name.length() > 100
                || !replyEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$") || replyEmail.length() > 150
                || topic.isBlank() || topic.length() > 100
                || details.length() < 10 || details.length() > 3000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra họ tên, email, chủ đề và nội dung (tối thiểu 10 ký tự). ");
            redirectAttributes.addFlashAttribute("feedbackName", name);
            redirectAttributes.addFlashAttribute("feedbackEmail", replyEmail);
            redirectAttributes.addFlashAttribute("feedbackCategory", topic);
            redirectAttributes.addFlashAttribute("feedbackMessage", details);
            return "redirect:/contact";
        }

        feedbackRepository.save(new SupportFeedback(name, replyEmail, topic, details));
        session.setAttribute("supportFeedbackEmail", replyEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu đã được gửi và được thêm vào danh sách theo dõi bên dưới.");
        return "redirect:/contact";
    }

    @GetMapping("/admin/support")
    public String adminSupport(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        List<SupportFeedback> feedback = feedbackRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("feedbackItems", feedback);
        model.addAttribute("openFeedbackCount", feedbackRepository.countByResolvedFalse());
        model.addAttribute("resolvedFeedbackCount", feedback.size() - feedbackRepository.countByResolvedFalse());
        model.addAttribute("csrfToken", session.getAttribute("adminCsrfToken"));
        return "admin-support";
    }

    @PostMapping("/admin/support/{id}/resolve")
    public String resolveFeedback(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (!hasValidCsrfToken(csrfToken, session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc hết hạn. Hãy tải lại trang rồi thử lại.");
            return "redirect:/admin/support";
        }
        // A status-only update is kept for older admin pages; a response is submitted through /reply.
        feedbackRepository.findById(id).ifPresent(feedback -> feedback.markInProgress());
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái yêu cầu.");
        return "redirect:/admin/support";
    }

    @PostMapping("/admin/support/{id}/reply")
    public String replyFeedback(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String reply,
            @RequestParam(defaultValue = "") String csrfToken,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        if (!hasValidCsrfToken(csrfToken, session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc hết hạn. Hãy tải lại trang rồi thử lại.");
            return "redirect:/admin/support";
        }
        String cleanReply = reply.trim();
        if (cleanReply.isBlank() || cleanReply.length() > 3000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nội dung phản hồi không được để trống và tối đa 3.000 ký tự.");
            return "redirect:/admin/support";
        }
        feedbackRepository.findById(id).ifPresent(item -> item.reply(cleanReply));
        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi phản hồi vào tài khoản theo dõi của sinh viên.");
        return "redirect:/admin/support";
    }

    @PostMapping("/admin/support/{id}/progress")
    public String markInProgress(@PathVariable Long id, @RequestParam(defaultValue = "") String csrfToken,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        if (!hasValidCsrfToken(csrfToken, session)) return "redirect:/admin/support";
        feedbackRepository.findById(id).ifPresent(feedback -> feedback.markInProgress());
        redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu đã được chuyển sang trạng thái đang xử lý.");
        return "redirect:/admin/support";
    }

    private boolean isAdmin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }

    private boolean hasValidCsrfToken(String provided, HttpSession session) {
        Object stored = session.getAttribute("adminCsrfToken");
        return stored instanceof String expected && MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8));
    }
}
