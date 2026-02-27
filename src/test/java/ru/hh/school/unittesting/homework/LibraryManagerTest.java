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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
        libraryManager.addBook("Second", 2);
    }

    @Test
    void testBorrowBookUserIsNotActive() {
        when(userService.isUserActive(any())).thenReturn(false);
        assertFalse(libraryManager.borrowBook("First", "4"));
    }

    @Test
    void testBorrowBookIfBookNotFound() {
        when(userService.isUserActive(eq("4"))).thenReturn(true);
        assertFalse(libraryManager.borrowBook("Third", "4"));
    }

    @ParameterizedTest
    @CsvSource({
            "First, 4",
            "Second, 3",
            "First, 5"
    })
    void testBorrowBookIfAllSuccess(String bookId, String userId) {
        when(userService.isUserActive(eq(userId))).thenReturn(true);
        assertTrue(libraryManager.borrowBook(bookId, userId));
    }

    @Test
    void testReturnBookIfBookNotBorrowedOrUserNotMatch() {
        libraryManager.borrowBook("Second", "3");
        assertFalse(libraryManager.returnBook("First", "3"));

        libraryManager.borrowBook("First", "2");
        assertFalse(libraryManager.returnBook("First", "3"));
    }

    @Test
    void testReturnBookIfAllSuccess() {
        when(userService.isUserActive(eq("5"))).thenReturn(true);
        libraryManager.borrowBook("Second", "5");
        assertTrue(libraryManager.returnBook("Second", "5"));
    }

    @Test
    void testGetAvailableCopies() {
        assertEquals(3, libraryManager.getAvailableCopies("First"));
    }

    @Test
    void testCalculateDynamicLateFeeIfNegativeOverdueDays() {
        assertThrows(IllegalArgumentException.class, () ->
                libraryManager.calculateDynamicLateFee(-3, true, true));
        assertThrows(IllegalArgumentException.class, () ->
                libraryManager.calculateDynamicLateFee(-1, false, true));
        assertThrows(IllegalArgumentException.class, () ->
                libraryManager.calculateDynamicLateFee(-5, true, false));
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
