package pl.bratosz.smartlockers.scraping;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.date.FormatDate;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

import pl.bratosz.smartlockers.model.clothes.LifeCycleStatus;
import pl.bratosz.smartlockers.service.ClientArticleService;
import pl.bratosz.smartlockers.strings.MyString;

import java.io.IOException;
import java.util.*;

@Service
public class Scrapper {
    private ClientArticleService clientArticleService;
    private OnlineConnection connection;
    private String login;
    private String password;

    public Scrapper(ClientArticleService clientArticleService) {
        this.clientArticleService = clientArticleService;
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
        findLocker(locker.getLockerNumber());
    }

    public void findLocker(int lockerNumber) {
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


    public List<Cloth> getClothes(Client client) {
        Elements elements = connection.getActualPage().select(
                "#ctl00_MainContent_GridView2 > tbody > tr");
        List<Cloth> clothes = new LinkedList<>();
        for (int i = 1; i < elements.size(); i++) {
            Elements td = elements.get(i).select("td");
            Date release = getRelease(td);
            Cloth cloth = new Cloth(
                    getBarcode(td),
                    getAssignment(td),
                    getLastWashing(td),
                    release,
                    getOrdinalNo(td),
                    getArticleByArticleNumber(td, client),
                    getSize(td),
                    getLifeCycleStatus(release));
            clothes.add(cloth);
        }
        return clothes;
    }

    private LifeCycleStatus getLifeCycleStatus(Date release) {
        if(release.compareTo(FormatDate.getDefaultDate()) == 0) {
            return LifeCycleStatus.BEFORE_RELEASE;
        } else {
            return LifeCycleStatus.IN_ROTATION;
        }
    }

    public List<Element> getAllRows() {
        Elements elements = connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > tr");
        return elements;
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

    public void clickViewButtonByRowIndex(int rowIndex) {
        int buttonIndex = rowIndex - 1;
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

    private ClientArticle getArticleByArticleNumber(Elements td, Client client) {
        int articleNumber = Integer.parseInt(td.get(1).text());
        String articleName = getArticleName(td);
        return clientArticleService.get(articleNumber, client, articleName);
    }

    private String getArticleName(Elements td) {
        return td.get(2).text();
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

    public void goToLockersView() {
        connection.goToLockersView();
    }

    public void goToRotationView() {
        connection.goToRotationView();
    }
}
