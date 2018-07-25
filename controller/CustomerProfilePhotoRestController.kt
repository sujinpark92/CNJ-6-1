package com.example.demo.controller

import com.example.demo.Customer
import com.example.demo.CustomerNotFoundException
import com.example.demo.repository.CustomerRepository
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.Assert
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import java.io.*
import java.net.URI
import java.util.concurrent.Callable

import org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest

//@formatter:off
//@formatter:on

@RestController
@RequestMapping(value = "/customers/{id}/photo")
class CustomerProfilePhotoRestController @Autowired
internal constructor(
        @Value("\${upload.dir:\${user.home}/images}") uploadDir: String,
        private val customerRepository: CustomerRepository) {

    private val root: File = File(uploadDir)
    private val log = LogFactory.getLog(javaClass)

    init {
        Assert.isTrue(this.root.exists() || this.root.mkdirs(),
                String.format("The path '%s' must exist.", this.root.absolutePath))
    }

    // <1>
    @GetMapping
    internal fun read(@PathVariable id: Long?): ResponseEntity<Resource> {
        return this.customerRepository
                .findById(id!!)
                .map { customer ->
                    val file = fileFor(customer)

                    Assert.isTrue(file.exists(),
                            String.format("file-not-found %s", file.absolutePath))

                    val fileSystemResource = FileSystemResource(file)
                    ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                            .body<Resource>(fileSystemResource)
                }.orElseThrow { CustomerNotFoundException(id) }
    }

    // <2>
    @RequestMapping(method = [(RequestMethod.POST), (RequestMethod.PUT)])
    // <3>
    @Throws(Exception::class)
    internal fun write(@PathVariable id: Long?,
                       @RequestParam file: MultipartFile): Callable<ResponseEntity<*>> {
        log.info(String.format("upload-start /customers/%s/photo (%s bytes)", id,
                file.size))
        return Callable {
            this.customerRepository
                    .findById(id!!)
                    .map { customer ->
                        val fileForCustomer = fileFor(customer)
                        try {
                            file.inputStream.use { `in` -> FileOutputStream(fileForCustomer).use { out -> FileCopyUtils.copy(`in`, out) } }
                        } catch (ex: IOException) {
                            throw RuntimeException(ex)
                        }

                        val location = fromCurrentRequest().buildAndExpand(id!!).toUri() // <4>
                        log.info(String.format("upload-finish /customers/%s/photo (%s)", id, location))
                        ResponseEntity.created(location)
                    }.orElseThrow { CustomerNotFoundException(id!!) }.build<Any>()
        }
    }

    private fun fileFor(person: Customer): File {
        return File(this.root, java.lang.Long.toString(person.id!!))
    }
}
