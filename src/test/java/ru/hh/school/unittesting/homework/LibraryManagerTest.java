package ru.hh.school.unittesting.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LibraryManagerTest {
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @InjectMocks
    private LibraryManager libraryManager;

    @BeforeEach
    void setUp() {
        libraryManager.addBook("First", 3);
    }

    @Test
    void testBorrowBookUserIsNotActive() {

    }

    @Test
    void testBorrowBookIfBookNotFound() {

    }

    @Test
    void testBorrowBookIfAllSuccess() {

    }

    @Test
    void testReturnBookIfBookNotBorrowedOrUserNotMatch() {

    }

    @Test
    void testReturnBookIfAllSuccess() {

    }

    @Test
    void testGetAvailableCopies() {

    }

    @Test
    void testCalculateDynamicLateFeeIfNegativeOverdueDays() {

    }

    @ParameterizedTest
    @CsvSource({
            "0, true, false"
    })
    void testCalculateDynamicLateFeeIfAllSuccess(
            int overdueDays,
            boolean isBestseller,
            boolean isPremiumMember) {

    }
}
