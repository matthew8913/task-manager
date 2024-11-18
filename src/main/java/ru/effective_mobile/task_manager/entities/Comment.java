package ru.effective_mobile.task_manager.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(nullable = false)
  private LocalDateTime createdAt;
}
