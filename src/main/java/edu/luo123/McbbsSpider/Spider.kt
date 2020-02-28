package edu.luo123.McbbsSpider

import javafx.geometry.Pos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.apache.log4j.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception

class Spider {
    companion object {
        val IDPATTERN = "(\\d+\\.?\\d*)".toRegex()
    }

    val logger = Logger.getLogger(Spider::class.java)


    suspend fun downloadPage(url: String): Document? {
        logger.info("正在下载 $url")
        delay(100)
        try {
            return Jsoup.connect(url).get()
        } catch (e: Exception) {
            logger.error("错误 ${e.message}")
        }
        return null
    }

    suspend fun getAllLink(document: Document): List<String> {
        val elements = document.select("a[href]")
        return elements.map { it.attr("href") }
    }

    suspend fun getPostInfo(document: Document): Post? {
        val post = Post()
        try {
            post.id = IDPATTERN.find(document.location())?.value?.toInt() ?: return null
            post.title = document.title()
            post.url = document.location()
            val contents = document.select("#postlist > div")
            post.author = contents[0].select(" .authi > a")[0].text()
            post.authorHome = document.baseUri() + contents[0].select(" .authi > a")[0].attr("href")
        } catch (e: Exception) {
            logger.warn(e.message)
            return null
        }

        return post
    }
}