package pl.bratosz.smartlockers.scraping;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OnlineConnection {
    final String startURL = "http://klsonline24.pl";
    final String loginURL = startURL + "/default.aspx";
    final String baseURL = startURL + "/baza.aspx";
    String referrer = "https://google.com/";
    final String userAgentChrome = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36";
    Map<String, String> cookies;
    Map<String, String> formParameters;
    Connection.Response mainPage;
    Document actualPage;


    public OnlineConnection(String login, String password) {
        try {
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

    void getFormParameters(Document loginPage) {
        formParameters = new HashMap<String, String>();
        Elements elements = loginPage.select("input");
        for(Element e : elements) {
            String id = e.attr("name");
            String value = e.val();
            formParameters.put(id, value);
        }
        formParameters.remove("");
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

    private void putLoginDataToForm(String login, String password) {
        formParameters.put("txtName", login);
        formParameters.put("txtPW", password);
    }

    public String getStartURL() {
        return startURL;
    }

    public String getLoginURL() {
        return loginURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getUserAgentChrome() {
        return userAgentChrome;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(Map<String, String> formParameters) {
        this.formParameters = formParameters;
    }

    public Connection.Response getMainPage() {
        return mainPage;
    }

    public void setMainPage(Connection.Response mainPage) {
        this.mainPage = mainPage;
    }

    public Document getActualPage() {
        return actualPage;
    }

    public void setActualPage(Document actualPage) {
        this.actualPage = actualPage;
    }
}



