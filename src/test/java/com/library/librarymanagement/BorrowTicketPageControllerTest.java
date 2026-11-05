package com.library.librarymanagement;

import com.library.librarymanagement.controller.BorrowTicketPageController;
import com.library.librarymanagement.repository.BorrowTicketDetailRepository;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class BorrowTicketPageControllerTest {

    private BorrowService borrowService;
    private BorrowTicketPageController controller;

    @BeforeEach
    void setUp() {
        borrowService = mock(BorrowService.class);
        controller = new BorrowTicketPageController(
                mock(BorrowTicketRepository.class),
                mock(BorrowTicketDetailRepository.class),
                borrowService
        );
    }

    @Test
    void rejectsTicketManagementActionsWithoutAdminSession() {
        MockHttpSession session = new MockHttpSession();

        assertEquals("redirect:/admin/login", controller.pickupTicket(
                "T001", "", session, new RedirectAttributesModelMap()));
        assertEquals("redirect:/admin/login", controller.revertTicket(
                "T001", "", session, new RedirectAttributesModelMap()));
        assertEquals("redirect:/admin/login", controller.returnBook(
                "T001", "", session, new RedirectAttributesModelMap()));

        verifyNoInteractions(borrowService);
    }

    @Test
    void rejectsTicketManagementActionsWithInvalidCsrfToken() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("adminLoggedIn", true);
        session.setAttribute("adminCsrfToken", "expected-token");

        assertEquals("redirect:/tickets", controller.pickupTicket(
                "T001", "invalid-token", session, new RedirectAttributesModelMap()));
        assertEquals("redirect:/tickets", controller.revertTicket(
                "T001", "invalid-token", session, new RedirectAttributesModelMap()));
        assertEquals("redirect:/tickets", controller.returnBook(
                "T001", "invalid-token", session, new RedirectAttributesModelMap()));

        verifyNoInteractions(borrowService);
    }
}