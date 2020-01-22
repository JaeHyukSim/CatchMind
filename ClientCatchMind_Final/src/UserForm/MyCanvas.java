package UserForm;
import java.awt.BasicStroke;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Canvas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;



import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

import javax.swing.ButtonGroup;

import javax.swing.JComboBox;
import java.awt.FlowLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.*;


public class MyCanvas extends Canvas {

	int x = -30;
	int y = -30;
	int ex =0;
	int ey = 0;

    Graphics gg;
	Color cr = Color.BLACK;
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		
		g.setColor(cr);
		g.fillOval(x-20, y-20, 25 ,25);
		
	}

	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		paint(g);
	}
	
	
	
}