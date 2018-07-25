package com.example.demo.repository

import com.example.demo.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import java.util.Optional

interface CustomerRepository : JpaRepository<Customer, Long> {

    fun findByFirstNameIgnoreCase(@Param("fn") firstName: String): Collection<Customer>

    override fun findById(@Param("id") id: Long): Optional<Customer>

    fun findByLastNameIgnoreCase(@Param("ln") ln: String): Collection<Customer>
}
