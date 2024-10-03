package com.amimul.book.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner;
    private byte[] cover;
    //Average of all feedback
    private double rate;
    private boolean archived;
    private boolean shareable;
}
