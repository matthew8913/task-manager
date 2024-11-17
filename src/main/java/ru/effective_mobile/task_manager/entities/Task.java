package ru.effective_mobile.task_manager.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priority;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne
  @JoinColumn(name = "assignee_id")
  private User assignee;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Comment> comments;

  public enum Status {
    PENDING,
    IN_PROGRESS,
    COMPLETED
  }

  public enum Priority {
    HIGH,
    MEDIUM,
    LOW
  }
}
