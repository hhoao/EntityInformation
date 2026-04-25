package org.hhoa.mc;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hhoa.mc.item_information.itemtooltip.item.ItemInfo;

/**
 * ItemInfoFormatter
 *
 * @author xianxing
 * @since 2024/10/21
 */
public class ItemInfoFormatter {
    public static void main(String[] args) throws IOException {
        File file =
                new File(
                        "C:\\Users\\haung\\IdeaProjects\\first_mod_39\\src\\main\\resources\\assets\\first_mod_39\\item_infos\\minecraft");
        Gson gson = new Gson();
        for (File listFile : file.listFiles()) {
            ItemInfo itemInfo = gson.fromJson(Files.readString(listFile.toPath()), ItemInfo.class);
            Map<String, Set<String>> infos = itemInfo.getInfos();
            Set<String> s = new HashSet<>();
            Set<String> intro = infos.get("简介");
            Iterator<String> iterator = intro.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.isBlank()) {
                    iterator.remove();
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    boolean open = false;
                    for (int i = 0; i < next.length(); i++) {
                        char c = next.charAt(i);
                        if (c == '（') {
                            open = true;
                            continue;
                        }
                        if (c == '）') {
                            open = false;
                            continue;
                        }
                        if (!open) {
                            stringBuilder.append(next.charAt(i));
                        }
                    }
                    s.add(stringBuilder.toString());
                }
            }
            infos.put("简介", s);
            itemInfo.setInfos(infos);
            String json = gson.toJson(itemInfo);
            listFile.delete();
            Files.writeString(listFile.toPath(), json);
        }
    }
}
