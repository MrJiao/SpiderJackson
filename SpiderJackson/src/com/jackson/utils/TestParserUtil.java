package com.jackson.utils;

import com.jackson.common.task.ParserTestTask;
import com.jackson.db.po.Url;
import com.jackson.task.parser.IParser;

/**
 * Created by Jackson on 2016/12/25.
 */
public class TestParserUtil {

    public static void testParser(Url url, Class<? extends IParser> clazz){
        url.setParserClass(clazz);
        new ParserTestTask(url).run();

    }

}
