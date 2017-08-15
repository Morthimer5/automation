import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//Test adds tweet to timeline and compares text fields that it got from API and WebDriver

public class StatusUpdateTest {

    static String ConsumerKey = "96HPQVNBRtyOd87GfNYdU8tpu";
    static String ConsumerSecret = "QfrIUneNjugbPGTxbIPloJE3SEp1wG2jMGsklc5SjKF1uFAq4u";
    static String AccessToken = "896451540944289793-Gq285GsQsQZaOKQDkZudNE6MQmmVlXo";
    static String AccessSecret = "ZR3QftpR92kKwhjLfIjzhtWkp34oU2GVUV4XbRT9Reg7T";

    private WebDriver driver;
    private JSONObject json;
    private String tweetID;


    @Before
    public void setUp() throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {

        //Open FireFox
        driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10, 500);
        driver.get("https://twitter.com/B73Vhj7Orw4PyV2");

        //Make Get request to TweeterAPI with application's autentification keys
        HttpClient client = HttpClientBuilder.create().build();
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        HttpPost request = new HttpPost("https://api.twitter.com/1.1/statuses/update.json?status=Avada%20Kadavra");
        consumer.sign(request);
        HttpResponse response = (HttpResponse) client.execute(request);

        //Put response in String
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        //Get respons as JSON object
        json = new JSONObject(result.toString());
        tweetID = json.getString("id_str");



    }
    @After
    public void tearDown() throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {

//        Destroid status added in @Before
        HttpClient client = HttpClientBuilder.create().build();
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        HttpPost request = new HttpPost("https://api.twitter.com/1.1/statuses/destroy/" + tweetID + ".json");
        consumer.sign(request);
        client.execute(request);
        driver.quit();
    }


    //Check that new tweet appears in thetimeline
    @Test
    public void statusUpdateTest() {
        //Compare texts of latest added tweet from API and WebDriver
        String textAPI = json.getString("text");
        String textWD = driver.findElement(By.className("TweetTextSize--normal")).getAttribute("innerText");

    }

    //Check that posting the same tweeter caus 403 response status
    @Test
    public void statusDuplicationTest() throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {
        //Add one more tweet — the same as in @before
        HttpClient client = HttpClientBuilder.create().build();
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        HttpPost request = new HttpPost("https://api.twitter.com/1.1/statuses/update.json?status=Avada%20Kadavra");
        consumer.sign(request);
        HttpResponse response = (HttpResponse) client.execute(request);
        int codeResponse = response.getStatusLine().getStatusCode();

        //Check if response status is — 403
        Assert.assertTrue(codeResponse == 403);
    }
}
