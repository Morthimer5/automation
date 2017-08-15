import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StatusHomeTimelineTest {
    static String ConsumerKey = "96HPQVNBRtyOd87GfNYdU8tpu";
    static String ConsumerSecret = "QfrIUneNjugbPGTxbIPloJE3SEp1wG2jMGsklc5SjKF1uFAq4u";
    static String AccessToken = "896451540944289793-Gq285GsQsQZaOKQDkZudNE6MQmmVlXo";
    static String AccessSecret = "ZR3QftpR92kKwhjLfIjzhtWkp34oU2GVUV4XbRT9Reg7T";

    private WebDriver driver;
    private JSONObject jsonO;


    @Before
    public void setUp() throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException {
        //Open FireFox
        driver = new FirefoxDriver();
        driver.get("https://twitter.com/B73Vhj7Orw4PyV2");

        //Make Get request to TweeterAPI with application's autentification keys
        HttpClient client = HttpClientBuilder.create().build();
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        HttpGet request = new HttpGet("https://api.twitter.com/1.1/statuses/home_timeline.json");
        consumer.sign(request);
        HttpResponse response = (HttpResponse) client.execute(request);

        //Put response from API to String
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        //Get JSON object for last twitt in the line
        JSONArray json = new JSONArray(result.toString());
        jsonO= json.getJSONObject(0);
    }

    @After
    public void tearDown(){
        driver.quit();

    }

    @Test
    public void createAtValidation (){
        //This test fails because of date formats are not equal,
        //Need parse both to Date
        String createdAtAPI = jsonO.getString("created_at");
        String createAtWD = driver.findElement(By.className("tweet-timestamp")).getAttribute("title");
        Assert.assertTrue(createAtWD.equals(createdAtAPI));

    }

    @Test
    public void retweetsValidation(){
        int retweetsWD = Integer.valueOf(driver.findElement(By.className("js-actionRetweet")).findElement(By.className("ProfileTweet-actionCountForPresentation")).getAttribute("innerText"));
        int retweetsAPI= jsonO.getInt("retweet_count");
        Assert.assertTrue(retweetsAPI == retweetsWD);

    }
    @Test
    public void textValidation(){
        String textAPI = jsonO.getString("text");
        String textWD = driver.findElement(By.className("TweetTextSize--normal")).getAttribute("innerText");
        Assert.assertTrue(textAPI.equals(textWD));
    }
}
