package edu.luo123.McbbsSpider

import org.apache.log4j.BasicConfigurator

suspend fun main() {
    val STARTURL = arrayListOf(
        "https://www.mcbbs.net/forum.php"
        , "https://www.mcbbs.net/forum-map-1.html"
        , "https://www.mcbbs.net/forum-gameplay-1.html"
    )
    val DBFILE = "jdbc:sqlite:test.db"
    Class.forName("org.sqlite.JDBC")
    val db = DBBase(DBFILE)
    val controller = Controller(STARTURL, db)
    BasicConfigurator.configure()
    controller.start()

}