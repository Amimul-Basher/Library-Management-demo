package com.amimul.book.role;

import com.amimul.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Table(name="role")
//Adding jpa Auditing to our application. ???
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue
    private Integer Id;
    @Column(unique=true)
    private String name;

    @ManyToMany(mappedBy = "roles")
//    To stop the infinite loop. Just to Ignore serialization for Users attribute.
    //It won't prevent the users being fetched but it will  prevent the serialization and infinite loop construction
    @JsonIgnore
    private List<User> users;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime modifiedDate;
}
