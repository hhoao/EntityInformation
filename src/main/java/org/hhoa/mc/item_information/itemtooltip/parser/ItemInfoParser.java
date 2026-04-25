package org.hhoa.mc.item_information.itemtooltip.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hhoa.mc.item_information.itemtooltip.item.ItemInfo;
import org.hhoa.mc.item_information.itemtooltip.item.TAG;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ItemInfoParser
 *
 * @author xianxing
 * @since 2024/10/20
 */
public class ItemInfoParser {
    public static void main(String[] args) throws IOException, InterruptedException {
        String path = ItemInfoParser.class.getResource("/").getPath();
        //        File jsonItemDist = new File(path, "convertedItems");
        //        File fetchItemHTMLDir = new File(path, "items");
        //        fetchItemsHtml("https://minecraft.fandom.com/zh/wiki/%E7%89%A9%E5%93%81",
        // fetchItemHTMLDir, false);
        //        parseItems(fetchItemHTMLDir, jsonItemDist);

        File jsonBlockDist = new File(path, "convertedBlocks");
        File fetchBlockHTMLDir = new File(path, "blocks");
        fetchItemsHtml(
                "https://minecraft.fandom.com/zh/wiki/%E6%96%B9%E5%9D%97", fetchBlockHTMLDir, true);
        parseItems(fetchBlockHTMLDir, jsonBlockDist);
    }

    public static void fetchItemsHtml(String catalog, File dist, boolean isBlock)
            throws IOException {
        String catalogHtml;
        try (CloseableHttpClient httpclient = createHttpClient()) {
            HttpGet httpget = new HttpGet(catalog);
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                HttpEntity entity = response.getEntity();
                catalogHtml = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
        }
        Document doc = Jsoup.parse(catalogHtml, "utf-8");
        HashMap<String, String> map = new HashMap<>();
        if (!isBlock) {
            Elements children =
                    doc.getElementsByClass("mw-parser-output").get(0).getElementsByTag("div");
            for (int i = 4; i < children.size(); i++) {
                if (children.get(i).hasClass("div-col")
                        && children.get(i).hasClass("columns")
                        && children.get(i).hasClass("column-width")) {
                    Element element = children.get(i);
                    Elements lis = element.getElementsByTag("li");
                    for (Element li : lis) {
                        Element a = li.child(0);
                        map.put(a.text(), a.attribute("href").getValue());
                    }
                }
            }
        } else {
            Elements children =
                    doc.getElementsByClass("mw-parser-output").get(0).getElementsByTag("div");
            for (Element child : children) {
                if (child.hasClass("div-col")
                        && child.hasClass("columns")
                        && child.hasClass("column-width")) {
                    Elements lis = child.getElementsByTag("li");
                    for (Element li : lis) {
                        Elements as = li.getElementsByTag("a");
                        for (Element a : as) {
                            if (a.hasClass("mw-redirect")) {
                                if (!a.text().contains("info_update")) {
                                    String url =
                                            "https://minecraft.fandom.com"
                                                    + a.attribute("href").getValue();
                                    System.out.println(a.text() + "   " + url);
                                    map.put(a.text(), url);
                                }
                            }
                        }
                    }
                }
            }
        }
        try (CloseableHttpClient httpclient = createHttpClient()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                HttpGet httpget = new HttpGet(entry.getValue());
                try {
                    try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                        HttpEntity entity = response.getEntity();
                        String result = EntityUtils.toString(entity);
                        EntityUtils.consume(entity);
                        FileUtils.writeStringToFile(
                                new File(dist, entry.getKey() + ".html"),
                                result,
                                StandardCharsets.UTF_8);
                    }
                } catch (ClientProtocolException e) {
                    System.out.println("ERROR " + entry.getValue());
                }
            }
        }
    }

    private static CloseableHttpClient createHttpClient() {
        RequestConfig requestConfig =
                RequestConfig.copy(RequestConfig.DEFAULT)
                        .setConnectTimeout(60000)
                        .setConnectionRequestTimeout(60000)
                        .setSocketTimeout(60000)
                        .build();
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    public static void parseItems(File fetchItemHTMLDir, File dist) throws IOException {
        File[] htmlItemFiles = fetchItemHTMLDir.listFiles();
        ObjectMapper objectMapper = new ObjectMapper();
        FileUtils.deleteDirectory(dist);
        dist.mkdir();
        for (int i = 0; i < Objects.requireNonNull(htmlItemFiles).length; i++) {
            ItemInfo item = parseItem(htmlItemFiles[i]);
            Set<String> id = item.getInfo("ID");
            if (id != null) {
                for (String is : id) {
                    objectMapper.writeValue(new File(dist, is + ".json"), item);
                }
            }
        }
    }

    private static ItemInfo parseItem(File file) throws IOException {
        Document doc = Jsoup.parse(file, "utf-8");
        Elements elements = doc.getElementsByClass("mw-parser-output").get(0).children();
        String name = file.getName().split("\\.")[0];
        ItemInfo item = new ItemInfo(name);
        String currentTag = TAG.DESC.getCn();

        for (Element element : elements) {
            if (element.tagName().equals("p") && TAG.DESC.getCn().equals(currentTag)) {
                item.getInfo(TAG.DESC.getCn()).add(element.text().trim());
            } else if (element.id().equals("toc")) {
                currentTag = null;
            } else if (element.tagName().equals("h2")) {
                currentTag = null;
                String text = element.text();
                if (text.contains("数据值")) {
                    currentTag = "ID";
                } else {
                    for (TAG tag : TAG.values()) {
                        if (text.contains(tag.getCn())) {
                            currentTag = tag.getCn();
                        }
                    }
                }
            } else if (element.tagName().equals("h3")
                    && currentTag != null
                    && !currentTag.equals("ID")) {
                Set<String> info = item.getInfo(currentTag);
                Element headLine = element.getElementsByClass("mw-headline").get(0);
                info.add(headLine.text().trim());
            } else if (currentTag != null && currentTag.equals("ID")) {
                if (element.tagName().equals("table")) {
                    Set<String> ids = item.getInfo(currentTag);
                    for (Element tbody : element.children()) {
                        Elements trs = tbody.children();
                        for (int i = 1; i < trs.size(); i++) {
                            Element td = trs.get(i).children().get(1);
                            ids.add(td.text().trim());
                        }
                    }
                } else if (element.tagName().equals("h3") && !element.text().contains("ID")) {
                    currentTag = null;
                }
            }
        }
        return item;
    }
}
