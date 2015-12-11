import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    @FXML
    private TextField domainText;

    @FXML
    private TextArea resultArea;

    @FXML
    public void onSearchBtnClick() {
        new GetSubDomain().ReadByPost();

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


    public void DelDuplicate(String getresult, String postresult) {
        resultArea.setText(null);
        String domain = domainText.getText();
        String result = getresult + postresult;
        Pattern pattern = Pattern.compile("[0-9a-zA-z]+\\." + domain);
        Matcher matcher = pattern.matcher(result);
        ArrayList<String> subdomain = new ArrayList<>();

        while (matcher.find()) {
            subdomain.add(matcher.group(0));
        }

        Set set = new HashSet<>();
        ArrayList<Object> newsubdomain = new ArrayList<>();
        for (Iterator iter = subdomain.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element)) {
                newsubdomain.add(element);
            }
        }
        resultArea.setText(newsubdomain.toString().replace(",","\n").replace("[","").replace("]","").replace(" ",""));
        if (newsubdomain.toString().length() == 2) {
            resultArea.setText("抱歉，没有搜索到" + domain + "的相关子域名数据。");
        }

    }


    class GetSubDomain extends Thread {
        public void ReadByPost() {
            String domain = domainText.getText();
            try {
                URL url = new URL("http://i.links.cn/subdomain/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.addRequestProperty("encoding", "UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);


                bufferedWriter.write("domain=" + domain + "&b2=1&b3=1&b4=1");
                bufferedWriter.flush();


                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);

                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();

                outputStream.close();
                outputStreamWriter.close();
                bufferedWriter.close();

                String postresult = builder.toString();

                ReadByGet(postresult);


            } catch (IOException e){
                resultArea.setText("抱歉，网络出错。");
                return;
            }
        }

        public void ReadByGet(String postresult) {
            String domain = domainText.getText();
            try {
                URL url = new URL("http://www.5118.com/subdomains/" + domain);
                java.net.URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);

                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                String getresult = builder.toString();

                DelDuplicate(getresult, postresult);


            } catch (MalformedURLException e){

                resultArea.setText("抱歉，网络出错。");
            } catch (IOException e){

                resultArea.setText("抱歉，网络出错。");
            }
        }
    }

}


