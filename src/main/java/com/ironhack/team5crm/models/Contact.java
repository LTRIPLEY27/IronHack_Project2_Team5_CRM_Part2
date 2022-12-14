package com.ironhack.team5crm.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


    // * CONSTRUCTOR
    // **********************************************
    public Contact(String name, String phoneNumber, String email, Account account) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.account = account;
    }

    // * Equals, hashcode and toString
    // **********************************************
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        if (!super.equals(object))
            return false;
        Contact contact = (Contact) object;
        return id == contact.id;
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "👤 Contact with ID " + id + ": \n" +
                "Name: " + name + "\n" +
                "Phone: " + phoneNumber + "\n" +
                "e-mail: " + email + "\n";
    }
}
