package com.xjs.sina.task;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.xjs.annotation.ReptileLog;
import com.xjs.common.util.HttpUtils;
import com.xjs.sina.pojo.SinaNews;
import com.xjs.sina.service.SinaNewsService;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 新浪新闻爬虫任务
 *
 * @author xiejs
 * @since 2022-02-15
 */
@Component
@Log4j2
public class SinaNewsTask {

    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private SinaNewsService sinaNewsService;

    public static final String URL = "https://news.sina.com.cn/";

    @ReptileLog(name = "新浪新闻", url = URL)
    public Long reptileSinaNews() {
        //定义循环次数计时器
        Long count = 0L;

        try {

            String html = httpUtils.doGetHtml(URL);

            Document document = Jsoup.parse(html);

            count = this.parse(document, count);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return count;
    }

    /**
     * 解析dom
     *
     * @param document dom
     * @param count    循环次数
     */
    private Long parse(Document document, Long count) {
        //获取子链接
        Elements nav_mod_1 = document.getElementsByClass("nav-mod-1");
        Elements link = nav_mod_1.select("ul > li > a");
        List<Map<String, String>> hrefList = link.stream().map(a -> {
            String href = a.attr("href");
            String text = a.text();
            Map<String, String> map = new HashMap<>();
            map.put(text, href);
            return map;
        }).collect(Collectors.toList());
        hrefList.removeIf(s -> s.containsKey("javascript:;"));

        for (Map<String, String> map : hrefList) {
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                String html = httpUtils.doGetHtml(entry.getValue());
                Document docChild = Jsoup.parse(html);

                Long newCount = this.parseChile(docChild, entry.getKey(), count);

                count = count + newCount;
            }

        }
        return count;
    }

    /**
     * 解析子dom
     *
     * @param docChild 子
     * @param key      key
     */
    private Long parseChile(Document docChild, String key, Long count) {
        try {
            Elements a = docChild.getElementsByTag("a");
            ArrayList<String> link = new ArrayList<>();
            for (Element element : a) {
                String href = element.attr("href");
                if (href.contains(".html") || href.contains(".shtml")) {
                    link.add(href);
                }
            }

            ArrayList<SinaNews> sinaNewsList = new ArrayList<>();

            //遍历每个文章页面，然后持久化到数据库
            for (String url : link) {
                //url不包含yyyy-dd-  直接跳过
                if (!url.contains("-")) {
                    continue;
                }
                String html = httpUtils.doGetHtml(url);
                Document document = Jsoup.parse(html);
                Elements main_title = document.getElementsByClass("main-title");
                Elements tit = document.getElementsByClass("tit");
                Element artibodyTitle = document.getElementById("artibodyTitle");
                Elements F_yahei = document.getElementsByClass("F-yahei");
                Elements crt_h1 = document.select(".crticalcontent > h1");
                Elements crth_h1 = document.select(".article-header > h1");


                if (CollUtil.isNotEmpty(main_title)
                        || CollUtil.isNotEmpty(tit)
                        || artibodyTitle != null
                        || CollUtil.isNotEmpty(F_yahei)
                        || CollUtil.isNotEmpty(crt_h1)
                        || CollUtil.isNotEmpty(crth_h1)) {
                    String title = null;
                    if (CollUtil.isNotEmpty(main_title)) {
                        title = main_title.text();
                    }
                    if (title == null) {
                        if (CollUtil.isNotEmpty(tit)) {
                            title = tit.text();
                        }
                    }
                    if (title == null) {
                        if (artibodyTitle != null) {
                            title = artibodyTitle.text();
                        }
                    }
                    if (title == null) {
                        if (CollUtil.isNotEmpty(F_yahei)) {
                            title = F_yahei.text();
                        }
                    }
                    if (title == null) {
                        if (CollUtil.isNotEmpty(crt_h1)) {
                            title = crt_h1.text();
                        }
                    }
                    if (title == null) {
                        if (CollUtil.isNotEmpty(crth_h1)) {
                            title = crth_h1.text();
                        }
                    }

                    if (StringUtils.isEmpty(title)) {
                        continue;
                    }

                    //持久化
                    SinaNews sinaNews = new SinaNews();
                    sinaNews.setCategory(key);
                    sinaNews.setTitle(title);
                    sinaNews.setUrl(url);
                    sinaNews.setCreateTime(new Date());

                    sinaNewsList.add(sinaNews);
                }
            }

            //计数
            count++;

            sinaNewsService.saveBatch(sinaNewsList, 30);

            //删除重复
            int num = sinaNewsService.deleteRepeatData();
            log.info("重复数据为:{}", num);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return count;
    }

}
