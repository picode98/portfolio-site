package net.saamankhali.portfoliosite

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PortfolioSiteApplication

fun main(args: Array<String>) {
	runApplication<PortfolioSiteApplication>(*args)
}
