package com.amimul.book.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;


    //Average of all feedback
    private double rate;
    private boolean returned;
    private boolean returnApproved;
}
