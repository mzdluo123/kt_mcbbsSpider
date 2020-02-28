package edu.luo123.McbbsSpider;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "post")
public class Post {

    @DatabaseField(id = true)
    public int id;

    @DatabaseField
    public String title;


    @DatabaseField
    public String url;

    @DatabaseField
    public String author;
    @DatabaseField(columnName = "author_home")
    public String authorHome;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorHome() {
        return authorHome;
    }

    public void setAuthorHome(String authorHome) {
        this.authorHome = authorHome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Post(int id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public Post() {

    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", authorHome='" + authorHome + '\'' +
                '}';
    }
}
