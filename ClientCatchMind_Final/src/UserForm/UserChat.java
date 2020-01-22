package UserForm;

import java.awt.Font;
import java.util.Date;

import javax.swing.JTextField;
import javax.swing.text.StyledEditorKit.BoldAction;

public class UserChat extends JTextField {
   
   int userNum;
   int clock;
   Runnable taskRunnable;
   Thread taskThread;
   Runnable clockRunnable;
   Thread clockThread;
   
   public UserChat(int userNum) {
      this.userNum = userNum;
      setFont(new Font(getName(), Font.BOLD, 20));
      clockRunnable = new ClockThread();
      clockThread = new Thread(clockRunnable);
      clock = 100;
      clockThread.start();
      taskRunnable = new printChatTread();
      taskThread = new Thread(taskRunnable);
      taskThread.start();
   }
   
   public void printChat(String message) {
      setText(" "+message);
      clock = 0;
   }
   
   class ClockThread implements Runnable {

      @Override
      public void run() {
         // TODO Auto-generated method stub
         try {
            while(true) {
               Thread.sleep(100);
               clock ++;
            }
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      
   }
   
   class printChatTread implements Runnable {

      @Override
      public void run() {
         // TODO Auto-generated method stub
         while(true) {
            if(clock/10 == 0) {
               switch(userNum) {
               case 0:
                  setBounds(270, 85, 5+getText().length()*20, 50);
                  break;
               case 1:
                  setBounds(270, 290, 5+getText().length()*20, 50);
                  break;
               case 2:
                  setBounds(270, 495, 5+getText().length()*20, 50);
                  break;
               case 3:
                  setBounds(1175-(5+getText().length()*20), 85, 5+getText().length()*20, 50);
                  break;
               case 4:
                  setBounds(1175-(5+getText().length()*20), 290, 5+getText().length()*20, 50);
                  break;
               case 5:
                  setBounds(1175-(5+getText().length()*20), 495, 5+getText().length()*20, 50);
                  break;
               }
               setVisible(true);
            }
            else if(clock/10 > 5) {
               setVisible(false);
            }
            try {
               Thread.sleep(100);
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }
   }
}