package com.amimul.book.feedback;

import com.amimul.book.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedBackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                //Why we are building a book instead of connecting the existing one.
                .book(Book.builder()
                        .id(request.bookId())
                        .archived(false)
                        .shareable(false)
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer userId) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), userId))
                .build();
    }
}
