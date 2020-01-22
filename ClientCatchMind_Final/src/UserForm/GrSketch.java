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
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.simple.parser.JSONParser;

import Client.DisplayThread;
import Client.UserInputThread;
import Client.UserMessageProcessor;

public class GrSketch extends JPanel implements UserForm, ChangeListener {

	private volatile static GrSketch uniqueInstance;
	// 4. 필수 변수들을 선언해 줍니다!!!!!
	private UserMessageProcessor userMessageProcessor;
	private DisplayThread displayThread;
	private Socket socket;
	private UserInputThread unt;
	private JSONParser jsonParser;

	private Runnable userRunnable;
	private Thread userThread; // ... 데이터 전송을 위한 thread를 선언해 줍니다!!!

	private int brushSize;
	private Color savedColor;
	/* graphic algorithm */
	int[][] slidingWindow;
	int slidingWindowPointer;

	JSlider slider;

	JPanel gui_panel, paint_panel;

	MyCanvas can;
	JButton pencil_bt, eraser_bt, colorRed, colorBlue, colorGray, colorGreen, colorYellow, colorPink, colorViolet,
			colorOrange, colorskyblue;

	JButton allClear;
	JButton colorSelect_bt;

	JLabel thicknessInfo_label;
	JTextField thicknessControl_tf;
	JSlider thicknessControl_slider;
	Color selectedColor;

	// Graphics2D Ŭ������ ����� ���� ����
	Graphics graphics;

	// Graphics2D�� ���� Graphics�� ��������
	Graphics2D g;
	int thickness = 10;

	int startX; // Ŭ�����۽ÿ� X��ǥ���� ����� ����
	int startY; // Ŭ�����۽ÿ� Y��ǥ���� ����� ����
	int endX; // Ŭ������ÿ� X��ǥ���� ����� ����
	int endY; // Ŭ������ÿ� Y��ǥ���� ����� ����

	boolean tf = false;

	private String color;

	private GrSketch(DisplayThread dt, Socket socket) {
		brushSize = 10;
		savedColor = Color.black;
		color = "black";
		this.displayThread = dt;
		this.socket = socket;
		userMessageProcessor = new UserMessageProcessor();
		unt = UserInputThread.getInstance(socket);
		jsonParser = new JSONParser();

		userRunnable = unt;

		slidingWindow = new int[2][2];
		initSlidingWindow();

		setLayout(null);
		gui_panel = new JPanel();

		// ���ȭ���� ���
		gui_panel.setBackground(Color.WHITE);

		// ��ġ������ ���� �õ��Ҽ� �ִ�.
		gui_panel.setLayout(null);

		pencil_bt = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\black11.jpg"))));

		pencil_bt.setBorderPainted(false);
		pencil_bt.setFocusPainted(false);
		pencil_bt.setContentAreaFilled(false);

		colorRed = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\red11.jpg"))));

		colorRed.setBorderPainted(false);
		colorRed.setFocusPainted(false);
		colorRed.setContentAreaFilled(false);

		colorBlue = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\blue11.jpg"))));

		colorBlue.setBorderPainted(false);
		colorBlue.setFocusPainted(false);
		colorBlue.setContentAreaFilled(false);

		colorGreen = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\green11.jpg"))));

		colorGreen.setBorderPainted(false);
		colorGreen.setFocusPainted(false);
		colorGreen.setContentAreaFilled(false);

		colorYellow = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\yellow11.jpg"))));

		colorYellow.setBorderPainted(false);
		colorYellow.setFocusPainted(false);
		colorYellow.setContentAreaFilled(false);

		colorPink = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\pink11.jpg"))));

		colorPink.setBorderPainted(false);
		colorPink.setFocusPainted(false);
		colorPink.setContentAreaFilled(false);

		colorViolet = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\violet11.jpg"))));

		colorViolet.setBorderPainted(false);
		colorViolet.setFocusPainted(false);
		colorViolet.setContentAreaFilled(false);

		colorOrange = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\orange11.jpg"))));

		colorOrange.setBorderPainted(false);
		colorOrange.setFocusPainted(false);
		colorOrange.setContentAreaFilled(false);

		colorGray = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\gray11.jpg"))));

		colorGray.setBorderPainted(false);
		colorGray.setFocusPainted(false);
		colorGray.setContentAreaFilled(false);

		colorskyblue = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\skyblue11.jpg"))));

		colorskyblue.setBorderPainted(false);
		colorskyblue.setFocusPainted(false);
		colorskyblue.setContentAreaFilled(false);

		eraser_bt = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\eraser1.jpg"))));

		eraser_bt.setBorderPainted(false);
		eraser_bt.setFocusPainted(false);
		eraser_bt.setContentAreaFilled(false);

		// allClear = new JButton("ALL");
		// allClear.setFont(new Font("���ʷյ���",Font.BOLD, 17));

		// colorSelect_bt = new JButton("Col");

		// colorSelect_bt.setFont(new Font("���ʷյ���",Font.BOLD, 17));

		allClear = new JButton(new ImageIcon((getClass().getResource("..\\Resource\\color\\allclear.jpg"))));

		allClear.setBorderPainted(false);
		allClear.setFocusPainted(false);
		allClear.setContentAreaFilled(false);

		thicknessInfo_label = new JLabel("");

		thicknessInfo_label.setFont(new Font("���ʷյ���", Font.BOLD, 15));

		// thicknessControl_tf = new JTextField("15", 10);

		// thicknessControl_tf.setFont(new Font("���ʷյ���",Font.BOLD, 20));

		slider = new JSlider(0, 50);

		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		pencil_bt.setBounds(25, 15, 80, 50);
		colorRed.setBounds(100, 15, 80, 50);
		colorBlue.setBounds(175, 15, 80, 50);
		colorGreen.setBounds(250, 15, 80, 50);
		colorYellow.setBounds(325, 15, 80, 50);

		allClear.setBounds(540, 15, 95, 95);

		colorPink.setBounds(25, 78, 80, 50);
		colorViolet.setBounds(100, 78, 80, 50);
		colorOrange.setBounds(175, 78, 80, 50);
		colorGray.setBounds(250, 78, 80, 50);
		colorskyblue.setBounds(325, 78, 80, 50);

		// ���찳 ��ġ����
		eraser_bt.setBounds(420, 15, 95, 95);

		slider.setBounds(660, 25, 180, 70);

		// ���� �� ��ġ ����
		// colorSelect_bt.setBounds(530, 0, 70, 55);

		// �������� �� ��ġ ����
		// thicknessInfo_label.setBounds(630, 0, 80, 55);

		// ���� ���� ����Ʈ �ʵ� ��ġ ����
		// thicknessControl_tf.setBounds(670, 0, 70, 55);

		gui_panel.add(pencil_bt);
		gui_panel.add(colorRed);
		gui_panel.add(colorBlue);
		gui_panel.add(colorGreen);
		gui_panel.add(colorYellow);
		gui_panel.add(allClear);
		gui_panel.add(colorPink);
		gui_panel.add(colorViolet);
		gui_panel.add(colorOrange);
		gui_panel.add(colorskyblue);
		gui_panel.add(colorGray);

		gui_panel.add(eraser_bt); // gui_panel�� ���찳 ��ư �߰�

		// gui_panel.add(colorSelect_bt); //gui_panel�� ���� ���� �߰�

		// gui_panel.add(thicknessInfo_label); //gui_panel�� ���� ���� �� �߰�

		// gui_panel.add(thicknessControl_tf); // gui_panel�� �������� �ؽ�Ʈ�ʵ� �߰�

		gui_panel.add(slider);
		gui_panel.setBounds(0, 450, 880, 150); // gui_panel�� ������ ���� ��ġ�� ��ġ ����

		paint_panel = new JPanel();

		paint_panel.setBackground(Color.YELLOW); // �г��� �Ͼ� ����

		paint_panel.setLayout(null); // paint_panel�� ���̾ƿ��� null���ְ� �г� ��ü�� setBounds�� ��ġ�� ������ �� �ִ�.

		paint_panel.setBounds(0, 0, 880, 460); // paint_panel �� ��ġ����

		add(gui_panel);

		add(paint_panel);

		can = new MyCanvas();

		can.setSize(880, 460); // ��ȭ�� ũ��
		can.setBackground(Color.white);
		paint_panel.add(can);

		MyHandler my = new MyHandler();
		can.addMouseMotionListener(my);
		can.addMouseListener(my);

		pencil_bt.addActionListener(my);

		colorRed.addActionListener(my);
		colorBlue.addActionListener(my);
		colorGreen.addActionListener(my);
		colorYellow.addActionListener(my);
		allClear.addActionListener(my);

		colorPink.addActionListener(my);
		colorViolet.addActionListener(my);
		colorOrange.addActionListener(my);
		colorskyblue.addActionListener(my);
		colorGray.addActionListener(my);

	//	colorSelect_bt.addActionListener(my);
		// ���찳 ��ư ó��

		// eraser_bt.addActionListener(new ToolActionListener());
		eraser_bt.addActionListener(my);

		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				brushSize = slider.getValue();

			}
		});

	}

	// 9. Singleton pattern의 유일한 instance를 만들기 위해 getInstance()메소드를 만듭니다.
	public static GrSketch getInstance(DisplayThread dt, Socket socket) {
		if (uniqueInstance == null) {
			synchronized (GrSketch.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new GrSketch(dt, socket);
				}
			}
		}
		return uniqueInstance;
	}

	public void setColor(String color) {
		System.out.println("color : " + color);
		switch (color) {
		case "black":
			savedColor = Color.black;
			break;
		case "red":
			savedColor = Color.red;
			break;
		case "blue":
			savedColor = Color.blue;
			break;
		case "green":
			savedColor = Color.green;
			break;
		case "yellow":
			savedColor = Color.yellow;
			break;
		case "pink":
			savedColor = Color.pink;
			break;
		case "magenta":
			savedColor = Color.magenta;
			break;
		case "orange":
			savedColor = Color.orange;
			break;
		case "skyblue":
			savedColor = Color.CYAN;
			break;
		case "gray":
			savedColor = Color.GRAY;
			break;
		case "white":
			savedColor = Color.white;
			break;
		case "whiteAll":
			can.cr = Color.white;
			Graphics g = can.getGraphics();
			g.clearRect(0, 0, can.getWidth(), can.getHeight());
			can.cr = Color.black;
			break;
		}
	}

	public void printData(int x, int y) {
		/*
		 * startX = x; startY = y; can.x=startX; can.y=startY;
		 */
		// can.x = x; can.y = y;
		Graphics g = can.getGraphics();
		g.setColor(savedColor);
		g.fillOval(x, y, brushSize, brushSize);

		// can.repaint(); // it is thread
		/*
		 * thickness = Integer.parseInt(thicknessControl_tf.getText()); endX = x; endY =
		 * y; startX = endX; startY = endY;
		 */
	}

	public void draw(int x, int y) {
		System.out.println("(" + x + "," + y + ")");
		int other = (slidingWindowPointer + 1) % 2;
		slidingWindow[slidingWindowPointer][0] = x;
		slidingWindow[slidingWindowPointer][1] = y;

		// 2. use graphic algorithm
		if (slidingWindow[other][0] == 100000) {
			// if it is init state --------
			// System.out.println("point 0");
			// System.out.println("draw : ( " + x + "," + y + ")");
			printData(x, y);
		} else {

			// first : slidingWindow - [not pointer] second : slidingWindow - [pointer]
			int xSize = Math.abs(slidingWindow[slidingWindowPointer][0] - slidingWindow[other][0]);
			int ySize = Math.abs(slidingWindow[slidingWindowPointer][1] - slidingWindow[other][1]);
			// System.out.println("xSize : " + xSize);
			// System.out.println("ySize : " + ySize);
			int xMark = 1;
			int yMark = 1;
			if (slidingWindow[slidingWindowPointer][0] - slidingWindow[other][0] < 0) {
				xMark = -1;
			}
			if (slidingWindow[slidingWindowPointer][1] - slidingWindow[other][1] < 0) {
				yMark = -1;
			}
			if (xSize <= 1) {
				// System.out.println("point : 1");
				for (int i = 0; i <= ySize / 2; i++) {
					// System.out.println("draw : (" + slidingWindow[other][0] + "," +
					// (slidingWindow[other][1] + i * yMark) + ")");

					printData(slidingWindow[other][0], slidingWindow[other][1] + i * yMark);
				}
				for (int i = ySize / 2; i <= ySize; i++) {
					// System.out.println("draw : (" + slidingWindow[slidingWindowPointer][0] + ","
					// + (slidingWindow[other][1] + i * yMark) + ")");

					printData(slidingWindow[slidingWindowPointer][0], slidingWindow[other][1] + i * yMark);
				}
			} else {
				// determine bagic x, y
				// System.out.println("point 2");
				double d = (slidingWindow[slidingWindowPointer][1] - slidingWindow[other][1]) / (double) (xSize);
				double ad = Math.abs(d);
				int fx, fy;
				for (int i = 0; i < xSize; i++) {
					fx = slidingWindow[other][0] + i * xMark;
					fy = (int) (slidingWindow[other][1] + i * d);
					for (int j = 0; j <= (int) (ad / 2); j++) {
						// System.out.println("draw : (" + fx + "," + (fy + + j * yMark) + ")");

						printData(fx, fy + j * yMark);
					}
					for (int j = (int) (ad / 2); j <= (int) ad; j++) {
						// System.out.println("draw : (" + fx + "," + (fy + j * yMark) + ")");

						printData(fx + 1, fy + j * yMark);
					}
				}
			}
			/////////////////////////////////
		}
		slidingWindowPointer = (slidingWindowPointer + 1) % 2;
	}

	class MyHandler implements MouseMotionListener, ActionListener, MouseListener {

		@Override
		public void mouseDragged(MouseEvent e) {

			// 서버로 보내봅시다.
			// 1. 메소드를 정해봅시다. 3700 -> 3702
			String sendData = "{";
			sendData += userMessageProcessor.getJSONData("method", "3700");
			sendData += "," + userMessageProcessor.getJSONData("x", String.valueOf(e.getX()));
			sendData += "," + userMessageProcessor.getJSONData("y", String.valueOf(e.getY()));
			sendData += "," + userMessageProcessor.getJSONData("color", String.valueOf(color));
			sendData += "," + userMessageProcessor.getJSONData("brushSize", String.valueOf(brushSize));
			sendData += "}";

			// 13. 데이터를 서버로 보냅니다!
			unt.setInputData(sendData);
			unt.pushMessage();
			/*
			 * startX = e.getX(); //���콺 �������� X��ǥ������ �ʱ�ȭ startY = e.getY(); //���콺
			 * �������� Y��ǥ������ �ʱ�ȭ can.x=startX; can.y=startY;
			 * 
			 * can.repaint(); thickness = Integer.parseInt(thicknessControl_tf.getText());
			 * 
			 * //�ؽ�Ʈ �ʵ� �κп��� ���� ������ thickness������ ����
			 * 
			 * 
			 * 
			 * endX = e.getX(); //�巡�� �Ǵ� �������� x��ǥ ����
			 * 
			 * 
			 * endY = e.getY(); //�巡�� �Ǵ� �������� y��ǥ ����
			 * 
			 * 
			 * startX = endX; // ���ۺκ��� �������� �巹�׵� X��ǥ�� ������ ������ �̾� �׷��� ��
			 * �ִ�.
			 * 
			 * startY = endY; // ���ۺκ��� �������� �巹�׵� Y��ǥ�� ������ ������ �̾� �׷��� ��
			 * �ִ�.
			 */
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			Object o = e.getSource();
			MyCanvas can2 = (MyCanvas) can;
			//////////////////////////////////////////////////////////////////

			if (o == pencil_bt) {

				// if(tf == false) g.setColor(Color.BLACK);
				// else g.setColor(selectedColor);
				// g.setColor(Color.BLACK);
				color = "black";
				can2.cr = Color.BLACK;

			} else if (o == colorRed) {

				// if(tf == false) g.setColor(Color.RED);
				// else g.setColor(selectedColor);
				// g.setColor(Color.RED);
				color = "red";
				can2.cr = Color.red;

			} else if (o == colorBlue) {

				// if(tf == false) g.setColor(Color.BLUE);
				// else g.setColor(selectedColor);
				// g.setColor(Color.BLUE);
				color = "blue";
				can2.cr = Color.blue;

			} else if (o == colorGreen) {

				// if(tf == false) g.setColor(Color.GREEN);
				// else g.setColor(selectedColor);
				// g.setColor(Color.GREEN);

				color = "green";
				can2.cr = Color.GREEN;

			} else if (o == colorYellow) {

				// if(tf == false) g.setColor(Color.YELLOW);
				// else g.setColor(selectedColor);
				// g.setColor(Color.YELLOW);
				color = "yellow";
				can2.cr = Color.yellow;

			} else if (o == colorPink) {

				// if(tf == false) g.setColor(Color.PINK);
				// else g.setColor(selectedColor);
				// g.setColor(Color.PINK);
				color = "pink";
				can2.cr = Color.PINK;

			} else if (o == colorViolet) {

				// if(tf == false) g.setColor(Color.MAGENTA);
				// else g.setColor(selectedColor);
				// g.setColor(Color.MAGENTA);
				color = "magenta";
				can2.cr = Color.MAGENTA;

			} else if (o == colorOrange) {

				// if(tf == false) g.setColor(Color.ORANGE);
				// else g.setColor(selectedColor);

				// g.setColor(new Color(255,130,51));
				color = "orange";
				can2.cr = Color.ORANGE;

			} else if (o == colorskyblue) {

				// if(tf == false) g.setColor(Color.getHSBColor(153, 102, 51));
				// else g.setColor(selectedColor);
				// g.setColor(new Color(194,113,81));
				color = "black";
				can2.cr = Color.black;

			} else if (o == colorGray) {

				// if(tf == false) g.setColor(Color.getHSBColor(153, 102, 51));
				// else g.setColor(selectedColor);
				// g.setColor(new Color(194,113,81));

				color = "gray";

				can2.cr = new Color(125, 63, 0);//

			} else if (o == colorskyblue) {

				// if(tf == false) g.setColor(Color.getHSBColor(153, 102, 51));
				// else g.setColor(selectedColor);
				// g.setColor(new Color(194,113,81));

				color = "skyblue";

				can2.cr = new Color(153, 217, 234);//

			} else if (o == allClear) {
				color = "whiteAll";
				// 서버로 보내봅시다.
				// 1. 메소드를 정해봅시다. 3700 -> 3702
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "3710");
				sendData += "," + userMessageProcessor.getJSONData("color", String.valueOf(color));
				sendData += "}";
				color = "black";
				can2.cr = Color.black;
				// 13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();

			} else if (o == eraser_bt) {
				color = "white";
				can2.cr = Color.WHITE;

			} else if (o == colorSelect_bt) {

				color = "selectColor";
				Color selCr = JColorChooser.showDialog(null, "Color", Color.ORANGE);
				can2.cr = selCr;
			}

		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			System.out.println("here!!!!!!!!!!!!!!!!!!!!");
			// 서버로 보내봅시다.
			// 1. 메소드를 정해봅시다. 3700 -> 3702
			String sendData = "{";
			sendData += userMessageProcessor.getJSONData("method", "3720");
			sendData += "}";

			// 13. 데이터를 서버로 보냅니다!
			unt.setInputData(sendData);
			unt.pushMessage();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void display() {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformMethod() {
		// TODO Auto-generated method stub

	}

	@Override
	public void operation(String data) {
		// TODO Auto-generated method stub

	}

	@Override
	public JPanel getJPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initSlidingWindow() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				slidingWindow[i][j] = 100000;
			}
		}
		slidingWindowPointer = 0;
	}

	public int getBrushSize() {
		return brushSize;
	}

	public void setBrushSize(int brushSize) {
		this.brushSize = brushSize;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}

}
