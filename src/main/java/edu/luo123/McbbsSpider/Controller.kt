package edu.luo123.McbbsSpider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.HashSet


class Controller(val startUrls: List<String>, val db: DBBase) {
    val urls = HashSet<String>()
    val spider = Spider()

    val logger = org.apache.log4j.Logger.getLogger(Controller::class.java)
    suspend fun start() {
        for (i in startUrls) {
            withContext(Dispatchers.Default) { processPage(i) }
        }
    }

    suspend fun processPage(url: String) {
        val doc = spider.downloadPage(url) ?: return
        if (isMcbbsPage(doc.location())) {
            val urls = spider.getAllLink(doc)
                .filter { isMcbbsPage(it) }
                .filter { PageDeduplication(it) }
                .filter { "plugin.php?id=link_redirect" !in it }

            logger.info("添加了 ${urls.size} 个链接")
            urls.forEach {
                withContext(Dispatchers.Default) { processPage(it) }
            }
        }
        if (isPostPage(url)) {
            val post = spider.getPostInfo(doc)
            if (post == null) {
                logger.warn("抓取信息失败,可能是因为该页面不是帖子页面")
                return
            }
            db.addPost(post)
        }

    }

    fun isPostPage(str: String): Boolean {
        return "thread-" in str
    }

    fun isMcbbsPage(str: String): Boolean {
        return "www.mcbbs.net" in str
    }

    fun PageDeduplication(url: String): Boolean {
        return urls.add(url)
    }
}