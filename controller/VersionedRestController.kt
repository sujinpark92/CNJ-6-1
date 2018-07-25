package com.example.demo.controller

import org.springframework.web.bind.annotation.*

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RestController
@RequestMapping("/api")
class VersionedRestController {

    companion object {
        const val V1_MEDIA_TYPE_VALUE = "application/vnd.bootiful.demo-v1+json"

        const val V2_MEDIA_TYPE_VALUE = "application/vnd.bootiful.demo-v2+json"
    }

    enum class ApiVersion {
        v1, v2
    }

    class Greeting(val how: String, version: ApiVersion) {
        val version : String = version.toString()
    }

    // <1>
    @GetMapping(value = ["/{version}/hi"], produces = [APPLICATION_JSON_VALUE])
    internal fun greetWithPathVariable(@PathVariable version: ApiVersion): Greeting {
        return greet(version, "path-variable")
    }

    // <2>
    @GetMapping(value = ["/hi"], produces = [APPLICATION_JSON_VALUE])
    internal fun greetWithHeader(@RequestHeader("X-API-Version") version: ApiVersion): Greeting {
        return this.greet(version, "header")
    }

    // <3>
    @GetMapping(value = ["/hi"], produces = [V1_MEDIA_TYPE_VALUE])
    internal fun greetWithContentNegotiationV1(): Greeting {
        return this.greet(ApiVersion.v1, "content-negotiation")
    }

    // <4>
    @GetMapping(value = ["/hi"], produces = [V2_MEDIA_TYPE_VALUE])
    internal fun greetWithContentNegotiationV2(): Greeting {
        return this.greet(ApiVersion.v2, "content-negotiation")
    }

    private fun greet(version: ApiVersion, how: String): Greeting {
        return Greeting(how, version)
    }

}
