import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller {


    @FXML
    private TextField domainText;

    @FXML
    private TextArea subResultArea;

    @FXML
    private TextField whoisDomainText;

    @FXML
    private TextArea whoisResultArea;

    @FXML
    private Label ErrorLable;

    @FXML
    private Button copySubBtn;

    @FXML
    public void SubdomainBtnClick() {
        subResultArea.setText(null);
        whoisDomainText.setText(domainText.getText());
        String domain = domainText.getText();
        String geturl="http://www.5118.com/subdomains/";
        String posturl="http://i.links.cn/subdomain/";

        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("domain", domain));
        parameters.add(new BasicNameValuePair("b2", "1"));
        parameters.add(new BasicNameValuePair("b3", "1"));
        parameters.add(new BasicNameValuePair("b4", "1"));

        HtmlGet get=new HtmlGet(geturl);
        HtmlPost post=new HtmlPost(posturl,parameters);

        try {
            get.start();
            post.start();
            get.join();
            post.join();
            String result=get.result+post.result;
            DelDuplicateDomain(result);
        } catch (InterruptedException e){
            e.printStackTrace();
        }



    }

    @FXML
    public void onSaveBtnClick() {

        FileChooser fileChooser = new FileChooser();
        Stage s = new Stage();
        File file = fileChooser.showSaveDialog(s);
        if (file == null) {
            return;
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            StringBuilder textemp = new StringBuilder();


        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    public void WhoisBtnClick() {
        String domain = whoisDomainText.getText();
        String whoisurl = "http://whois.263.tw/weixinindex.php?domain="+domain;
        HtmlGet get=new HtmlGet(whoisurl);

        try {
            get.start();
            get.join();
            whoisResultArea.setText(get.result);
        } catch (InterruptedException e){
            e.printStackTrace();
        }


    }

    @FXML
    private void copySubBtnClick() {
        StringSelection stsel = new StringSelection(subResultArea.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
    }


    public void DelDuplicateDomain(String result) {

        String domain = domainText.getText();
        Pattern pattern = Pattern.compile("[0-9a-zA-z]+\\." + domain);
        Matcher matcher = pattern.matcher(result);
        ArrayList<String> subdomain = new ArrayList<>();
        ArrayList<Object> newsubdomain = new ArrayList<>();

        while (matcher.find()) {
            subdomain.add(matcher.group(0));
        }

        Set set = new HashSet<>();


        for (Iterator iter = subdomain.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element)) {
                newsubdomain.add(element);
            }
        }
        subResultArea.setText(newsubdomain.toString().replace(",","\n").replace("[","").replace("]","").replace(" ",""));
        if (newsubdomain.toString().length() == 2) {
            subResultArea.setText("抱歉，没有搜索到" + domain + "的相关子域名数据。");
        }


    }


    class HtmlPost extends Thread {

        HttpClient client=HttpClients.createDefault();
        String url;
        String result;
        List<BasicNameValuePair> parameters;
        HtmlPost(String url,List<BasicNameValuePair> parameters) {
            this.url=url;
            this.parameters=parameters;
        }
        @Override
        public void run() {

            org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(url);

            try {

                post.setEntity(new UrlEncodedFormEntity(parameters,"UTF-8"));
                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                this.result = EntityUtils.toString(entity,"UTF-8");
            } catch (IOException e){
                ErrorLable.setText("抱歉，网络出错。");
            }

        }

    }
    class HtmlGet extends Thread {
        HttpClient client=HttpClients.createDefault();
        String url;
        String result;
        HtmlGet (String url) {
            this.url=url;
        }
        @Override
        public void run() {
            org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(url);

            try {
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                this.result = EntityUtils.toString(entity,"UTF-8");
            } catch (IOException e){
                ErrorLable.setText("抱歉，网络出错。");
            }

        }

    }
}




