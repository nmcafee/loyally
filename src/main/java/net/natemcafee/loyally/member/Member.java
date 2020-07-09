package net.natemcafee.loyally.member;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Member {
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String username;

  @Column
  private String firstName;

  @Column
  private String lastName;

  @Column
  private String email;

  protected Member() {
  }

  public Member(String username, String firstName, String lastName, String email) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  public Member(Long id, String username, String firstName, String lastName, String email) {
    this(username, firstName, lastName, email);
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Member member = (Member) o;
    return Objects.equals(id, member.id) &&
        Objects.equals(username, member.username) &&
        Objects.equals(firstName, member.firstName) &&
        Objects.equals(lastName, member.lastName) &&
        Objects.equals(email, member.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, firstName, lastName, email);
  }
}
