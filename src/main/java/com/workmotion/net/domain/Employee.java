package com.workmotion.net.domain;


import javax.persistence.*;

import org.springframework.boot.context.properties.bind.DefaultValue;

import java.io.Serializable;
import java.time.LocalDate;

import com.workmotion.net.domain.enumeration.HiringStatus;

import net.bytebuddy.implementation.bind.annotation.Default;

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "status")
    private String status;

    @Column(name = "phone_no")
    private String phoneNO;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Employee name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Employee dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public HiringStatus getStatus() {
        return HiringStatus.valueOf(this.status);
    }


    public void setStatus(HiringStatus status) {
        this.status = status.name();
    }

    public String getPhoneNO() {
        return phoneNO;
    }

    public Employee phoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
        return this;
    }

    public void setPhoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return id != null && id.equals(((Employee) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", status='" + getStatus() + "'" +
            ", phoneNO='" + getPhoneNO() + "'" +
            "}";
    }

	public Employee status(HiringStatus updatedStatus) {
		this.status = updatedStatus.name();
		return this;
	}
}
