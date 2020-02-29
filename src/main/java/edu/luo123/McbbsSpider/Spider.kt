package edu.luo123.McbbsSpider

import javafx.geometry.Pos
import kotlinx.coroutines.*
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
        return withContext(Dispatchers.IO) {
            val job = async {
                try {
                    return@async Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                        .get()
                } catch (e: Exception) {
                    logger.error("错误", e)
                    return@async null
                }
            }
            return@withContext job.await()
        }
    }

    fun getAllLink(document: Document): List<String> {
        val elements = document.select("a[href]")
        return elements.map { it.attr("href") }
    }

    fun getPostInfo(document: Document): Post? {
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
