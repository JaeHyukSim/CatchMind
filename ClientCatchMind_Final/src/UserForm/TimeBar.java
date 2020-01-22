package UserForm;

import java.awt.Color;

import javax.swing.JProgressBar;

public class TimeBar extends JProgressBar{
   
   int timelimit;
   
   public TimeBar(int timelimit) {
      this.timelimit = timelimit;
      setMinimum(0);
      setMaximum(timelimit);
      setBounds(5, 35, 340, 30);
      setBackground(Color.gray);
      setForeground(Color.green);
      setValue(timelimit);
   }
/*
   @Override
   public void run() {
      // TODO Auto-generated method stub
      setForeground(Color.green);
      while(getValue()>0) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         setValue(getValue()-1);
         if(getValue()==30) setForeground(Color.red);
      }
   }
   */
}