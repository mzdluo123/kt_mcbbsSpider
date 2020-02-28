package edu.luo123.McbbsSpider;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.log4j.Logger;


import java.sql.SQLException;

public class DBBase {
    Dao<Post, Integer> postDao;
    Logger logger = Logger.getLogger(DBBase.class);

    public DBBase(String url) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource(url);

        postDao = DaoManager.createDao(connectionSource, Post.class);
        try {
            TableUtils.createTable(connectionSource, Post.class);
        }catch (SQLException e){
            logger.info("数据表已存在");
        }
    }

    public void addPost(Post post) {
        try {
            if (postDao.idExists(post.id)){
                logger.warn(post.toString());
                return;
            }
            postDao.create(post);
            logger.info(post.toString());
        } catch (SQLException e) {
            logger.error("发生错误,正在重试 " + post.toString(), e);
            addPost(post);
        }
    }
}
