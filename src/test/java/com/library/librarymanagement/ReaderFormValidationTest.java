package com.library.librarymanagement;

import com.library.librarymanagement.controller.ReaderForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Kiểm thử dữ liệu nhập sai của form thêm / sửa độc giả. */
class ReaderFormValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    private ReaderForm validForm() {
        ReaderForm form = new ReaderForm();
        form.setFullName("Nguyen Van An");
        form.setPhone("0901234567");
        form.setReaderType("NORMAL");
        return form;
    }

    private Set<String> invalidFields(ReaderForm form) {
        return validator.validate(form).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void validFormHasNoViolations() {
        assertTrue(validator.validate(validForm()).isEmpty());
    }

    @Test
    void emptyOptionalFieldsAreAccepted() {
        ReaderForm form = validForm();
        form.setEmail("");
        form.setAddress(null);
        form.setStudentCode(null);

        // email rỗng hợp lệ với @Email
        assertTrue(validator.validate(form).isEmpty());
    }

    @Test
    void blankFullNameIsRejected() {
        ReaderForm form = validForm();
        form.setFullName("   ");

        assertEquals(Set.of("fullName"), invalidFields(form));
    }

    @Test
    void nullFullNameIsRejected() {
        ReaderForm form = validForm();
        form.setFullName(null);

        assertEquals(Set.of("fullName"), invalidFields(form));
    }

    @Test
    void fullNameLongerThan100CharactersIsRejected() {
        ReaderForm form = validForm();
        form.setFullName("A".repeat(101));

        assertEquals(Set.of("fullName"), invalidFields(form));
    }

    @Test
    void blankPhoneIsRejected() {
        ReaderForm form = validForm();
        form.setPhone("");

        assertTrue(invalidFields(form).contains("phone"));
    }

    @Test
    void phoneWithLettersIsRejected() {
        ReaderForm form = validForm();
        form.setPhone("09012abc67");

        assertEquals(Set.of("phone"), invalidFields(form));
    }

    @Test
    void phoneNotStartingWithZeroIsRejected() {
        ReaderForm form = validForm();
        form.setPhone("1901234567");

        assertEquals(Set.of("phone"), invalidFields(form));
    }

    @Test
    void phoneTooShortOrTooLongIsRejected() {
        ReaderForm shortPhone = validForm();
        shortPhone.setPhone("090123");
        ReaderForm longPhone = validForm();
        longPhone.setPhone("090123456789");

        assertEquals(Set.of("phone"), invalidFields(shortPhone));
        assertEquals(Set.of("phone"), invalidFields(longPhone));
    }

    @Test
    void elevenDigitPhoneIsAccepted() {
        ReaderForm form = validForm();
        form.setPhone("09012345678");

        assertTrue(validator.validate(form).isEmpty());
    }

    @Test
    void malformedEmailIsRejected() {
        ReaderForm form = validForm();
        form.setEmail("khong-phai-email");

        assertEquals(Set.of("email"), invalidFields(form));
    }

    @Test
    void addressLongerThan200CharactersIsRejected() {
        ReaderForm form = validForm();
        form.setAddress("B".repeat(201));

        assertEquals(Set.of("address"), invalidFields(form));
    }

    @Test
    void studentCodeLongerThan50CharactersIsRejected() {
        ReaderForm form = validForm();
        form.setStudentCode("9".repeat(51));

        assertEquals(Set.of("studentCode"), invalidFields(form));
    }

    @Test
    void unknownReaderTypeIsRejected() {
        ReaderForm form = validForm();
        form.setReaderType("VIP");

        assertEquals(Set.of("readerType"), invalidFields(form));
    }

    @Test
    void blankReaderTypeIsRejected() {
        ReaderForm form = validForm();
        form.setReaderType("");

        assertTrue(invalidFields(form).contains("readerType"));
    }

    @Test
    void severalWrongFieldsAreAllReported() {
        ReaderForm form = new ReaderForm();
        form.setFullName("");
        form.setPhone("abc");
        form.setEmail("sai");
        form.setReaderType("X");

        Set<ConstraintViolation<ReaderForm>> violations = validator.validate(form);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(fields.containsAll(Set.of("fullName", "phone", "email", "readerType")));
    }
}
