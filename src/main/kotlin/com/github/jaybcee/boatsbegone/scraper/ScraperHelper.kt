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
data class ScraperHelper(
    var downstream: DocElement = DocElement(Element("span")),
    var upstream: DocElement = DocElement(Element("span"))
)

/**
 * A function to scrape the data from website and map it into a Lock object.
 */
fun scrape(): ScraperHelper {
    return skrape(HttpFetcher) {
        request {
            url = "https://www.glslw-glvm.com/R2/jsp/MaiBrdgStatus_235.jsp?language=E"
        }
        extractIt<ScraperHelper> {
            htmlDocument {
                it.downstream = findAll("#status")[0]
                it.upstream = findAll("#status")[1]
            }
        }
    }
}
