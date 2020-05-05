package pl.bratosz.smartlockers.scraping;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.bratosz.smartlockers.date.FormatDate;
import pl.bratosz.smartlockers.model.Cloth;
import pl.bratosz.smartlockers.model.EmployeeCloth;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.Size;
import pl.bratosz.smartlockers.service.exels.format.Format;

import java.io.IOException;
import java.util.*;

public class ServiceScrapper {
    private final String startURL = "http://klsonline24.pl";
    private final String loginURL = startURL + "/default.aspx";
    private final String baseURL = startURL + "/baza.aspx";
    private String referrer = "https://google.com/";
    private final String userAgentChrome = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36";
    private Map<String, String> cookies;
    private Map<String, String> formParameters;
    private Connection.Response mainPage;
    private Document actualPage;

    public ServiceScrapper(Locker.DepartmentNumber departmentNumber){
        try {
            String login = getLogin(departmentNumber);
            String password = getPassword(departmentNumber);
            Connection.Response response = grabLoginPage(startURL, referrer);
            Document loginPage = response.parse();
            getFormParameters(loginPage);
            cookies = response.cookies();
            referrer = startURL;
            mainPage = logIn(login, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findByLockerAndBox(Integer lockerNo, Integer boxNo) {
        try {
            getFormParameters(mainPage.parse());
            putLockerNoAndBoxNoToForm(lockerNo.toString(), boxNo.toString());
            Document doc = standardPost().parse();
            getFormParameters(doc);
            mainPage = clickViewButton();
            actualPage = mainPage.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEmployeeLastName() {
        return actualPage.select("#ctl00_MainContent_GridView1 > tbody > tr:nth-child(2) > td:nth-child(3)").text();
    }

    public Set<EmployeeCloth> getClothes() {
        Elements elements = actualPage.select("#ctl00_MainContent_GridView2 > tbody > tr");
        Set<EmployeeCloth> clothes = new HashSet<>();
        for(int i = 1; i < elements.size(); i++) {
            Elements td = elements.get(i).select("td");
            EmployeeCloth cloth = new EmployeeCloth(
                    getId(td), getAssignment(td), getLastWashing(td),
                    getRelease(td), getOrdinalNo(td), getArticleNo(td), getSize(td));
            clothes.add(cloth);
        }
        return clothes;
    }

    private String getPassword(Locker.DepartmentNumber departmentNumber) {
        if(departmentNumber.equals(Locker.DepartmentNumber.DEP_384)) {
            return "";
        } else {
            return "";
        }
    }

    private String getLogin(Locker.DepartmentNumber departmentNumber) {
        return "" +
                "" +
                "" +
                "" +
                "";
    }

    private Connection.Response logIn(String login, String password) {
        putLoginDataToForm(login, password);
        try {
            Connection.Response resp = Jsoup.connect(loginURL)
                    .userAgent(userAgentChrome)
                    .referrer(referrer)
                    .timeout(10 * 1000)
                    .cookies(cookies)
                    .data(formParameters)
                    .followRedirects(true)
                    .execute();
            cookies.putAll(resp.cookies());
            referrer = baseURL;
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
        formParameters.put("__EVENTTARGET", "ctl00$MainContent$GridView1");
        formParameters.put("__EVENTARGUMENT", "Select$0");
    }

    private Connection.Response standardPost() throws IOException {
        return Jsoup.connect(baseURL)
                .method(Connection.Method.POST)
                .userAgent(userAgentChrome)
                .referrer(referrer)
                .timeout(10 * 1000)
                .cookies(cookies)
                .data(formParameters)
                .followRedirects(true)
                .execute();
    }

    public Connection.Response getMainPage(){
        return  mainPage;
    }

    private void putLoginDataToForm(String login, String password) {
        formParameters.put("txtName", login);
        formParameters.put("txtPW", password);
    }

    private void putLockerNoAndBoxNoToForm(String lockerNo, String boxNo) {
        formParameters.put("ctl00$MainContent$szafa", lockerNo);
        formParameters.put("ctl00$MainContent$box", boxNo);
        formParameters.put("__EVENTTARGET", "ctl00$MainContent$szafa");
    }

    private Connection.Response grabLoginPage(String startURL, String referrer) {
        try {
            return Jsoup.connect(startURL)
                    .userAgent(userAgentChrome)
                    .referrer(referrer)
                    .timeout(10 * 1000)
                    .followRedirects(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getFormParameters(Document loginPage) {
        formParameters = new HashMap<String, String>();
        Elements elements = loginPage.select("input");
        for(Element e : elements) {
            String id = e.attr("name");
            String value = e.val();
            formParameters.put(id, value);
        }
        formParameters.remove("");
    }



    private int getOrdinalNo(Elements td) {
        return Integer.parseInt(td.get(0).text());
    }

    private int getArticleNo(Elements td) {
        return Integer.parseInt(td.get(1).text());
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
