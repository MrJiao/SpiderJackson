

import com.get_proxy_demo.Source;
import com.get_proxy_demo.YouDaiLiPage1;
import com.jackson.common.control.*;
import com.jackson.db.po.Url;
import com.jackson.db.service.UrlService;
import com.jackson.reservoir.HttpGetPool;
import org.apache.http.client.methods.HttpGet;


import java.util.ArrayList;
import java.util.Random;

import static com.jackson.common.control.UserAgentControl.UserAgentType.*;


/**
 * Created by Jackson on 2016/10/27.
 */
public class Main {

    public static void main(String[] args) {
        getProxyTest();

    }


    private static final String url0 = "http://www.youdaili.net/Daili/guonei/list_";
    private static void getProxyTest() {
        UrlService urlService = new UrlService("url_spider_proxy");
        ArrayList<Url> arr = new ArrayList<>();
        for (int i = 1; i < 28; i++) {
            Url url1 = Url.newHttpGetUrl(url0 + i + ".html", YouDaiLiPage1.class);
            url1.setPriority(1);
            arr.add(url1);
        }
        urlService.insert(arr);

        SimpleRequestControl simpleGetControl = SimpleRequestControl.newInstance(urlService);
        simpleGetControl.setHttpGetConfigHandler(new HttpGetPool.HttpGetConfigHandler() {
            @Override
            public HttpGet setConfig(HttpGet httpGet, Url url) {
                httpGet.setHeader("User-Agent", UserAgentControl.getInstance().next(PC));
                return httpGet;
            }
        });

        simpleGetControl.setThreadSize(2).start();
    }
}
