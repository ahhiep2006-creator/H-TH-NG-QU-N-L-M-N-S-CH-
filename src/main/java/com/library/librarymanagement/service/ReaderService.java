package com.library.librarymanagement.service;

import com.library.librarymanagement.controller.ReaderForm;
import com.library.librarymanagement.exception.LibraryBusinessException;
import com.library.librarymanagement.exception.ReaderNotFoundException;
import com.library.librarymanagement.model.LecturerReader;
import com.library.librarymanagement.model.PriorityStudentReader;
import com.library.librarymanagement.model.Reader;
import com.library.librarymanagement.model.StudentReader;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final BorrowTicketRepository borrowTicketRepository;

    public ReaderService(ReaderRepository readerRepository,
                         BorrowTicketRepository borrowTicketRepository) {
        this.readerRepository = readerRepository;
        this.borrowTicketRepository = borrowTicketRepository;
    }

    @Transactional(readOnly = true)
    public List<Reader> search(String keyword, String type) {
        String kw = keyword == null ? "" : keyword.trim();
        List<Reader> readers = kw.isEmpty()
                ? readerRepository.findAll()
                : readerRepository.findByIdContainingIgnoreCaseOrFullNameContainingIgnoreCase(kw, kw);

        if (type != null && !type.isBlank()) {
            readers = readers.stream()
                    .filter(reader -> type.equals(reader.getReaderType()))
                    .toList();
        }
        return readers;
    }

    @Transactional(readOnly = true)
    public Reader getById(String id) {
        return readerRepository.findById(id)
                .orElseThrow(() -> new ReaderNotFoundException("Không tìm thấy độc giả " + id + "."));
    }

    @Transactional
    public Reader create(ReaderForm form) {
        String id = nextReaderId();
        String name = form.getFullName().trim();
        String phone = form.getPhone().trim();

        Reader reader = switch (form.getReaderType()) {
            case "PRIORITY" -> new PriorityStudentReader(id, name, phone);
            case "LECTURER" -> new LecturerReader(id, name, phone);
            default -> new StudentReader(id, name, phone);
        };
        applyOptionalFields(reader, form);
        return readerRepository.save(reader);
    }

    @Transactional
    public Reader update(String id, ReaderForm form) {
        Reader reader = getById(id);
        reader.setFullName(form.getFullName().trim());
        reader.setPhone(form.getPhone().trim());
        applyOptionalFields(reader, form);
        // Không đổi loại bạn đọc vì mỗi loại là một lớp con riêng (JOINED inheritance)
        return readerRepository.save(reader);
    }

    @Transactional
    public void delete(String id) {
        Reader reader = getById(id);
        if (borrowTicketRepository.existsByReaderId(id)) {
            throw new LibraryBusinessException(
                    "Không thể xóa " + id + " vì độc giả này đã có phiếu mượn sách.");
        }
        readerRepository.delete(reader);
    }

    private void applyOptionalFields(Reader reader, ReaderForm form) {
        reader.setEmail(blankToNull(form.getEmail()));
        reader.setAddress(blankToNull(form.getAddress()));
        reader.setStudentCode(blankToNull(form.getStudentCode()));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    // Sinh mã dạng RD001, RD002... dựa trên số lớn nhất hiện có
    private String nextReaderId() {
        int max = 0;
        for (Reader reader : readerRepository.findAll()) {
            String id = reader.getId();
            if (id != null && id.startsWith("RD")) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(2)));
                } catch (NumberFormatException ignored) {
                    // bỏ qua mã không theo định dạng RDxxx
                }
            }
        }
        return String.format("RD%03d", max + 1);
    }
}
