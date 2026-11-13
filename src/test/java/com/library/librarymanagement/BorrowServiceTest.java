package com.library.librarymanagement;

import com.library.librarymanagement.exception.BorrowLimitExceededException;
import com.library.librarymanagement.exception.BorrowTicketAlreadyReturnedException;
import com.library.librarymanagement.exception.BorrowTicketNotFoundException;
import com.library.librarymanagement.exception.InvalidBorrowQuantityException;
import com.library.librarymanagement.exception.InvalidBorrowTicketStatusException;
import com.library.librarymanagement.exception.OutOfStockException;
import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.model.BorrowTicket;
import com.library.librarymanagement.model.BorrowTicketDetail;
import com.library.librarymanagement.model.LecturerReader;
import com.library.librarymanagement.model.PriorityStudentReader;
import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.model.ReturnRecord;
import com.library.librarymanagement.model.StudentReader;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.BorrowTicketDetailRepository;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.ReturnRecordRepository;
import com.library.librarymanagement.service.BorrowService;
import com.library.librarymanagement.service.FinePolicy;
import com.library.librarymanagement.service.LecturerFinePolicy;
import com.library.librarymanagement.service.NormalStudentFinePolicy;
import com.library.librarymanagement.service.PriorityStudentFinePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BorrowServiceTest {

    private BookRepository bookRepository;
    private ReaderRepository readerRepository;
    private BorrowTicketRepository ticketRepository;
    private BorrowTicketDetailRepository detailRepository;
    private ReturnRecordRepository returnRecordRepository;
    private BorrowService borrowService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        readerRepository = mock(ReaderRepository.class);
        ticketRepository = mock(BorrowTicketRepository.class);
        detailRepository = mock(BorrowTicketDetailRepository.class);
        returnRecordRepository = mock(ReturnRecordRepository.class);

        List<FinePolicy> policies = List.of(
                new NormalStudentFinePolicy(),
                new PriorityStudentFinePolicy(),
                new LecturerFinePolicy()
        );
        borrowService = new BorrowService(
                bookRepository,
                readerRepository,
                ticketRepository,
                detailRepository,
                returnRecordRepository,
                policies
        );
    }

    @Test
    void borrowsAvailableBookAndReducesStock() {
        Reader reader = new StudentReader("R001", "Nguyen An", "0900000001");
        Book book = book("B001", 4);
        when(readerRepository.findByIdForUpdate("R001"))
                .thenReturn(Optional.of(reader));
        when(bookRepository.findByIdForUpdate("B001"))
                .thenReturn(Optional.of(book));
        when(ticketRepository.findByReaderIdAndStatusIn(
                "R001", List.of("BORROWING", "WAITING_PICKUP")))
                .thenReturn(List.of());

        BorrowTicket ticket = borrowService.borrowBook("R001", "B001", 2);

        assertEquals("R001", ticket.getReader().getId());
        assertEquals(2, book.getQuantity());
        verify(ticketRepository).save(ticket);
        verify(detailRepository).save(any(BorrowTicketDetail.class));
        verify(bookRepository).save(book);
    }

    @Test
    void rejectsBookWhenRequestedQuantityIsUnavailable() {
        when(readerRepository.findByIdForUpdate("R001"))
                .thenReturn(Optional.of(
                        new StudentReader("R001", "Nguyen An", "0900000001")
                ));
        when(bookRepository.findByIdForUpdate("B001"))
                .thenReturn(Optional.of(book("B001", 0)));

        assertThrows(
                OutOfStockException.class,
                () -> borrowService.borrowBook("R001", "B001", 1)
        );
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void rejectsZeroOrNegativeBorrowQuantityBeforeLoadingRecords() {
        assertThrows(
                InvalidBorrowQuantityException.class,
                () -> borrowService.borrowBook("R001", "B001", 0)
        );
        verifyNoInteractions(readerRepository, bookRepository);
    }

    @Test
    void rejectsBorrowThatExceedsReaderLimit() {
        Reader reader = new StudentReader("R001", "Nguyen An", "0900000001");
        BorrowTicket currentTicket = new BorrowTicket(
                "T001", reader, LocalDate.now(), LocalDate.now().plusDays(14),
                "BORROWING"
        );
        Book book = book("B001", 4);
        when(readerRepository.findByIdForUpdate("R001"))
                .thenReturn(Optional.of(reader));
        when(bookRepository.findByIdForUpdate("B001"))
                .thenReturn(Optional.of(book));
        when(ticketRepository.findByReaderIdAndStatusIn(
                "R001", List.of("BORROWING", "WAITING_PICKUP")))
                .thenReturn(List.of(currentTicket));
        when(detailRepository.findByBorrowTicketId("T001"))
                .thenReturn(List.of(new BorrowTicketDetail(currentTicket, book, 3)));

        assertThrows(
                BorrowLimitExceededException.class,
                () -> borrowService.borrowBook("R001", "B001", 1)
        );
        assertEquals(4, book.getQuantity());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void chargesNormalStudentForEachOverdueDay() {
        Reader reader = new StudentReader("R001", "Nguyen An", "0900000001");
        Book book = book("B001", 0);
        BorrowTicket ticket = new BorrowTicket(
                "T001", reader, LocalDate.now().minusDays(15),
                LocalDate.now().minusDays(1), "BORROWING"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));
        when(detailRepository.findByBorrowTicketId("T001"))
                .thenReturn(List.of(new BorrowTicketDetail(ticket, book, 1)));
        when(bookRepository.findByIdForUpdate("B001"))
                .thenReturn(Optional.of(book));
        when(returnRecordRepository.save(any(ReturnRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReturnRecord record = borrowService.returnBook("T001");

        assertEquals(new BigDecimal("5000"), record.getFineAmount());
        assertEquals(1, book.getQuantity());
        assertEquals("RETURNED", ticket.getStatus());
    }

    @Test
    void chargesPriorityStudentAndLecturerAccordingToTheirPolicies() {
        Reader priority = new PriorityStudentReader("R002", "Tran Binh", "0900000002");
        Book book1 = book("B001", 1);
        BorrowTicket priorityTicket = new BorrowTicket(
                "T002", priority, LocalDate.now().minusDays(17),
                LocalDate.now().minusDays(3), "BORROWING"
        );
        when(ticketRepository.findByIdForUpdate("T002"))
                .thenReturn(Optional.of(priorityTicket));
        when(detailRepository.findByBorrowTicketId("T002"))
                .thenReturn(List.of(new BorrowTicketDetail(priorityTicket, book1, 2)));
        when(bookRepository.findByIdForUpdate("B001"))
                .thenReturn(Optional.of(book1));

        Reader lecturer = new LecturerReader("R003", "Le Chi", "0900000003");
        Book book2 = book("B002", 5);
        BorrowTicket lecturerTicket = new BorrowTicket(
                "T003", lecturer, LocalDate.now().minusDays(18),
                LocalDate.now().minusDays(4), "BORROWING"
        );
        when(ticketRepository.findByIdForUpdate("T003"))
                .thenReturn(Optional.of(lecturerTicket));
        when(detailRepository.findByBorrowTicketId("T003"))
                .thenReturn(List.of(new BorrowTicketDetail(lecturerTicket, book2, 1)));
        when(bookRepository.findByIdForUpdate("B002"))
                .thenReturn(Optional.of(book2));

        when(returnRecordRepository.save(any(ReturnRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReturnRecord priorityRecord = borrowService.returnBook("T002");
        ReturnRecord lecturerRecord = borrowService.returnBook("T003");

        assertEquals(new BigDecimal("9000"), priorityRecord.getFineAmount());
        assertEquals(3, book1.getQuantity());
        assertEquals(new BigDecimal("8000"), lecturerRecord.getFineAmount());
        assertEquals(6, book2.getQuantity());
    }

    @Test
    void returnsBookOnTimeWithZeroFineAndRestoresStockOnce() {
        Reader reader = new StudentReader("R001", "Nguyen An", "0900000001");
        Book book = book("B001", 2);
        BorrowTicket ticket = new BorrowTicket(
                "T001", reader, LocalDate.now().minusDays(5),
                LocalDate.now(), "BORROWING"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));
        when(detailRepository.findByBorrowTicketId("T001"))
                .thenReturn(List.of(new BorrowTicketDetail(ticket, book, 2)));
        when(bookRepository.findByIdForUpdate("B001"))
                .thenReturn(Optional.of(book));
        when(returnRecordRepository.save(any(ReturnRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReturnRecord record = borrowService.returnBook("T001");

        assertEquals(BigDecimal.ZERO, record.getFineAmount());
        assertEquals(LocalDate.now(), record.getReturnDate());
        assertEquals(4, book.getQuantity());
        assertEquals("RETURNED", ticket.getStatus());
        verify(bookRepository, times(1)).save(book);
        verify(returnRecordRepository, times(1)).save(any(ReturnRecord.class));
    }

    @Test
    void rejectsReturningNonExistentOrBlankTicket() {
        when(ticketRepository.findByIdForUpdate("T404"))
                .thenReturn(Optional.empty());

        assertThrows(
                BorrowTicketNotFoundException.class,
                () -> borrowService.returnBook("T404")
        );
        assertThrows(
                BorrowTicketNotFoundException.class,
                () -> borrowService.returnBook("   ")
        );
        verify(returnRecordRepository, never()).save(any());
    }

    @Test
    void rejectsReturningTicketWithInvalidStatus() {
        BorrowTicket waitingTicket = new BorrowTicket(
                "T001",
                new StudentReader("R001", "Nguyen An", "0900000001"),
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                "WAITING_PICKUP"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(waitingTicket));

        assertThrows(
                InvalidBorrowTicketStatusException.class,
                () -> borrowService.returnBook("T001")
        );
        verify(bookRepository, never()).save(any());
        verify(returnRecordRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateReturnWhenReturnRecordAlreadyExists() {
        BorrowTicket ticket = new BorrowTicket(
                "T001",
                new StudentReader("R001", "Nguyen An", "0900000001"),
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(12),
                "BORROWING"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));
        when(returnRecordRepository.existsByBorrowTicketId("T001"))
                .thenReturn(true);

        assertThrows(
                BorrowTicketAlreadyReturnedException.class,
                () -> borrowService.returnBook("T001")
        );
        verify(returnRecordRepository, never()).save(any());
    }

    @Test
    void rejectsInvalidReturnQuantityWithoutUpdatingTicketOrRecord() {
        Reader reader = new StudentReader("R001", "Nguyen An", "0900000001");
        Book book = book("B001", 3);
        BorrowTicket ticket = new BorrowTicket(
                "T001", reader, LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(12), "BORROWING"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));
        when(detailRepository.findByBorrowTicketId("T001"))
                .thenReturn(List.of(new BorrowTicketDetail(ticket, book, 0)));

        assertThrows(
                InvalidBorrowQuantityException.class,
                () -> borrowService.returnBook("T001")
        );
        assertEquals(3, book.getQuantity());
        verify(ticketRepository, never()).save(any());
        verify(returnRecordRepository, never()).save(any());
    }

    @Test
    void confirmsPickupOnlyForWaitingTicket() {
        BorrowTicket ticket = new BorrowTicket(
                "T001", new StudentReader("R001", "Nguyen An", "0900000001"),
                LocalDate.now(), LocalDate.now().plusDays(14), "WAITING_PICKUP"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));

        borrowService.markPickedUp("T001");

        assertEquals("BORROWING", ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void rejectsRevertingReturnedTicket() {
        BorrowTicket ticket = new BorrowTicket(
                "T001", new StudentReader("R001", "Nguyen An", "0900000001"),
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), "RETURNED"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                InvalidBorrowTicketStatusException.class,
                () -> borrowService.revertPickup("T001")
        );
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void revertsOnlyBorrowingTicketToWaitingPickup() {
        BorrowTicket ticket = new BorrowTicket(
                "T001", new StudentReader("R001", "Nguyen An", "0900000001"),
                LocalDate.now(), LocalDate.now().plusDays(14), "BORROWING"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));

        borrowService.revertPickup("T001");

        assertEquals("WAITING_PICKUP", ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void rejectsReturningTicketThatWasAlreadyReturned() {
        BorrowTicket ticket = new BorrowTicket(
                "T001",
                new StudentReader("R001", "Nguyen An", "0900000001"),
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(12),
                "RETURNED"
        );
        when(ticketRepository.findByIdForUpdate("T001"))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                BorrowTicketAlreadyReturnedException.class,
                () -> borrowService.returnBook("T001")
        );
        verify(returnRecordRepository, never()).save(any());
    }

    @Test
    void finePoliciesApplyTheConfiguredDailyRates() {
        Reader normal = new StudentReader("R001", "Nguyen An", "0900000001");
        Reader priority = new PriorityStudentReader(
                "R002", "Tran Binh", "0900000002");
        Reader lecturer = new LecturerReader(
                "R003", "Le Chi", "0900000003");

        assertEquals(
                new BigDecimal("10000"),
                new NormalStudentFinePolicy().calculateFine(normal, 2)
        );
        assertEquals(
                new BigDecimal("6000"),
                new PriorityStudentFinePolicy().calculateFine(priority, 2)
        );
        assertEquals(
                new BigDecimal("4000"),
                new LecturerFinePolicy().calculateFine(lecturer, 2)
        );
        assertEquals(
                BigDecimal.ZERO,
                new NormalStudentFinePolicy().calculateFine(normal, 0)
        );
    }

    private Book book(String id, int quantity) {
        return new Book(
                id, "Lap trinh Java", "Tac gia", "Cong nghe",
                quantity, new BigDecimal("100000")
        );
    }
}
