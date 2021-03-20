package pl.bratosz.smartlockers.scraping;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.date.FormatDate;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

import pl.bratosz.smartlockers.service.ArticleService;
import pl.bratosz.smartlockers.strings.MyString;

import java.io.IOException;
import java.util.*;

@Service
public class Scrapper {
    private ArticleService articleService;
    private OnlineConnection connection;
    private String login;
    private String password;

    public Scrapper(ArticleService articleService) {
        this.articleService = articleService;
        login = "";
        password = "";
    }

    public void createConnection(Plant plant) {
        login = plant.getLogin();
        password = plant.getPassword();
        if (itIsSamePlant(login, password) && connection != null) {
            try {
                connection.checkConnection();
            } catch (IOException e) {
                updateActualLoginData(login, password);
                connection = new OnlineConnection(login, password);
            }
        } else {
            updateActualLoginData(login, password);
            connection = new OnlineConnection(login, password);
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

    public void find(Box box) {
        int lockerNo = box.getLocker().getLockerNumber();
        int boxNo = box.getBoxNumber();
        searchByLockerNoAndBoxNo(lockerNo, boxNo);
    }

    public void find(Locker locker) {
        int lockerNumber = locker.getLockerNumber();
        searchByLockerNumber(lockerNumber);
    }

    private void searchByLockerNoAndBoxNo(Integer lockerNo, Integer boxNo) {
        putLockerNoAndBoxNoToForm(lockerNo.toString(), boxNo.toString());
        connection.standardPost();
    }

    private void searchByLockerNumber(Integer lockerNumber) {
        putLockerNumberToForm(lockerNumber.toString());
        connection.standardPost();
    }



    public String getDepartmentName() {
        return MyString.create(connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(1)").text()).get();
    }

    public String getEmployeeLastName() {
        return MyString.create(connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(3)").text()).get();
    }

    public String getEmployeeFirstName(int rowIndex) {
        Element boxRow = getBoxRow(rowIndex);
        return MyString.create(boxRow.select("td").get(1).text()).get();
    }

    public String getEmployeeLastName(int rowIndex) {
        Element boxRow = getBoxRow(rowIndex);
        return MyString.create(boxRow.select("td").get(2).text()).get();
    }

    public String getDepartmentName(int rowIndex) {
        Element boxRow = getBoxRow(rowIndex);
        return MyString.create(boxRow.select("td").get(0).text()).get();
    }


    private Element getBoxRow(int rowIndex) {
        Elements boxesRows = getBoxesRows();
        return boxesRows.get(rowIndex);

    }

    public String getEmployeeFirstName() {
        return MyString.create(connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(2)").text()).get();
    }


    public List<Cloth> getClothes() {
        Elements elements = connection.getActualPage().select(
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

    public int getBoxNumberByTableRow(int rowIndex) {
        Elements boxesRows = getBoxesRows();
        return getBoxNumber(rowIndex, boxesRows);
    }

    private Elements getBoxesRows() {
        return connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > tr");
    }

    public List<Integer> getBoxNumbers() {
        Elements boxesRows = getBoxesRows();
        List<Integer> boxNumbers = new LinkedList<>();
        for(int i = 1; i < boxesRows.size(); i++) {
            boxNumbers.add(
                    getBoxNumber(i, boxesRows));
        }
        return boxNumbers;
    }

    public void clickViewButton() {
        int buttonIndex = 0;
        clickViewButton(buttonIndex);
    }

    public void clickViewButton(int buttonIndex) {
        updateFormWithClick(buttonIndex);
        connection.standardPost();
    }

    private void updateFormWithClick(int buttonIndex) {
        connection.formParameters.put("__EVENTTARGET", "ctl00$MainContent$GridView102");
        connection.formParameters.put("__EVENTARGUMENT", "Select$" + buttonIndex);
    }


    private void putLockerNoAndBoxNoToForm(String lockerNo, String boxNo) {
        connection.setFormParameters(new HashMap<String, String>());
        connection.formParameters.put("ctl00$MainContent$szafa_akt", lockerNo);
        connection.formParameters.put("ctl00$MainContent$box_akt", boxNo);
        connection.formParameters.put("__EVENTTARGET", "ctl00$MainContent$box_akt");
    }

    private void putLockerNumberToForm(String lockerNumber) {
        connection.setFormParameters(new HashMap<String, String>());
        connection.formParameters.put("ctl00$MainContent$szafa_akt", lockerNumber);
        connection.formParameters.put("__EVENTTARGET", "ctl00$MainContent$box_akt");
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

    private Integer getBoxNumber(int rowIndex, Elements boxesRows) {
        String boxNumber = boxesRows.get(rowIndex)
                .select("td").get(5).text();
        return Integer.valueOf(boxNumber);
    }
}
