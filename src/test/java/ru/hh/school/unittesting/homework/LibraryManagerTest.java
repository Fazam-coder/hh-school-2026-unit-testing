package ru.hh.school.unittesting.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryManagerTest {
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @InjectMocks
    private LibraryManager libraryManager;

    @BeforeEach
    void setUp() {
        libraryManager.addBook("First", 3);
        libraryManager.addBook("Second", 2);
    }

    @Test
    void testBorrowBookUserIsNotActive() {
        when(userService.isUserActive(any())).thenReturn(false);
        assertFalse(libraryManager.borrowBook("First", "4"));
        verify(notificationService).notifyUser("4", "Your account is not active.");
    }

    @Test
    void testBorrowBookIfBookNotFound() {
        when(userService.isUserActive("4")).thenReturn(true);
        assertFalse(libraryManager.borrowBook("Third", "4"));
    }

    @Test
    void testBorrowBookIfAllSuccess() {
        when(userService.isUserActive("4")).thenReturn(true);
        assertTrue(libraryManager.borrowBook("First", "4"));
        assertEquals(2, libraryManager.getAvailableCopies("First"));
        verify(notificationService).notifyUser("4", "You have borrowed the book: First");
    }

    @Test
    void testReturnBookIfBookNotBorrowed() {
        libraryManager.borrowBook("Second", "3");
        assertFalse(libraryManager.returnBook("First", "3"));
    }

    @Test
    void testReturnBookIfUserNotMatch() {
        when(userService.isUserActive(any())).thenReturn(true);
        libraryManager.borrowBook("First", "2");
        assertFalse(libraryManager.returnBook("First", "3"));
    }

    @Test
    void testReturnBookIfAllSuccess() {
        when(userService.isUserActive("5")).thenReturn(true);
        libraryManager.borrowBook("Second", "5");
        assertTrue(libraryManager.returnBook("Second", "5"));
        assertEquals(2, libraryManager.getAvailableCopies("Second"));
        verify(notificationService).notifyUser("5", "You have returned the book: Second");
    }

    @Test
    void testGetAvailableCopies() {
        assertEquals(3, libraryManager.getAvailableCopies("First"));
    }

    @ParameterizedTest
    @CsvSource({
            "-3, true, true",
            "-1, false, true",
            "-5, true, false",
            "-2, false, false"
    })
    void testCalculateDynamicLateFeeIfNegativeOverdueDays(int overdueDays, boolean isBestseller, boolean isPremiumMember) {
        assertThrows(IllegalArgumentException.class, () ->
                libraryManager.calculateDynamicLateFee(overdueDays, isBestseller, isPremiumMember));
    }

    @ParameterizedTest
    @CsvSource({
            "0, true, true, 0",
            "3, false, false, 1.5",
            "4, false, true, 1.6",
            "2, true, false, 1.5",
            "10, true, true, 6"
    })
    void testCalculateDynamicLateFeeIfAllSuccess(
            int overdueDays,
            boolean isBestseller,
            boolean isPremiumMember,
            double expectedFee) {
        assertEquals(expectedFee,
                libraryManager.calculateDynamicLateFee(overdueDays, isBestseller, isPremiumMember));
    }
}
