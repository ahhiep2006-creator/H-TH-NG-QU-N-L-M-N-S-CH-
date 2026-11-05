# Tình huống kiểm thử

Các tình huống dưới đây dùng dữ liệu mẫu trong ứng dụng. Các test tự động hiện có được ghi rõ; tình huống chưa có test tự động cần được kiểm tra thủ công hoặc bổ sung test trước khi khẳng định đã được xác minh.

| # | Tình huống | Kết quả mong đợi | Kiểm tra hiện có |
|---|---|---|---|
| 1 | Bạn đọc hợp lệ mượn sách còn đủ số lượng | Tạo phiếu, giảm tồn kho và đặt trạng thái đang mượn | Test tự động |
| 2 | Mượn sách đã hết hoặc tồn kho không đủ | Từ chối mượn, không tạo phiếu và không giảm tồn kho | Test tự động |
| 3 | Mượn số lượng bằng 0 hoặc âm | Từ chối trước khi truy cập dữ liệu | Test tự động |
| 4 | Mượn vượt hạn mức theo loại bạn đọc | Từ chối mượn và giữ nguyên tồn kho | Test tự động |
| 5 | Trả sách quá hạn | Lưu ngày trả, cộng lại tồn kho, chuyển phiếu sang đã trả và tính phạt theo loại bạn đọc | Test tự động cho sinh viên thường; có kiểm tra mức phạt của cả ba loại |
| 6 | Trả phiếu đã trả lần nữa | Từ chối và không tạo thêm bản ghi trả | Test tự động |
| 7 | Hoàn tác một phiếu đã trả | Từ chối chuyển trạng thái và không thay đổi phiếu | Test tự động |
| 8 | Hoàn tác phiếu đang mượn rồi xác nhận nhận sách | Phiếu chuyển về chờ lấy rồi trở lại đang mượn; không thay đổi số lượng tồn kho | Có test trạng thái; nên kiểm tra thêm tồn kho qua luồng tích hợp |
| 9 | Trả sách đúng hạn | Tiền phạt bằng 0, tồn kho tăng đúng một lần và phiếu chuyển sang đã trả | Chưa có test riêng |
| 10 | Mã bạn đọc, mã sách hoặc mã phiếu không tồn tại | Từ chối và hiển thị thông báo phù hợp | Có xử lý trong service; chưa có test riêng cho cả ba trường hợp |

Các test tự động nằm trong `src/test/java/com/library/librarymanagement/BorrowServiceTest.java` và `BookCsvExportServiceTest.java`.