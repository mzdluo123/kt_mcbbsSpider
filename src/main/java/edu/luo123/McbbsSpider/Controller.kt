package edu.luo123.McbbsSpider

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.HashSet


class Controller(val startUrls: List<String>, val db: DBBase) {
    val urls = HashSet<String>()
    val spider = Spider()

    val logger = org.apache.log4j.Logger.getLogger(Controller::class.java)
    suspend fun start() {
        for (i in startUrls) {
            processPage(i)
        }
    }

    suspend fun processPage(url: String) {
        coroutineScope {
            val doc = spider.downloadPage(url) ?: return@coroutineScope
            if (isMcbbsPage(doc.location())) {
                val urls = spider.getAllLink(doc)
                    .filter { isMcbbsPage(it) }
                    .filter { pageDeduplication(it) }
                    .filter { "plugin.php?id=link_redirect" !in it }

                logger.info("添加了 ${urls.size} 个链接")
                urls.forEach {
                    launch {  processPage(it)}
                }
            }
            if (isPostPage(url)) {
                val post = spider.getPostInfo(doc)
                if (post == null) {
                    logger.warn("抓取信息失败,可能是因为该页面不是帖子页面")
                    return@coroutineScope
                }
                db.addPost(post)
            }
        }


    }

    fun isPostPage(str: String): Boolean {
        return "thread-" in str
    }

    fun isMcbbsPage(str: String): Boolean {
        return "www.mcbbs.net" in str
    }

    fun pageDeduplication(url: String): Boolean {
        return urls.add(url)
    }
}