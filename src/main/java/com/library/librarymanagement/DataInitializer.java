package com.library.librarymanagement;

import com.library.librarymanagement.model.Book;
import com.library.librarymanagement.model.BorrowTicket;
import com.library.librarymanagement.model.BorrowTicketDetail;
import com.library.librarymanagement.model.LecturerReader;
import com.library.librarymanagement.model.PriorityStudentReader;
import com.library.librarymanagement.model.StudentReader;
import com.library.librarymanagement.repository.BookRepository;
import com.library.librarymanagement.repository.BorrowTicketDetailRepository;
import com.library.librarymanagement.repository.BorrowTicketRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(BookRepository bookRepository,
                                     ReaderRepository readerRepository,
                                     BorrowTicketRepository borrowTicketRepository,
                                     BorrowTicketDetailRepository borrowTicketDetailRepository) {
        return args -> {
            StudentReader r1 = new StudentReader("RD001", "Nguyen Van A", "0367586982", "nam_2251220175@dau.edu.vn", "Tp Hue", "2251220171");
            PriorityStudentReader r2 = new PriorityStudentReader("RD002", "Trần Văn Long", "0912345678");
            r2.setEmail("longtran13112004@gmail.com");
            r2.setAddress("K50/11 Bùi Tá Hán, Ngũ Hành Sơn, Tp Đà Nẵng");
            r2.setStudentCode("2251220188");

            LecturerReader r3 = new LecturerReader("RD003", "TS. Lê Hoàng Nam", "0987654321");
            r3.setEmail("namle@dau.edu.vn");
            r3.setAddress("Tp Đà Nẵng");
            r3.setStudentCode("GV-0012");

            if (readerRepository.count() == 0) {
                readerRepository.saveAll(List.of(r1, r2, r3));
            }

            Book b1 = new Book("BK001", "Điểm Mù Trong Kinh Doanh - Steve Diller", "Dân Trí, Phụ Nữ Việt N...", "Kinh tế & Quản trị", 11, new BigDecimal("115000"),
                    "Minh Long", 205, 2021, "Bìa mềm", "Tiếng Việt", "/images/books/diem-mu.svg",
                    "Điểm mù trong kinh doanh phân tích những sai lầm tiềm ẩn trong chiến lược phát triển khách hàng và cách xây dựng tư duy đổi mới.", 8);

            Book b2 = new Book("BK002", "Phong Cách Lãnh Đạo Châu Á", "Thế Giới", "Lãnh đạo & Kỹ năng", 4, new BigDecimal("135000"),
                    "Cty Bán Lẻ Phương Nam", 215, 2023, "Bìa mềm", "Tiếng Việt", "/images/books/lanh-dao-chau-a.svg",
                    "Phong Cách Lãnh Đạo Châu Á Bí mật thành công và những góc khuất cuộc đời của 28 nhà lãnh đạo tài năng hàng đầu châu lục. Phong cách lãnh đạo châu Á là một cái nhìn sâu sắc về các vấn đề lãnh đạo từ góc nhìn của 28 nhân vật xuất sắc...", 10);

            Book b3 = new Book("BK003", "Cơ Hội Khởi Nghiệp", "Hải Đăng", "Khởi nghiệp", 1, new BigDecimal("98000"),
                    "Đinh Tị", 153, 2025, "Bìa cứng", "Tiếng Việt", "/images/books/co-hoi-khoi-nghiep.svg",
                    "Cơ hội khởi nghiệp hướng dẫn cách đánh giá ý tưởng, thẩm định thị trường và tìm kiếm nguồn lực đột phá cho các startup.", 5);

            Book b4 = new Book("BK004", "Trốn lên mái nhà để khóc - Lam", "NXB Trẻ", "Văn học & Tản văn", 14, new BigDecimal("85000"),
                    "NXB Trẻ", 235, 2023, "Bìa mềm", "Tiếng Việt", "/images/books/tron-len-mai-nha.svg",
                    "Trốn lên mái nhà để khóc là những rung cảm nhẹ nhàng, sâu lắng về tuổi trẻ, những vết thương và sự chữa lành từ bên trong.", 7);

            Book b5 = new Book("BK005", "Đi Ra Thế Giới Với Người Khổng Lồ", "Thế Giới", "Kinh tế quốc tế", 1, new BigDecimal("125000"),
                    "Phương Trang", 137, 2021, "Bìa cứng", "Tiếng Việt", "/images/books/di-ra-the-gioi.svg",
                    "Đi ra thế giới với người khổng lồ đúc kết bài học đàm phán hợp đồng tỷ đô và xây dựng vị thế cạnh tranh trên trường quốc tế.", 6);

            Book b6 = new Book("BK006", "Tiếng Hàn Tổng Hợp Dành Cho Người Việt Nam - Sơ Cấp 2", "MCBooks", "Ngoại ngữ", 8, new BigDecimal("160000"),
                    "MCBooks", 300, 2022, "Bìa mềm", "Tiếng Việt", "/images/books/tieng-han.svg",
                    "Giáo trình tiếng Hàn chuẩn quốc tế với đầy đủ kỹ năng Nghe - Nói - Đọc - Viết dành riêng cho người học Việt Nam.", 9);

            if (bookRepository.count() == 0) {
                bookRepository.saveAll(List.of(b1, b2, b3, b4, b5, b6));
            }

            if (borrowTicketRepository.count() == 0) {
                // Ticket 1: 6cea057a-4 - Nguyen Van A - Phong Cách Lãnh Đạo Châu Á - Chờ lấy sách
                BorrowTicket t1 = new BorrowTicket("6cea057a-4", r1, LocalDate.of(2026, 6, 21), LocalDate.of(2026, 6, 30), "WAITING_PICKUP");
                borrowTicketRepository.save(t1);
                borrowTicketDetailRepository.save(new BorrowTicketDetail(t1, b2, 1));

                // Ticket 2: 3762ebcf-d - Trần Văn Long - Phong Cách Lãnh Đạo Châu Á - Đã trả
                BorrowTicket t2 = new BorrowTicket("3762ebcf-d", r2, LocalDate.of(2026, 6, 21), LocalDate.of(2026, 6, 30), "RETURNED");
                borrowTicketRepository.save(t2);
                borrowTicketDetailRepository.save(new BorrowTicketDetail(t2, b2, 1));

                // Ticket 3: e5fb8e91-9 - Nguyen Van A - Phong Cách Lãnh Đạo Châu Á - Đã lấy sách
                BorrowTicket t3 = new BorrowTicket("e5fb8e91-9", r1, LocalDate.of(2026, 6, 21), LocalDate.of(2026, 6, 22), "BORROWING");
                borrowTicketRepository.save(t3);
                borrowTicketDetailRepository.save(new BorrowTicketDetail(t3, b2, 1));

                // Ticket 4: d4a46785-c - Trần Văn Long - Trốn lên mái nhà để khóc - Lam - Đã trả
                BorrowTicket t4 = new BorrowTicket("d4a46785-c", r2, LocalDate.of(2026, 5, 5), LocalDate.of(2026, 5, 28), "RETURNED");
                borrowTicketRepository.save(t4);
                borrowTicketDetailRepository.save(new BorrowTicketDetail(t4, b4, 1));
            }
        };
    }
}
