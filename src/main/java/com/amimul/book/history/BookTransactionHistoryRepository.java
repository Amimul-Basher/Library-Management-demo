package com.amimul.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowed(@Param("bookId") Integer bookId);

    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.bookUser.id = :userId
            AND bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser(@Param("bookId") Integer bookId);

    @Query("""
        SELECT history
        FROM BookTransactionHistory history
        WHERE history.bookUser.id = :userId
    """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT history
        FROM BookTransactionHistory history
        WHERE history.book.owner.id = :userId
    """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT bookTransactionHistory
        FROM BookTransactionHistory bookTransactionHistory
        WHERE bookTransactionHistory.bookUser.id = :userId
        AND bookTransactionHistory.book.id = :bookId
        AND bookTransactionHistory.returned = false
    """)
    Optional<BookTransactionHistory> findBookByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
        SELECT bookTransactionHistory
        FROM BookTransactionHistory bookTransactionHistory
        WHERE bookTransactionHistory.book.owner.id = :ownerId
        AND bookTransactionHistory.book.id = :bookId
        AND bookTransactionHistory.returned = true
    """)
    Optional<BookTransactionHistory> findBookByBookIdAndOwnerId(Integer bookId, Integer ownerId);
}