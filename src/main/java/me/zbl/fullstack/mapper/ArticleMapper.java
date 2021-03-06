package me.zbl.fullstack.mapper;

import me.zbl.fullstack.entity.Article;
import me.zbl.fullstack.entity.Tag;
import me.zbl.fullstack.entity.dto.form.ArticleSearchForm;
import me.zbl.fullstack.framework.mapper.IMyMapper;
import me.zbl.fullstack.mapper.provider.ArticleSqlProvider;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author czbcxy
 */
public interface ArticleMapper extends IMyMapper<Article> {

    String COLUMN_LIST_HOT = "id," +
            "CONCAT('[<small style=','color:red;','>热</small>] ',title) as title ," +
            "introduction,gmt_create AS gmtCreate,gmt_modified AS gmtModified ,see_count as seeCount";
    String COLUMN_LIST_NEW = "id," +
            "CONCAT('[<small style=','color:red;','>New</small>] ',title) as title ," +
            "introduction,gmt_create AS gmtCreate,gmt_modified AS gmtModified ,see_count as seeCount";
    String COLUMN_LIST = "article.id,article.title,article.introduction,article.gmt_create AS gmtCreate,article.gmt_modified AS gmtModified ,article.see_count as seeCount";

    @Select({"SELECT",
            COLUMN_LIST_NEW,
            " FROM article ",
            "WHERE DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= DATE(gmt_create) ",
            "ORDER BY see_count DESC",
            "limit 1,999999"
    })
    List<Article> getPostViewAllArticlesByToday();
    @Select({"SELECT",
            COLUMN_LIST_HOT,
            "FROM article ",
            "ORDER BY see_count DESC",
            "limit 1"
    })
    List<Article> getPostViewAllHotArticles();
    @Select({
            "SELECT",
            COLUMN_LIST,
            "FROM",
            "article ",
            "WHERE id NOT IN(SELECT id FROM article WHERE DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= DATE(gmt_create))",
//            "ORDER BY see_count DESC ",
//            "limit #{size}"
    })
    List<Article> getPostViewAllArticles();

    /**
     * 通过 tag id 查找文章
     *
     * @param id tag id
     * @return 符合条件的文章
     */
    @Select({
            "SELECT",
            COLUMN_LIST,
            "FROM article",
            "INNER JOIN tag_article",
            "ON tag_article.article_id = article.id",
            "AND tag_article.tag_id=#{id}",
            "ORDER BY article.gmt_create DESC"
    })
    List<Article> getArticleListByTagId(Integer id);

    /**
     * 通过条件查找文章
     *
     * @param form 条件表单
     * @return 符合条件的文章
     */
    @SelectProvider(type = ArticleSqlProvider.class, method = "getArticleByCondition")
    List<Article> getArticleListByCondition(ArticleSearchForm form);

    /**
     * 增加访问量
     *
     * @param id
     */
    @Update({
            "UPDATE article SET  see_count = see_count + 1 WHERE id = #{id}"
    })
    void updateCountById(Integer id);

    @Select({
        "select id,name from tag"
    })
    List<Tag> blogSelectTags();
}