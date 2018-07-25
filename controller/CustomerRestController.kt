package com.example.demo.controller

import com.example.demo.Customer
import com.example.demo.CustomerNotFoundException
import com.example.demo.repository.CustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

import java.net.URI

@RestController
@RequestMapping("/v1/customers")
class CustomerRestController {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    internal val collection: ResponseEntity<Collection<Customer>>
        @GetMapping
        get() = ResponseEntity.ok(this.customerRepository.findAll())

    // <1>
    @RequestMapping(method = [(RequestMethod.OPTIONS)])
    internal fun options(): ResponseEntity<*> {

        //@formatter:off
        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST,
                        HttpMethod.HEAD, HttpMethod.OPTIONS,
                        HttpMethod.PUT, HttpMethod.DELETE)
                .build<Any>()
        //@formatter:on
    }

    // <2>
    @GetMapping(value = "/{id}")
    internal operator fun get(@PathVariable id: Long?): ResponseEntity<Customer> {
        return this.customerRepository.findById(id!!).map { ResponseEntity.ok() }
                .orElseThrow { CustomerNotFoundException(id) }.build()
    }

    @PostMapping
    internal fun post(@RequestBody c: Customer): ResponseEntity<Customer> { // <3>

        val customer = this.customerRepository.save(Customer(c
                .firstName!!, c.lastName!!))

        val uri = MvcUriComponentsBuilder.fromController(javaClass).path("/{id}")
                .buildAndExpand(customer.id!!).toUri()
        return ResponseEntity.created(uri).body(customer)
    }

    // <4>
    @DeleteMapping(value = "/{id}")
    internal fun delete(@PathVariable id: Long): ResponseEntity<*> {
        return this.customerRepository.findById(id).map { c ->
            customerRepository.delete(c)
            ResponseEntity.noContent().build<Any>()
        }.orElseThrow { CustomerNotFoundException(id) }
    }

    // <5>
    @RequestMapping(value = "/{id}", method = [(RequestMethod.HEAD)])
    internal fun head(@PathVariable id: Long): ResponseEntity<*> {
        return this.customerRepository.findById(id)
                .map { exists -> ResponseEntity.noContent().build<Any>() }
                .orElseThrow { CustomerNotFoundException(id) }
    }

    // <6>
    @PutMapping(value = "/{id}")
    internal fun put(@PathVariable id: Long, @RequestBody c: Customer): ResponseEntity<Customer> {
        return this.customerRepository
                .findById(id)
                .map { existing ->
                    val customer = this.customerRepository.save(Customer(existing
                            .id, c.firstName!!, c.lastName!!))
                    val selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest()
                            .toUriString())
                    ResponseEntity.created(selfLink).body(customer)
                }.orElseThrow { CustomerNotFoundException(id) }

    }
}
