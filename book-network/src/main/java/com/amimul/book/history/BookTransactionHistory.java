package com.amimul.book.history;

import com.amimul.book.book.Book;
import com.amimul.book.common.BaseEntity;
import com.amimul.book.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class BookTransactionHistory extends BaseEntity {

    private boolean returned;
    private boolean returnApproved;

    // --> todo Change required
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User bookUser;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
