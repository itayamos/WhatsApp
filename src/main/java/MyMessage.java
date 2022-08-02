import com.google.common.base.MoreObjects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MyMessage {
    private boolean sent;
    private boolean read;
    private String status;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;
    private WebElement myStatus;
    private FileWriter fileWriter;

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    private boolean received;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }




    public MyMessage(){
        this.sent=false;
        this.read=false;
        this.received=false;
        try {
            this.fileWriter=new FileWriter(Constant.REPORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setSent(boolean sent) {
        this.sent = sent;
    }
    public String personalization(String id,String text){
        String message="";
        if(id.contains(":")) {
            message = "שלום, סליחה על ההפרעה, " + id.split(":")[1] + ". " + text+" .";
        }
        else {
            message=text;
        }
        return message;
    }
    public String phone(String phone){
        return phone.split(":")[0];
    }
    public boolean validity(String phoneNum){
        String phone=phone(phoneNum);
        boolean flag=false;
        if ((phone.charAt(0) == '0' && phone.charAt(1) == '5') &&phone.length()==10 ||
                (phone.charAt(0) == '9' && phone.charAt(1) == '7' && phone.charAt(2) == '2'&&phone.length()==11 )) {
            for (int i = 2; i < phone.length(); i++) {
                if(Character.isDigit(phone.charAt(i))){
                    flag=true;
                }
                else {
                    flag=false;
                    break;
                }
            }
        }
        return flag;
    }
    public ChromeDriver sendTo(String phoneNum, String text, ChromeDriver driver){        driver.get(Constant.WEBSITE);
        new Thread(()-> {
            if (validity(phone(phoneNum)) && !text.equals(Constant.EMPTY)) {
                driver.get(Constant.linkToSend + "" + phone(phoneNum.substring(1)));
                List<WebElement> textInput;
                List<WebElement> sendBTN;
                boolean flag=false;
                while(!flag) {                                      /*Constant.WA_TEXTBOX*/
                    textInput = driver.findElements(By.cssSelector(Constant.WA_TEXTBOX));
                    if (textInput.size() > 0) {
                        textInput.get(0).sendKeys(personalization(phoneNum,text));
                    }
                    sendBTN = driver.findElements(By.cssSelector(Constant.WA_SENDBUTTON));
                    if (sendBTN.size() > 0) {
                        sendBTN.get(0).click();
                        flag=true;
                        this.sent=true;
                        break;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return driver;
    }
    public void connect(String phone, String text){
        try {
        System.setProperty(Constant.PROP_KEY,Constant.FILE_PATH);
        ChromeOptions chromeOptions=new ChromeOptions();

        chromeOptions.addArguments(Constant.CHROME_ARGUMENT);
        //C:\Users\Lenovo\Downloads\1.exe\chromedriver_win32
        ChromeDriver driver = new ChromeDriver(chromeOptions);
        ChromeDriver cd=sendTo(phone,text,driver);
        getLastMessage(cd);
        messageStatus();
        comment(cd);
        //sendToMany(phone,text,driver);
        }catch (Exception e){
            connect(phone,text);
        }

    }
    public void WriteToFile(String text){
        try {
            this.fileWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public WebElement getLastMessage(ChromeDriver driver) {
        try {
            while (!this.sent) {
                Thread.sleep(Constant.WAIT1);
            }
            WebElement chat = driver.findElement(By.className(Constant.WA_CHAT));
            WebElement chatBody = chat.findElement(By.cssSelector(Constant.WA_CHAT_MESSAGES));
            List<WebElement> allMessage = chatBody.findElements(By.cssSelector(Constant.WA_MESSAGES));
            this.myStatus = allMessage.get(allMessage.size() - 1);

        } catch (Exception e) {
            getLastMessage(driver);
        }
        return this.myStatus;
    }
    public void messageStatus() {
        new Thread(() -> {
            WebElement messageStatus = null;
            try {
                String status;
                while (!this.sent) {

                    Thread.sleep(Constant.WAIT1);
                }
                do {
                    messageStatus = this.myStatus.findElement(By.cssSelector(Constant.MSG_STATUS));
                    status = messageStatus.getAttribute(Constant.ARIA_LABEL);

                    if (status.contains(Constant.DELIVERED)) {
                        this.status = Constant.DELIVERED;
                        this.received=true;
                    }
                } while (!status.equals(Constant.READ));
                this.read=true;
            } catch (Exception e) {
                messageStatus();
            }
        }).start();

    }
    public String comment(ChromeDriver driver){
        new Thread(()->{
            while (!this.sent){
                try {
                    Thread.sleep(Constant.WAIT1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (true){
                try {
                    Thread.sleep(Constant.WAIT1);
                    this.myStatus=getLastMessage(driver);
                    String messageClass=this.myStatus.getAttribute(Constant.COM_ATTR);
                    if (messageClass.contains(Constant.COM_NAME)){
                        WebElement comment=this.myStatus.findElement(By.cssSelector(Constant.COM_PATH));
                        this.comment=comment.getText();
                        break;
                    }
                } catch (Exception e) {
                    this.comment=comment(driver);
                }
            }
            setReceived(true);
        }).start();
        return this.comment;
    }
}
