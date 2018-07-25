package com.example.demo

import org.springframework.data.jpa.domain.AbstractPersistable_.id
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import java.util.Objects

@Entity
class Customer {

    @Id
    @GeneratedValue
    var id: Long = 0L

    var firstName: String? = null
    var lastName: String? = null

    constructor(id: Long, f: String, l: String) {
        this.id = id
        this.firstName = f
        this.lastName = l
    }

    constructor() {}

    constructor(firstName: String, lastName: String) {
        this.firstName = firstName
        this.lastName = lastName
    }

    override fun equals(o: Any?): Boolean {
        if (this === o)
            return true
        if (o !is Customer)
            return false
        val customer = o as Customer?
        return (id == customer!!.id
                && firstName == customer.firstName
                && lastName == customer.lastName)
    }

    override fun hashCode(): Int {
        return Objects.hash(id, firstName, lastName)
    }

    override fun toString(): String {
        return ("Customer{" + "id=" + id + ", firstName='" + firstName + '\''.toString()
                + ", lastName='" + lastName + '\''.toString() + '}'.toString())
    }
}
