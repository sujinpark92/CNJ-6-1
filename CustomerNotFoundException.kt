package com.example.demo

class CustomerNotFoundException(val customerId: Long?) : RuntimeException("customer-not-found-" + customerId!!)
