package com.github.jaybcee.boatsbegone.scraper

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import org.jsoup.nodes.Element

/**
 * A data class representing the two bridges over the locks.
 */
data class Lock(
    var downstream: DocElement = DocElement(Element("tr")),
    var upstream: DocElement = DocElement(Element("tr"))
)

/**
 * A function to scrape the data from website and map it into a Lock object.
 */
fun scrape(): Lock {
    return skrape(HttpFetcher) {
        request {
            url = "https://www.glslw-glvm.com/R2/jsp/MaiBrdgStatus_235.jsp?language=E"
        }
        extractIt<Lock> {
            htmlDocument {
                it.downstream = findAll("#status")[0]
                it.upstream = findAll("#status")[1]
            }
        }
    }
}

