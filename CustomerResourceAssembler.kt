package com.example.demo

import com.example.demo.controller.CustomerHypermediaRestController
import com.example.demo.controller.CustomerProfilePhotoRestController
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resource
import org.springframework.hateoas.ResourceAssembler
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

import java.net.URI

@Component
class CustomerResourceAssembler : ResourceAssembler<Customer, Resource<Customer>> {

    override fun toResource(customer: Customer): Resource<Customer> {

        val customerResource = Resource(customer)//<1>
        val photoUri = MvcUriComponentsBuilder
                .fromMethodCall(
                        MvcUriComponentsBuilder.on(CustomerProfilePhotoRestController::class.java).read(
                                customer.id)).buildAndExpand().toUri()

        val selfUri = MvcUriComponentsBuilder
                .fromMethodCall(
                        MvcUriComponentsBuilder.on(CustomerHypermediaRestController::class.java)[customer.id]).buildAndExpand().toUri()

        customerResource.add(Link(selfUri.toString(), "self"))
        customerResource.add(Link(photoUri.toString(), "profile-photo"))
        return customerResource

    }
}
