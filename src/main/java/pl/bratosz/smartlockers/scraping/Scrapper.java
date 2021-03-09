package pl.bratosz.smartlockers.scraping;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.date.FormatDate;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

import pl.bratosz.smartlockers.service.ArticleService;
import pl.bratosz.smartlockers.strings.MyString;

import java.io.IOException;
import java.util.*;

@Component
public class Scrapper {
    private ArticleService articleService;
    private OnlineConnection oc;
    private String login;
    private String password;

    public Scrapper(ArticleService articleService) {
        this.articleService = articleService;
        login = "";
        password = "";
    }

    public void createConnection(String login, String password) {
        if (itIsSamePlant(login, password) && oc != null) {
            try {
                oc.checkConnection();
            } catch (IOException e) {
                updateActualLoginData(login, password);
                oc = new OnlineConnection(login, password);
            }
        } else {
            updateActualLoginData(login, password);
            oc = new OnlineConnection(login, password);
        }
    }

    private void updateActualLoginData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    private boolean itIsSamePlant(String login, String password) {
        if (this.login.equals(login) && this.password.equals(password)) return true;
        return false;
    }

    public void findByLockerAndBox(Integer lockerNo, Integer boxNo) {
        searchByLockerNoAndBoxNo(lockerNo, boxNo);
        clickViewButton();
    }

    private void searchByLockerNoAndBoxNo(Integer lockerNo, Integer boxNo) {
        putLockerNoAndBoxNoToForm(lockerNo.toString(), boxNo.toString());
        oc.standardPost();

    }

    public String getDepartmentName() {
        return MyString.create(oc.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(1)").text()).get();
    }

    public String getEmployeeLastName() {
        return MyString.create(oc.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(3)").text()).get();
    }

    public String getEmployeeFirstName() {
        return MyString.create(oc.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(2)").text()).get();
    }


    public List<Cloth> getClothes() {
        Elements elements = oc.getActualPage().select(
                "#ctl00_MainContent_GridView2 > tbody > tr");
        List<Cloth> clothes = new LinkedList<>();
        for (int i = 1; i < elements.size(); i++) {
            Elements td = elements.get(i).select("td");
            Cloth cloth = new Cloth(
                    getBarcode(td),
                    getAssignment(td),
                    getLastWashing(td),
                    getRelease(td),
                    getOrdinalNo(td),
                    getArticleByArticleNumber(td),
                    getSize(td));
            clothes.add(cloth);
        }
        return clothes;
    }

    private void clickViewButton() {
        updateFormWithClick();
        oc.standardPost();
    }

    private void updateFormWithClick() {
        oc.formParameters.put("__EVENTTARGET", "ctl00$MainContent$GridView102");
        oc.formParameters.put("__EVENTARGUMENT", "Select$0");
    }


    private void putLockerNoAndBoxNoToForm(String lockerNo, String boxNo) {
        oc.setFormParameters(new HashMap<String, String>());
        oc.formParameters.put("ctl00$MainContent$szafa_akt", lockerNo);
        oc.formParameters.put("ctl00$MainContent$box_akt", boxNo);
        oc.formParameters.put("__EVENTTARGET", "ctl00$MainContent$box_akt");
    }

    private int getOrdinalNo(Elements td) {
        return Integer.parseInt(td.get(0).text());
    }

    private Article getArticleByArticleNumber(Elements td) {
        int articleNumber = Integer.parseInt(td.get(1).text());
        Article article = articleService.get(articleNumber);
        if (article == null) {
            String articleName = td.get(2).text();
            return articleService.addNewArticle(articleNumber, articleName);
        }
        return article;
    }

    private ClothSize getSize(Elements td) {
        String size = td.get(3).text();
        return ClothSize.getSizeByName(size);
    }

    private Date getAssignment(Elements td) {
        String date = td.get(4).text();
        return FormatDate.getDate(date);
    }

    private long getBarcode(Elements td) {
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
