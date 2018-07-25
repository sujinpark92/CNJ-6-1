package com.example.demo

import com.example.demo.repository.CustomerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

import java.util.*

@SpringBootApplication
class DemoApplication {

    companion object {
        /**
         * 이 부분은 Deprecated 되었는데 어떻게 하는지 잘..모르겠다..
         */
//        @ControllerAdvice
//        class JsonAdvice() : AbstractJsonpResponseBodyAdvice {
//
//        }
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DemoApplication::class.java, *args)
        }
    }

    @Bean
    fun init(r: CustomerRepository): CommandLineRunner {
        return CommandLineRunner { args ->
            Arrays
                    .stream(
                            ("Mark,Fisher;Scott,Frederick;Brian,Dussault;" + "Josh,Long;Kenny,Bastani;Dave,Syer;Spencer,Gibb").split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                    .map { n -> n.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }.map { tpl -> r.save(Customer(tpl[0], tpl[1])) }
                    .forEach { println(it) }
        }
    }
}
