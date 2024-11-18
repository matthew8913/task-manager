package ru.effective_mobile.task_manager.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Task> authoredTasks;

  @OneToMany(mappedBy = "assignee", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Task> assignedTasks;

  @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Comment> comments;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(name = "refresh_token")
  private String refreshToken;

  public enum Role {
    ADMIN,
    USER
  }
}
