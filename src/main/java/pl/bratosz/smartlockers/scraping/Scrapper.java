package pl.bratosz.smartlockers.scraping;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.date.FormatDate;
import pl.bratosz.smartlockers.model.Article;
import pl.bratosz.smartlockers.model.EmployeeCloth;
import pl.bratosz.smartlockers.model.Size;
import pl.bratosz.smartlockers.service.ArticleService;

import java.io.IOException;
import java.util.*;

@Component
public class Scrapper {
    private ArticleService articleService;
    private OnlineConnection oc;

    public Scrapper(ArticleService articleService) {
        this.articleService = articleService;
    }

    public void createConnection(String login, String password) {
        oc = new OnlineConnection(login, password);
    }

    public void findByLockerAndBox(Integer lockerNo, Integer boxNo) {
        try {
            oc.getFormParameters(oc.getMainPage().parse());
            putLockerNoAndBoxNoToForm(lockerNo.toString(), boxNo.toString());
            Document doc = standardPost().parse();
            oc.getFormParameters(doc);
            oc.setMainPage(clickViewButton());
            oc.setActualPage(getMainPage().parse());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEmployeeLastName() {
        return oc.getActualPage().select("#ctl00_MainContent_GridView1 > tbody > tr:nth-child(2) > td:nth-child(3)").text();
    }

    public Set<EmployeeCloth> getClothes() {
        Elements elements = oc.getActualPage().select("#ctl00_MainContent_GridView2 > tbody > tr");
        Set<EmployeeCloth> clothes = new HashSet<>();
        for(int i = 1; i < elements.size(); i++) {
            Elements td = elements.get(i).select("td");
            EmployeeCloth cloth = new EmployeeCloth(
                    getId(td), getAssignment(td), getLastWashing(td),
                    getRelease(td), getOrdinalNo(td), getArticleByArticleNumber(td), getSize(td));
            clothes.add(cloth);
        }
        return clothes;
    }

    private Connection.Response clickViewButton() {
        updateFormWithClick();
        try {
            return standardPost();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateFormWithClick() {
        oc.formParameters.put("__EVENTTARGET", "ctl00$MainContent$GridView1");
        oc.formParameters.put("__EVENTARGUMENT", "Select$0");
    }

    private Connection.Response standardPost() throws IOException {
        return Jsoup.connect(oc.baseURL)
                .method(Connection.Method.POST)
                .userAgent(oc.userAgentChrome)
                .referrer(oc.referrer)
                .timeout(10 * 1000)
                .cookies(oc.cookies)
                .data(oc.formParameters)
                .followRedirects(true)
                .execute();
    }

    public Connection.Response getMainPage(){
        return  oc.mainPage;
    }



    private void putLockerNoAndBoxNoToForm(String lockerNo, String boxNo) {
        oc.formParameters.put("ctl00$MainContent$szafa", lockerNo);
        oc.formParameters.put("ctl00$MainContent$box", boxNo);
        oc.formParameters.put("__EVENTTARGET", "ctl00$MainContent$szafa");
    }

    private int getOrdinalNo(Elements td) {
        return Integer.parseInt(td.get(0).text());
    }

    private Article getArticleByArticleNumber(Elements td) {
        return articleService.getByArticleNumber(Integer.parseInt(td.get(1).text()));
    }

    private Size getSize(Elements td) {
        String size = td.get(3).text();
        return Size.getSizeByName(size);
    }

    private Date getAssignment(Elements td) {
        String date = td.get(4).text();
        return FormatDate.getDate(date);
    }

    private long getId(Elements td) {
        return Long.parseLong(td.get(5).text());
    }

    private Date getRelease(Elements td) {
        String date = td.get(6).text();
        return FormatDate.getDate(date);
    }

    private Date getLastWashing(Elements td) {
        String date = td.get(7).text();
        return FormatDate.getDate(date);
    }
}
