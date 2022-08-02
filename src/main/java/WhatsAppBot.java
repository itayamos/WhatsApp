import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//phone:name
// levels: 1-8 & 11
public class WhatsAppBot extends JFrame implements ActionListener {
    private String propertyKey;
    private String filePath;
    private String chromeArguments;
    private String website;
    private JLabel JLabel1;
    private JLabel instruction1;
    private JTextField phoneTF;
    private JLabel instruction2;
    private JTextField textTF;
    private JLabel message;
    private JButton send;
    private JLabel note1;
    private JLabel talkBack;
    private JButton createFile;
    private JButton cancel;
    private JButton proceed;

    public WhatsAppBot(String propertyKey, String filePath,String chromeArguments,String website) {
        this.propertyKey = propertyKey;
        this.filePath = filePath;
        this.chromeArguments = chromeArguments;
        this.website = website;
        this.setSize(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        JLabel1 =new JLabel("hey, this is WhatsAppBot:");
        JLabel1.setBounds(Constant.X_Y,Constant.X_Y,Constant.TF_JL_WIDTH,Constant.TF_JL_HEIGHT);
        this.add(JLabel1);
        instruction1=new JLabel();
        instruction1.setBounds(Constant.X_Y,Constant.Y,Constant.TF_JL_WIDTH*5,Constant.TF_JL_HEIGHT);
        instruction1.setText(Constant.INSTRUCTION1);
        this.add(instruction1);
        phoneTF =new JTextField();
        phoneTF.setBounds(Constant.X_Y,Constant.Y*2,Constant.TF_JL_WIDTH,Constant.TF_JL_HEIGHT);
        this.add(phoneTF);
        instruction2=new JLabel();
        instruction2.setBounds(Constant.X_Y,Constant.Y*3,Constant.TF_JL_WIDTH,Constant.TF_JL_HEIGHT);
        instruction2.setText(Constant.INSTRUCTION2);
        this.add(instruction2);
        textTF=new JTextField();
        textTF.setBounds(Constant.X_Y,Constant.Y*4,Constant.TF_JL_WIDTH,Constant.TF_JL_HEIGHT);
        this.add(textTF);
        message=new JLabel("message");
        message.setBounds(Constant.X_Y,Constant.Y*5,Constant.TF_JL_WIDTH,Constant.TF_JL_HEIGHT);
        this.add(message);
        send = new JButton();
        send.setBounds(Constant.X_Y+Constant.BTN_WIDTH*2,Constant.Y*6, Constant.BTN_WIDTH, Constant.BTN_H);
        send.setText("send");
        send.addActionListener(this);
        this.add(send);
        note1=new JLabel();
        note1.setBounds(Constant.X_Y,Constant.Y*9,Constant.TF_JL_WIDTH,Constant.TF_JL_HEIGHT);
        note1.setText("update");
        this.add(note1);
        talkBack=new JLabel();
        talkBack.setBounds(Constant.X_Y+Constant.MARGIN,Constant.Y*9,Constant.TF_JL_WIDTH,Constant.TF_JL_HEIGHT);
        talkBack.setText("comment");
        this.add(talkBack);
        createFile=new JButton("create file");
        createFile.setBounds(Constant.X_Y+Constant.BTN_WIDTH*3/2,Constant.Y*8, Constant.BTN_WIDTH*2, Constant.BTN_H);
        createFile.addActionListener(this);
        this.add(createFile);
        cancel=new JButton("cancel");
        cancel.setBounds(Constant.X_Y,Constant.Y*7,Constant.BTN_WIDTH*2,Constant.BTN_H);
        cancel.addActionListener(this);
        this.add(cancel);
        proceed=new JButton("proceed");
        proceed.setBounds(Constant.X_Y+Constant.BTN_WIDTH*3,Constant.Y*7,Constant.BTN_WIDTH*2,Constant.BTN_H);
        proceed.addActionListener(this);
        this.add(proceed);
        proceed.setVisible(false);
        cancel.setVisible(false);
        createFile.setVisible(false);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MyMessage m=new MyMessage();
        if (e.getSource()==cancel){
            phoneTF.setEditable(true);
            textTF.setEditable(true);
            cancel.setVisible(false);
            proceed.setVisible(false);
            send.setEnabled(true);
        }
        if (e.getSource()==proceed){
            if(m.validity(m.phone(phoneTF.getText()))){
                if (!textTF.getText().equals(Constant.EMPTY)){
                    m.connect(phoneTF.getText(),textTF.getText());
                    whatMyStatus(m);
                    putInLabel(m);
                }
                else if(textTF.getText().equals(Constant.EMPTY)){
                    message.setText(Constant.NOTEXT);
                }

            }
            else if(!m.validity(phoneTF.getText())){
                message.setText(Constant.invalidNumber);
            }
            proceed.setVisible(false);
            cancel.setVisible(false);
            cancel.setEnabled(false);
            createFile.setVisible(true);
        }
        if (e.getSource()==send){
            phoneTF.setEditable(false);
            textTF.setEditable(false);
            proceed.setVisible(true);
            cancel.setVisible(true);
           send.setEnabled(false);
        }
        if (e.getSource()==createFile){
            m.WriteToFile("phone number: "+phoneTF.getText()+" ."+'\n'+
                    "valid: "+m.validity(phoneTF.getText().split(":")[0])+" ."+'\n'+
                    "textTF: "+m.phone(textTF.getText())+" ."+'\n'+
                    "status: "+m.getStatus()+"."+'\n'+
                    "comment"+m.getComment()+" .");
        }
    }

   public void whatMyStatus(MyMessage m){
        new Thread(()->{
            while (true){
                try {
                    Thread.sleep(Constant.WAIT3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if((m.isSent())&&(!m.isRead())&&(!m.isReceived())) {
                    note1.setText(Constant.SENT);
                }
                else if ((m.isSent())&&(!m.isRead())&&(m.isReceived())){
                    note1.setText(Constant.DELIVERED);

                }
                else if ((m.isSent())&&(m.isRead())&&(m.isReceived())){
                    note1.setText(Constant.READ);
                }
            }
        }).start();
   }
   public void putInLabel(MyMessage m){

        this.add(talkBack);
        new Thread(()->{
            while (true){
                if(m.getComment()!=null){
                    if(!m.getComment().equals(Constant.EMPTY)) {
                        talkBack.setText(m.getComment());
                    }
                }
            }
        }).start();
   }



    public static void main(String[] args) {
        WhatsAppBot skiller=new WhatsAppBot(Constant.PROP_KEY,Constant.FILE_PATH,Constant.CHROME_ARGUMENT,Constant.WEBSITE);
    }
}
