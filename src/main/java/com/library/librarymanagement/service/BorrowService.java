package com.library.librarymanagement.service;

import com.library.librarymanagement.exception.BookNotFoundException;
import com.library.librarymanagement.exception.BorrowLimitExceededException;
import com.library.librarymanagement.exception.BorrowTicketAlreadyReturnedException;
import com.library.librarymanagement.exception.BorrowTicketNotFoundException;
import com.library.librarymanagement.exception.InvalidBorrowQuantityException;
import com.library.librarymanagement.exception.InvalidBorrowTicketStatusException;
import com.library.librarymanagement.exception.OutOfStockException;
import com.library.librarymanagement.exception.ReaderNotFoundException;
import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.model.BorrowTicket;
import com.library.librarymanagement.model.BorrowTicketDetail;
import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.model.ReturnRecord;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.BorrowTicketDetailRepository;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.ReturnRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class BorrowService {

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BorrowTicketRepository borrowTicketRepository;
    private final BorrowTicketDetailRepository borrowTicketDetailRepository;
    private final ReturnRecordRepository returnRecordRepository;

    private final List<FinePolicy> finePolicies;

    public BorrowService(BookRepository bookRepository,
                         ReaderRepository readerRepository,
                         BorrowTicketRepository borrowTicketRepository,
                         BorrowTicketDetailRepository borrowTicketDetailRepository,
                         ReturnRecordRepository returnRecordRepository,
                         List<FinePolicy> finePolicies) {

        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.borrowTicketRepository = borrowTicketRepository;
        this.borrowTicketDetailRepository = borrowTicketDetailRepository;
        this.returnRecordRepository = returnRecordRepository;
        this.finePolicies = List.copyOf(finePolicies);
    }

    @Transactional
    public BorrowTicket borrowBook(String readerId,
                                   String bookId,
                                   int quantity) {

        // Kiểm tra số lượng
        if (quantity <= 0) {
            throw new InvalidBorrowQuantityException(
                    "Số lượng sách mượn phải lớn hơn 0."
            );
        }

        // Tìm độc giả
        Reader reader = readerRepository.findByIdForUpdate(readerId)
                .orElseThrow(() -> new ReaderNotFoundException(
                        "Không tìm thấy độc giả có mã: " + readerId
                ));

        // Tìm sách
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                        "Không tìm thấy sách có mã: " + bookId
                ));

        // Kiểm tra số lượng sách
        if (!book.isAvailable(quantity)) {
            throw new OutOfStockException(
                    "Sách \"" + book.getTitle()
                            + "\" không đủ số lượng để mượn."
            );
        }

        // Lấy các phiếu đang mượn
        List<BorrowTicket> borrowingTickets =
                borrowTicketRepository.findByReaderIdAndStatusIn(
                        readerId,
                        List.of("BORROWING", "WAITING_PICKUP")
                );

        int currentBorrowQuantity = 0;

        for (BorrowTicket ticket : borrowingTickets) {

            List<BorrowTicketDetail> details =
                    borrowTicketDetailRepository.findByBorrowTicketId(
                            ticket.getId()
                    );

            for (BorrowTicketDetail detail : details) {
                currentBorrowQuantity += detail.getQuantity();
            }
        }

        // Kiểm tra giới hạn mượn
        if (currentBorrowQuantity + quantity
                > reader.getMaxBorrowQuantity()) {

            throw new BorrowLimitExceededException(
                    "Độc giả chỉ được mượn tối đa "
                            + reader.getMaxBorrowQuantity()
                            + " cuốn sách."
            );
        }

        // Trừ số lượng sách
        book.borrow(quantity);

        // Tạo mã phiếu
        String ticketId = UUID.randomUUID()
                .toString()
                .substring(0, 20);

        LocalDate borrowDate = LocalDate.now();

        // Thời hạn mượn 14 ngày
        LocalDate dueDate = borrowDate.plusDays(14);

        BorrowTicket ticket = new BorrowTicket(
                ticketId,
                reader,
                borrowDate,
                dueDate,
                "BORROWING"
        );

        borrowTicketRepository.save(ticket);

        // Tạo chi tiết phiếu
        BorrowTicketDetail detail =
                new BorrowTicketDetail(
                        ticket,
                        book,
                        quantity
                );

        borrowTicketDetailRepository.save(detail);

        // Lưu số lượng sách mới
        bookRepository.save(book);

        return ticket;
    }

    @Transactional
    public BorrowTicket markPickedUp(String ticketId) {
        BorrowTicket ticket = borrowTicketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new BorrowTicketNotFoundException(
                        "Không tìm thấy phiếu mượn có mã: " + ticketId
                ));
        if (!"WAITING_PICKUP".equals(ticket.getStatus())
                && !"PENDING".equals(ticket.getStatus())) {
            throw new InvalidBorrowTicketStatusException(
                    "Chỉ có thể xác nhận nhận sách với phiếu đang chờ lấy sách."
            );
        }

        ticket.setStatus("BORROWING");
        return borrowTicketRepository.save(ticket);
    }

    @Transactional
    public BorrowTicket revertPickup(String ticketId) {
        BorrowTicket ticket = borrowTicketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new BorrowTicketNotFoundException(
                        "Không tìm thấy phiếu mượn có mã: " + ticketId
                ));
        if (!"BORROWING".equals(ticket.getStatus())) {
            throw new InvalidBorrowTicketStatusException(
                    "Chỉ có thể hoàn tác phiếu đang ở trạng thái đang mượn."
            );
        }

        ticket.setStatus("WAITING_PICKUP");
        return borrowTicketRepository.save(ticket);
    }

    @Transactional
    public ReturnRecord returnBook(String ticketId) {

        // Tìm phiếu mượn
        BorrowTicket ticket = borrowTicketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new BorrowTicketNotFoundException(
                        "Không tìm thấy phiếu mượn có mã: " + ticketId
                ));

        // Kiểm tra phiếu đã trả chưa
        if ("RETURNED".equals(ticket.getStatus())) {
            throw new BorrowTicketAlreadyReturnedException(
                    "Phiếu mượn này đã được trả sách."
            );
        }
        if (!"BORROWING".equals(ticket.getStatus())) {
            throw new InvalidBorrowTicketStatusException(
                    "Không thể trả sách vì phiếu đang ở trạng thái: "
                            + ticket.getStatus()
            );
        }

        // Lấy chi tiết phiếu mượn
        List<BorrowTicketDetail> details =
                borrowTicketDetailRepository.findByBorrowTicketId(
                        ticketId
                );
        if (details.isEmpty()) {
            throw new InvalidBorrowTicketStatusException(
                    "Phiếu mượn không có thông tin sách để hoàn trả."
            );
        }

        // Ngày trả
        LocalDate returnDate = LocalDate.now();

        // Tính số ngày quá hạn
        long overdueDays = 0;

        if (returnDate.isAfter(ticket.getDueDate())) {
            overdueDays = ChronoUnit.DAYS.between(
                    ticket.getDueDate(),
                    returnDate
            );
        }

        // Chọn chính sách phạt
        FinePolicy finePolicy =
                getFinePolicy(ticket.getReader());

        BigDecimal fineAmount =
                finePolicy.calculateFine(
                        ticket.getReader(),
                        overdueDays
                );

        // Trả lại số lượng sách
        for (BorrowTicketDetail detail : details.stream()
                .sorted(Comparator.comparing(item -> item.getBook().getId()))
                .toList()) {
            Book book = bookRepository.findByIdForUpdate(
                            detail.getBook().getId()
                    )
                    .orElseThrow(() -> new BookNotFoundException(
                            "Không tìm thấy sách có mã: "
                                    + detail.getBook().getId()
                    ));

            book.returnBook(detail.getQuantity());

            bookRepository.save(book);
        }

        // Đổi trạng thái phiếu
        ticket.setStatus("RETURNED");

        borrowTicketRepository.save(ticket);

        // Tạo bản ghi trả sách
        ReturnRecord returnRecord = new ReturnRecord();

        returnRecord.setBorrowTicket(ticket);
        returnRecord.setReturnDate(returnDate);
        returnRecord.setFineAmount(fineAmount);

        return returnRecordRepository.save(returnRecord);
    }

    private FinePolicy getFinePolicy(Reader reader) {
        return finePolicies.stream()
                .filter(policy -> policy.supports(reader))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Chưa cấu hình chính sách phạt cho loại bạn đọc: "
                                + reader.getReaderType()
                ));
    }
}