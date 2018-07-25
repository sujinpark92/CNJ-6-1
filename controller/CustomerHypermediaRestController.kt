package com.example.demo.controller

import com.example.demo.Customer
import com.example.demo.CustomerNotFoundException
import com.example.demo.CustomerResourceAssembler
import com.example.demo.repository.CustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

import java.net.URI
import java.util.Collections
import java.util.stream.Collector
import java.util.stream.Collectors


// <1>
@RestController
@RequestMapping(value = ["/v2"], produces = ["application/hal+json"])
class CustomerHypermediaRestController @Autowired
constructor(private val customerResourceAssembler: CustomerResourceAssembler, // <2>
            private val customerRepository: CustomerRepository) {

    // <4>
    val collection: ResponseEntity<Resources<Resource<Customer>>>

    @GetMapping("/customers")
    get() {
        val collect = this.customerRepository.findAll().stream()
                .map<Resource<Customer>> { customerResourceAssembler.toResource(it) }
                .collect(Collectors.toList())
        val resources = Resources(collect)
        val self = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()
        resources.add(Link(self.toString(), "self"))
        return ResponseEntity.ok(resources)
    }

    // <3>
    @GetMapping
    fun root(): ResponseEntity<Resources<Any>> {
        val objects = Resources(emptyList<Any>())
        val uri = MvcUriComponentsBuilder
                .fromMethodCall(MvcUriComponentsBuilder.on(javaClass).collection)
                .build().toUri()
        val link = Link(uri.toString(), "customers")
        objects.add(link)
        return ResponseEntity.ok(objects)
    }

    @RequestMapping(value = ["/customers"], method = [(RequestMethod.OPTIONS)])
    fun options(): ResponseEntity<*> {
        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS,
                        HttpMethod.PUT, HttpMethod.DELETE).build<Any>()
    }

    @GetMapping(value = ["/customers/{id}"])
    operator fun get(@PathVariable id: Long): ResponseEntity<Resource<Customer>> {
        return this.customerRepository.findById(id)
                .map { c -> ResponseEntity.ok(this.customerResourceAssembler.toResource(c)) }
                .orElseThrow { CustomerNotFoundException(id) }
    }

    @PostMapping(value = ["/customers"])
    fun post(@RequestBody c: Customer): ResponseEntity<Resource<Customer>> {
        val customer = this.customerRepository.save(Customer(c
                .firstName!!, c.lastName!!))
        val uri = MvcUriComponentsBuilder.fromController(javaClass)
                .path("/customers/{id}").buildAndExpand(customer.id).toUri()
        return ResponseEntity.created(uri).body(
                this.customerResourceAssembler.toResource(customer))
    }

    @DeleteMapping(value = ["/customers/{id}"])
    fun delete(@PathVariable id: Long): ResponseEntity<*> {
        return this.customerRepository.findById(id).map { c ->
            customerRepository.delete(c)
            ResponseEntity.noContent().build<Any>()
        }.orElseThrow { CustomerNotFoundException(id) }
    }

    @RequestMapping(value = ["/customers/{id}"], method = [(RequestMethod.HEAD)])
    fun head(@PathVariable id: Long): ResponseEntity<*> {
        return this.customerRepository.findById(id)
                .map { exists -> ResponseEntity.noContent().build<Any>() }
                .orElseThrow { CustomerNotFoundException(id) }
    }

    @PutMapping("/customers/{id}")
    fun put(@PathVariable id: Long,
            @RequestBody c: Customer): ResponseEntity<Resource<Customer>> {
        val customer = this.customerRepository.save(Customer(id, c
                .firstName!!, c.lastName!!))
        val customerResource = this.customerResourceAssembler
                .toResource(customer)
        val selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest()
                .toUriString())
        return ResponseEntity.created(selfLink).body(customerResource)
    }
}
