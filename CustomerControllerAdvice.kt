package com.example.demo

import org.springframework.hateoas.VndErrors
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

import java.util.Optional

@ControllerAdvice(annotations = arrayOf(RestController::class))
class CustomerControllerAdvice {

    // <1>
    private val vndErrorMediaType = MediaType
            .parseMediaType("application/vnd.error")

    // <2>
    @ExceptionHandler(CustomerNotFoundException::class)
    internal fun notFoundException(e: CustomerNotFoundException): ResponseEntity<VndErrors> {
        return this.error(e, HttpStatus.NOT_FOUND, e.customerId!!.toString() + "")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    internal fun assertionException(ex: IllegalArgumentException): ResponseEntity<VndErrors> {
        return this.error(ex, HttpStatus.NOT_FOUND, ex.localizedMessage)
    }

    // <3>
    private fun <E : Exception> error(error: E,
                                      httpStatus: HttpStatus, logref: String): ResponseEntity<VndErrors> {
        val msg = Optional.of<String>(error.message!!).orElse(
                error.javaClass.simpleName)
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = this.vndErrorMediaType
        return ResponseEntity(VndErrors(logref, msg), httpHeaders,
                httpStatus)
    }
}