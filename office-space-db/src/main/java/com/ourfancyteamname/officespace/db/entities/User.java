package com.ourfancyteamname.officespace.db.entities;

import com.ourfancyteamname.officespace.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@Entity
@Table(name = "`user`")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

  private static final long serialVersionUID = 2211441170433296406L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "`id`")
  private int id;

  @Column(name = "`username`")
  private String username;

  @Column(name = "`email`")
  private String email;

  @Column(name = "`password`")
  private String password;

  @Column(name = "`first-name`")
  private String firstName;

  @Column(name = "`last-name`")
  private String lastName;

  @Column(name = "`phone`")
  private String phone;

  @Column(name = "`alternate-phone`")
  private String alternatePhone;

  @Column(name = "`address`")
  private String address;

  @Column(name = "`gender`")
  private Gender gender;
}
