# Phân công công việc và phạm vi dòng code

Tài liệu này chia phạm vi commit cho 6 thành viên theo bảng phân công của nhóm và cấu trúc code hiện tại.

## Cách dùng

- Các số dòng dưới đây đối chiếu với commit `9f25ac1` (`Initial commit: Khoi tao du an quan ly thu vien`); sau khi sửa code, số dòng có thể thay đổi.
- Đây là phân công quyền sở hữu phần việc cho các commit tiếp theo, **không phải** kết luận ai đã viết các dòng code trong commit hiện tại. Hiện GitHub mới có commit khởi tạo chung.
- Mỗi thành viên tạo nhánh riêng, chỉ commit phần được giao và mở pull request để trưởng nhóm review/merge. Không tạo commit giả để chia lại lịch sử.
- Dòng được tính từ 1; các khoảng `1-N` bao gồm toàn bộ file. Tệp ảnh nhị phân không có phạm vi dòng.
- Thay `[Họ tên thành viên N]` bằng tên thật trước khi nộp báo cáo.

## Thành viên 1 — Trưởng nhóm, Git và chất lượng chung

**Phần việc:** quản lý nhánh/PR, review và tích hợp; giữ cấu hình build, xử lý lỗi dùng chung và cập nhật bảng đóng góp.

| File/phạm vi | Nội dung |
|---|---|
| `.gitattributes` dòng 1-2 | Quy tắc chuẩn hóa file |
| `.gitignore` dòng 1-34 | Quy tắc file không đưa lên Git |
| `pom.xml` dòng 1-91 | Khai báo cấu hình và dependencies Maven |
| `src/main/java/com/library/librarymanagement/exception/BookNotFoundException.java` dòng 1-8 | Lỗi không tìm thấy sách |
| `src/main/java/com/library/librarymanagement/exception/BorrowLimitExceededException.java` dòng 1-8 | Lỗi vượt hạn mức mượn |
| `src/main/java/com/library/librarymanagement/exception/BorrowTicketAlreadyReturnedException.java` dòng 1-8 | Lỗi trả phiếu lần nữa |
| `src/main/java/com/library/librarymanagement/exception/BorrowTicketNotFoundException.java` dòng 1-8 | Lỗi không tìm thấy phiếu |
| `src/main/java/com/library/librarymanagement/exception/GlobalExceptionHandler.java` dòng 1-96 | Xử lý lỗi chung |
| `src/main/java/com/library/librarymanagement/exception/InvalidBorrowQuantityException.java` dòng 1-8 | Lỗi số lượng mượn không hợp lệ |
| `src/main/java/com/library/librarymanagement/exception/InvalidBorrowTicketStatusException.java` dòng 1-8 | Lỗi trạng thái phiếu không hợp lệ |
| `src/main/java/com/library/librarymanagement/exception/LibraryBusinessException.java` dòng 1-8 | Lớp lỗi nghiệp vụ cơ sở |
| `src/main/java/com/library/librarymanagement/exception/OutOfStockException.java` dòng 1-8 | Lỗi sách không đủ tồn kho |
| `src/main/java/com/library/librarymanagement/exception/ReaderNotFoundException.java` dòng 1-8 | Lỗi không tìm thấy bạn đọc |
| `docs/team-contributions-template.md` dòng 1-12 | Cập nhật biểu mẫu báo cáo đóng góp thành 6 thành viên |

## Thành viên 2 — Mô hình dữ liệu và sơ đồ lớp

**Phần việc:** mô hình hóa sách, bạn đọc, phiếu mượn/trả và các quan hệ; cập nhật sơ đồ lớp khớp với code.

| File/phạm vi | Nội dung |
|---|---|
| `src/main/java/com/library/librarymanagement/model/Book.java` dòng 1-219 | Mô hình sách và tồn kho |
| `src/main/java/com/library/librarymanagement/model/Borrowable.java` dòng 1-10 | Hành vi có thể mượn |
| `src/main/java/com/library/librarymanagement/model/BorrowTicket.java` dòng 1-81 | Phiếu mượn |
| `src/main/java/com/library/librarymanagement/model/BorrowTicketDetail.java` dòng 1-73 | Chi tiết phiếu mượn |
| `src/main/java/com/library/librarymanagement/model/BorrowTicketDetailId.java` dòng 1-56 | Khóa định danh chi tiết phiếu |
| `src/main/java/com/library/librarymanagement/model/LecturerReader.java` dòng 1-23 | Bạn đọc giảng viên |
| `src/main/java/com/library/librarymanagement/model/PriorityStudentReader.java` dòng 1-23 | Bạn đọc sinh viên ưu tiên |
| `src/main/java/com/library/librarymanagement/model/Reader.java` dòng 1-54 | Bạn đọc cơ sở |
| `src/main/java/com/library/librarymanagement/model/ReturnRecord.java` dòng 1-59 | Bản ghi trả sách |
| `src/main/java/com/library/librarymanagement/model/StudentReader.java` dòng 1-30 | Bạn đọc sinh viên |
| `src/main/java/com/library/librarymanagement/model/SupportFeedback.java` dòng 1-93 | Phản hồi hỗ trợ |
| `src/main/java/com/library/librarymanagement/model/User.java` dòng 1-92 | Người dùng |
| `docs/class-diagram.md` dòng 1-73 | Sơ đồ lớp và mô tả quan hệ |

## Thành viên 3 — Repository, lưu trữ và nhập/xuất dữ liệu

**Phần việc:** lớp truy cập dữ liệu, cấu hình cơ sở dữ liệu, dữ liệu ban đầu và xuất danh sách sách CSV.

| File/phạm vi | Nội dung |
|---|---|
| `src/main/java/com/library/librarymanagement/repository/BookRepository.java` dòng 1-23 | Truy cập dữ liệu sách |
| `src/main/java/com/library/librarymanagement/repository/BorrowTicketDetailRepository.java` dòng 1-13 | Truy cập chi tiết phiếu |
| `src/main/java/com/library/librarymanagement/repository/BorrowTicketRepository.java` dòng 1-29 | Truy cập phiếu mượn |
| `src/main/java/com/library/librarymanagement/repository/ReaderRepository.java` dòng 1-17 | Truy cập bạn đọc |
| `src/main/java/com/library/librarymanagement/repository/ReturnRecordRepository.java` dòng 1-12 | Truy cập bản ghi trả |
| `src/main/java/com/library/librarymanagement/repository/SupportFeedbackRepository.java` dòng 1-12 | Truy cập phản hồi |
| `src/main/java/com/library/librarymanagement/DataInitializer.java` dòng 1-96 | Khởi tạo dữ liệu mẫu |
| `src/main/java/com/library/librarymanagement/service/BookCsvExportService.java` dòng 1-64 | Tạo file CSV danh sách sách |
| `src/main/resources/application.properties` dòng 1-21 | Cấu hình ứng dụng mặc định |
| `src/main/resources/application-mysql.properties` dòng 1-17 | Cấu hình MySQL |
| `src/main/resources/schema.sql` dòng 1-70 | Lược đồ cơ sở dữ liệu |
| `exports/books.csv` dòng 1-6 | File CSV xuất mẫu |
| `src/test/java/com/library/librarymanagement/BookCsvExportServiceTest.java` dòng 1-45 | Kiểm thử xuất CSV |

## Thành viên 4 — Nghiệp vụ mượn và quản lý phiếu

**Phần việc:** tạo phiếu mượn, kiểm tra điều kiện mượn, xác nhận/hoàn tác nhận sách và trang hiển thị/quản lý phiếu. Trong `BorrowService.java`, chỉ sửa vùng nghiệp vụ được giao; không định dạng lại toàn file.

| File/phạm vi | Nội dung |
|---|---|
| `src/main/java/com/library/librarymanagement/service/BorrowService.java` dòng 58-192 | Mượn sách, xác nhận nhận sách và hoàn tác trạng thái |
| `src/main/java/com/library/librarymanagement/controller/BorrowController.java` dòng 1-33 | API mượn sách và điều phối API phiếu |
| `src/main/java/com/library/librarymanagement/controller/BorrowPageController.java` dòng 1-85 | Trang và form mượn |
| `src/main/java/com/library/librarymanagement/controller/BorrowTicketPageController.java` dòng 1-172 | Trang phiếu, xác nhận/hoàn tác nhận sách và điều phối thao tác trên phiếu |
| `src/main/java/com/library/librarymanagement/controller/BorrowTicketItemDto.java` dòng 1-91 | Dữ liệu hiển thị phiếu |
| `src/main/resources/templates/borrow.html` dòng 1-92 | Giao diện mượn sách |
| `src/main/resources/templates/tickets.html` dòng 1-181 | Giao diện danh sách và thao tác phiếu |

## Thành viên 5 — Nghiệp vụ trả sách và tiền phạt

**Phần việc:** trả sách, cập nhật tồn kho/trạng thái, tính phí phạt theo loại bạn đọc và giao diện trả sách.

| File/phạm vi | Nội dung |
|---|---|
| `src/main/java/com/library/librarymanagement/service/BorrowService.java` dòng 194-290 | Xử lý trả sách, tính tiền phạt và chọn chính sách |
| `src/main/java/com/library/librarymanagement/service/FinePolicy.java` dòng 1-12 | Hợp đồng chính sách phạt |
| `src/main/java/com/library/librarymanagement/service/LecturerFinePolicy.java` dòng 1-26 | Chính sách phạt giảng viên |
| `src/main/java/com/library/librarymanagement/service/NormalStudentFinePolicy.java` dòng 1-26 | Chính sách phạt sinh viên thường |
| `src/main/java/com/library/librarymanagement/service/PriorityStudentFinePolicy.java` dòng 1-26 | Chính sách phạt sinh viên ưu tiên |
| `src/main/java/com/library/librarymanagement/controller/ReturnPageController.java` dòng 1-51 | Trang và thao tác trả sách |
| `src/main/resources/templates/return.html` dòng 1-43 | Giao diện trả sách |

**Lưu ý:** thao tác trả trên trang danh sách phiếu được điều phối trong `BorrowTicketPageController.java`, thuộc phạm vi Thành viên 4; Thành viên 5 cung cấp/xác nhận logic trả trong `BorrowService.java`. Hai người trao đổi qua pull request, không cùng sửa một vùng.

## Thành viên 6 — Giao diện còn lại, tích hợp và kiểm thử tổng thể

**Phần việc:** điểm khởi chạy ứng dụng, các màn hình ngoài luồng mượn/trả, CSS, kiểm thử hồi quy và slide báo cáo.

| File/phạm vi | Nội dung |
|---|---|
| `src/main/java/com/library/librarymanagement/LibraryManagementApplication.java` dòng 1-13 | Điểm khởi chạy ứng dụng |
| `src/main/java/com/library/librarymanagement/controller/AdminController.java` dòng 1-227 | Đăng nhập quản trị, thống kê và quản lý sách |
| `src/main/java/com/library/librarymanagement/controller/BookController.java` dòng 1-70 | Danh mục sách và endpoint tải CSV |
| `src/main/java/com/library/librarymanagement/controller/BookForm.java` dòng 1-85 | Form quản lý sách |
| `src/main/java/com/library/librarymanagement/controller/PageController.java` dòng 1-75 | Trang chủ, hồ sơ và nội quy |
| `src/main/java/com/library/librarymanagement/controller/ReaderController.java` dòng 1-29 | Trang bạn đọc |
| `src/main/java/com/library/librarymanagement/controller/SupportController.java` dòng 1-143 | Liên hệ và xử lý phản hồi |
| `src/main/resources/templates/admin-books.html` dòng 1-100 | Trang quản lý sách |
| `src/main/resources/templates/admin-login.html` dòng 1-46 | Trang đăng nhập quản trị |
| `src/main/resources/templates/admin-statistics.html` dòng 1-75 | Trang thống kê |
| `src/main/resources/templates/admin-support.html` dòng 1-69 | Trang xử lý phản hồi |
| `src/main/resources/templates/books.html` dòng 1-108 | Danh mục sách |
| `src/main/resources/templates/contact.html` dòng 1-85 | Trang liên hệ |
| `src/main/resources/templates/fragments.html` dòng 1-130 | Thành phần giao diện dùng chung |
| `src/main/resources/templates/index.html` dòng 1-294 | Trang chủ |
| `src/main/resources/templates/profile.html` dòng 1-74 | Trang hồ sơ |
| `src/main/resources/templates/readers.html` dòng 1-87 | Trang bạn đọc |
| `src/main/resources/templates/rules.html` dòng 1-52 | Trang nội quy |
| `src/main/resources/static/css/library.css` dòng 1-1858 | CSS giao diện |
| `src/main/resources/static/images/` | Tài nguyên ảnh; file ảnh nhị phân không có số dòng |
| `src/test/java/com/library/librarymanagement/BorrowServiceTest.java` dòng 1-260 | Kiểm thử nghiệp vụ mượn/trả và chính sách phạt |
| `src/test/java/com/library/librarymanagement/BorrowTicketPageControllerTest.java` dòng 1-60 | Kiểm thử trang phiếu |
| `docs/test-scenarios.md` dòng 1-18 | Kịch bản kiểm thử và trạng thái xác minh |
| `docs/presentation.html` dòng 1-149 | Slide báo cáo |

## Quy ước commit và tích hợp

1. Thành viên chỉ commit thay đổi do mình thực hiện; commit message nêu rõ nội dung, ví dụ `feat: add borrow validation`.
2. Với `BorrowService.java`, Thành viên 4 và 5 làm trên nhánh riêng, giới hạn thay đổi trong vùng được giao và mở PR lần lượt để tránh xung đột.
3. Thành viên 6 chạy bộ test và kiểm tra giao diện sau khi các PR nghiệp vụ được merge; Thành viên 1 review và merge.
4. Không đưa `target/`, `.idea/`, cấu hình workspace cá nhân hoặc file build lên GitHub.
5. Khi báo cáo, ghi đúng người thực hiện và PR/commit thực tế; không ghi nhận công việc chưa được thành viên đó làm.
