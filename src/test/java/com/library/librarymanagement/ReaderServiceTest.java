package com.library.librarymanagement;

import com.library.librarymanagement.controller.ReaderForm;
import com.library.librarymanagement.exception.LibraryBusinessException;
import com.library.librarymanagement.exception.ReaderNotFoundException;
import com.library.librarymanagement.model.LecturerReader;
import com.library.librarymanagement.model.PriorityStudentReader;
import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.model.StudentReader;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.service.ReaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReaderServiceTest {

    private ReaderRepository readerRepository;
    private BorrowTicketRepository ticketRepository;
    private ReaderService readerService;

    @BeforeEach
    void setUp() {
        readerRepository = mock(ReaderRepository.class);
        ticketRepository = mock(BorrowTicketRepository.class);
        readerService = new ReaderService(readerRepository, ticketRepository);
    }

    // ---------- Helper ----------

    private ReaderForm form(String name, String phone, String type) {
        ReaderForm form = new ReaderForm();
        form.setFullName(name);
        form.setPhone(phone);
        form.setReaderType(type);
        return form;
    }

    // ================= THÊM =================

    @Test
    void createFirstReaderGetsIdRD001() {
        when(readerRepository.findAll()).thenReturn(List.of());
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> inv.getArgument(0));

        Reader created = readerService.create(form("Nguyen Van An", "0901234567", "NORMAL"));

        assertEquals("RD001", created.getId());
        assertEquals("Nguyen Van An", created.getFullName());
    }

    @Test
    void createUsesNextNumberAfterHighestExistingId() {
        when(readerRepository.findAll()).thenReturn(List.of(
                new StudentReader("RD001", "A", "0900000001"),
                new StudentReader("RD007", "B", "0900000002"),
                new StudentReader("RD003", "C", "0900000003")
        ));
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> inv.getArgument(0));

        Reader created = readerService.create(form("Tran Thi Binh", "0912345678", "NORMAL"));

        assertEquals("RD008", created.getId());
    }

    @Test
    void createIgnoresIdsThatDoNotFollowRdPattern() {
        when(readerRepository.findAll()).thenReturn(List.of(
                new StudentReader("RD002", "A", "0900000001"),
                new StudentReader("ABC", "B", "0900000002"),
                new StudentReader("RDxyz", "C", "0900000003")
        ));
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> inv.getArgument(0));

        Reader created = readerService.create(form("Le Van Cuong", "0923456789", "NORMAL"));

        assertEquals("RD003", created.getId());
    }

    @Test
    void createBuildsCorrectSubclassAndBorrowLimitForEachType() {
        when(readerRepository.findAll()).thenReturn(List.of());
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> inv.getArgument(0));

        Reader student = readerService.create(form("SV", "0901111111", "NORMAL"));
        Reader priority = readerService.create(form("SV uu tien", "0902222222", "PRIORITY"));
        Reader lecturer = readerService.create(form("GV", "0903333333", "LECTURER"));

        assertInstanceOf(StudentReader.class, student);
        assertEquals(3, student.getMaxBorrowQuantity());
        assertInstanceOf(PriorityStudentReader.class, priority);
        assertEquals(5, priority.getMaxBorrowQuantity());
        assertInstanceOf(LecturerReader.class, lecturer);
        assertEquals(10, lecturer.getMaxBorrowQuantity());
    }

    @Test
    void createTrimsNameAndPhone() {
        when(readerRepository.findAll()).thenReturn(List.of());
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> inv.getArgument(0));

        Reader created = readerService.create(form("  Pham Thi Dung  ", " 0934567890 ", "NORMAL"));

        assertEquals("Pham Thi Dung", created.getFullName());
        assertEquals("0934567890", created.getPhone());
    }

    @Test
    void createStoresOptionalFields() {
        when(readerRepository.findAll()).thenReturn(List.of());
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> inv.getArgument(0));
        ReaderForm form = form("Hoang Van Em", "0945678901", "NORMAL");
        form.setEmail("  em@dau.edu.vn ");
        form.setAddress(" Ha Noi ");
        form.setStudentCode(" 2251220999 ");

        Reader created = readerService.create(form);

        assertEquals("em@dau.edu.vn", created.getEmail());
        assertEquals("Ha Noi", created.getAddress());
        assertEquals("2251220999", created.getStudentCode());
    }

    @Test
    void createRejectsUnknownReaderTypeAndSavesNothing() {
        LibraryBusinessException ex = assertThrows(
                LibraryBusinessException.class,
                () -> readerService.create(form("Ten", "0901234567", "VIP"))
        );

        assertEquals("Loại bạn đọc không hợp lệ.", ex.getMessage());
        verify(readerRepository, never()).save(any(Reader.class));
    }

    @Test
    void createRefusesWhenGeneratedIdAlreadyExists() {
        when(readerRepository.findAll()).thenReturn(List.of());
        when(readerRepository.existsById("RD001")).thenReturn(true);

        assertThrows(
                LibraryBusinessException.class,
                () -> readerService.create(form("Ten", "0901234567", "NORMAL"))
        );
        verify(readerRepository, never()).save(any(Reader.class));
    }

    @Test
    void manyConcurrentCreatesNeverProduceDuplicateIds() throws Exception {
        // Kho dữ liệu giả lập trong bộ nhớ, an toàn đa luồng
        Map<String, Reader> store = new ConcurrentHashMap<>();
        when(readerRepository.findAll()).thenAnswer(inv -> new ArrayList<>(store.values()));
        when(readerRepository.existsById(anyString())).thenAnswer(inv -> store.containsKey(inv.<String>getArgument(0)));
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> {
            Reader reader = inv.getArgument(0);
            store.put(reader.getId(), reader);
            return reader;
        });

        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startSignal = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            final int n = i;
            Callable<String> task = () -> {
                startSignal.await();
                return readerService.create(form("Doc gia " + n, "09000000" + String.format("%02d", n), "NORMAL")).getId();
            };
            futures.add(pool.submit(task));
        }
        startSignal.countDown(); // cho tất cả luồng chạy cùng lúc

        Set<String> ids = new HashSet<>();
        for (Future<String> future : futures) {
            ids.add(future.get());
        }
        pool.shutdown();

        assertEquals(threads, ids.size(), "Mỗi yêu cầu phải nhận một mã khác nhau");
        assertEquals(threads, store.size());
        assertTrue(ids.contains("RD001"));
        assertTrue(ids.contains("RD020"));
    }

    // ================= SỬA =================

    @Test
    void updateChangesEditableFieldsButKeepsTypeAndId() {
        Reader existing = new LecturerReader("RD005", "Ten cu", "0900000000");
        when(readerRepository.findById("RD005")).thenReturn(Optional.of(existing));
        when(readerRepository.save(any(Reader.class))).thenAnswer(inv -> inv.getArgument(0));
        ReaderForm form = form("  Ten moi ", "0987654321", "NORMAL"); // cố tình gửi loại khác
        form.setEmail("moi@dau.edu.vn");

        Reader updated = readerService.update("RD005", form);

        assertEquals("RD005", updated.getId());
        assertEquals("Ten moi", updated.getFullName());
        assertEquals("0987654321", updated.getPhone());
        assertEquals("moi@dau.edu.vn", updated.getEmail());
        assertEquals("LECTURER", updated.getReaderType()); // loại không bị đổi
    }

    @Test
    void updateThrowsWhenReaderDoesNotExist() {
        when(readerRepository.findById("RD999")).thenReturn(Optional.empty());

        assertThrows(
                ReaderNotFoundException.class,
                () -> readerService.update("RD999", form("Ten", "0901234567", "NORMAL"))
        );
        verify(readerRepository, never()).save(any(Reader.class));
    }

    // ================= XÓA =================

    @Test
    void deleteRemovesReaderWithoutBorrowHistory() {
        Reader reader = new StudentReader("RD002", "An", "0900000002");
        when(readerRepository.findById("RD002")).thenReturn(Optional.of(reader));
        when(ticketRepository.existsByReaderId("RD002")).thenReturn(false);

        readerService.delete("RD002");

        verify(readerRepository).delete(reader);
    }

    @Test
    void deleteIsBlockedWhenReaderHasBorrowTickets() {
        Reader reader = new StudentReader("RD003", "Binh", "0900000003");
        when(readerRepository.findById("RD003")).thenReturn(Optional.of(reader));
        when(ticketRepository.existsByReaderId("RD003")).thenReturn(true);

        LibraryBusinessException ex = assertThrows(
                LibraryBusinessException.class,
                () -> readerService.delete("RD003")
        );

        assertTrue(ex.getMessage().contains("RD003"));
        verify(readerRepository, never()).delete(any(Reader.class));
    }

    @Test
    void deleteThrowsWhenReaderDoesNotExist() {
        when(readerRepository.findById("RD404")).thenReturn(Optional.empty());

        assertThrows(ReaderNotFoundException.class, () -> readerService.delete("RD404"));
        verify(readerRepository, never()).delete(any(Reader.class));
    }

    // ================= TÌM KIẾM =================

    @Test
    void searchWithoutKeywordReturnsEveryone() {
        List<Reader> all = List.of(
                new StudentReader("RD001", "An", "0900000001"),
                new LecturerReader("RD002", "Binh", "0900000002")
        );
        when(readerRepository.findAll()).thenReturn(all);

        assertEquals(2, readerService.search("  ", "").size());
        assertEquals(2, readerService.search(null, null).size());
    }

    @Test
    void searchByKeywordUsesRepositoryQuery() {
        Reader match = new StudentReader("RD001", "Nguyen An", "0900000001");
        when(readerRepository.findByIdContainingIgnoreCaseOrFullNameContainingIgnoreCase("an", "an"))
                .thenReturn(List.of(match));

        List<Reader> result = readerService.search(" an ", "");

        assertEquals(List.of(match), result);
    }

    @Test
    void searchFiltersByReaderType() {
        when(readerRepository.findAll()).thenReturn(List.of(
                new StudentReader("RD001", "An", "0900000001"),
                new LecturerReader("RD002", "Binh", "0900000002"),
                new LecturerReader("RD003", "Cuong", "0900000003")
        ));

        List<Reader> lecturers = readerService.search("", "LECTURER");

        assertEquals(2, lecturers.size());
        assertTrue(lecturers.stream().allMatch(r -> "LECTURER".equals(r.getReaderType())));
    }

    @Test
    void searchReturnsEmptyListWhenNothingMatches() {
        when(readerRepository.findByIdContainingIgnoreCaseOrFullNameContainingIgnoreCase("zzz", "zzz"))
                .thenReturn(Collections.emptyList());

        assertTrue(readerService.search("zzz", "").isEmpty());
    }

    // ================= LẤY THEO MÃ =================

    @Test
    void getByIdThrowsWhenMissing() {
        when(readerRepository.findById("RD777")).thenReturn(Optional.empty());

        assertThrows(ReaderNotFoundException.class, () -> readerService.getById("RD777"));
    }
}
