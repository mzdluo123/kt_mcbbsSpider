package edu.luo123.McbbsSpider

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel


class Controller(private val startUrls: List<String>, private val db: DBBase) {
    private val urls = LinkedHashSet<String>()
    private val spider = Spider()
    private val channel = Channel<String>()
    private val logger = org.apache.log4j.Logger.getLogger(Controller::class.java)
    suspend fun start() {
        withContext(Dispatchers.Default) {
            for (i in startUrls) {
                launch { channel.send(i) }
            }
            loop()
        }
    }

    private suspend fun loop() {
        withContext(Dispatchers.Default) {
            logger.debug("启动loop")
            try {
                while (true) {
                    lateinit var url: String
                    withTimeout(10000) {
                        url = channel.receive()
                    }
                    delay(200)
                    launch { processPage(url) }
                }
            }catch (e:TimeoutCancellationException){
                logger.warn("爬取完成，正在退出")
            }

        }

    }

    private suspend fun processPage(url: String) {
        withContext(Dispatchers.Default) {
            val doc = spider.downloadPage(url) ?: return@withContext
            if (isMcbbsPage(doc.location())) {
                val urls = spider.getAllLink(doc)
                    .filter { isMcbbsPage(it) }
                    .filter { pageDeduplication(it) }
                    .filter { "plugin.php?id=link_redirect" !in it }

                logger.info("添加了 ${urls.size} 个链接")
                urls.forEach {
                    launch {
                        channel.send(it)
                    }
                }
            }
            if (isPostPage(url)) {
                val post = spider.getPostInfo(doc)
                if (post == null) {
                    logger.warn("抓取信息失败,可能是因为该页面不是帖子页面")
                    return@withContext
                }
                withContext(Dispatchers.IO) { launch { db.addPost(post) } }
            }
        }
    }

    private fun isPostPage(str: String): Boolean {
        return "thread-" in str
    }

    private fun isMcbbsPage(str: String): Boolean {
        return "www.mcbbs.net" in str
    }

    private fun pageDeduplication(url: String): Boolean {
        return urls.add(url)
    }
}