package com.github.jaybcee.boatsbegone.scraper

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import it.skrape.selects.html5.tr
import org.jsoup.nodes.Element

data class Rows(
    var downstream: DocElement = DocElement(Element("tr")),
    var upstream: DocElement = DocElement(Element("tr"))
)

fun scrape(): Rows {
    return skrape(HttpFetcher) {
        request {
            url = "https://www.glslw-glvm.com/R2/jsp/MaiBrdgStatus_235.jsp?language=E"
        }
        extractIt<Rows> {
            htmlDocument {
                it.downstream = tr { findByIndex(1) }
                it.upstream = tr { findByIndex(2) }
            }
        }
    }
}

