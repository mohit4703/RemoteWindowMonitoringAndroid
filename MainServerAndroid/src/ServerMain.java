import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;



public class ServerMain implements ActionListener {
	JFrame fr;
	JTextArea ta;
	JButton start;
	JLabel l1;
	private ByteArrayOutputStream boas;
	private int width;
	private int height;
	static Robot rob;
	private static DataOutputStream dos;
	private static OutputStream out;
	private static InputStream in;
	private static DataInputStream dis;
	public BufferedImage img;
	public int[] rgbs;
	public byte[] buff;
	private static String str;
	
	public ServerMain() {
		fr=new JFrame("Window Remote");
		fr.setLayout(null);
		ta=new JTextArea();
		ta.setBounds(300, 300, 150, 30);
		fr.add(ta);
		start=new JButton("start");
		start.setBounds(340, 360, 70, 30);
		fr.add(start);
		try {
			l1=new JLabel(InetAddress.getLocalHost().toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start.addActionListener(this);
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		l1.setBounds(300,60,350,40);
		fr.add(l1);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setBounds(0, 0, 800, 600);
		fr.setSize(800, 600);
		fr.setVisible(true);
	}

	public static BufferedImage capture()
	{
		
	
		return  rob.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height));
	}
	void start()
	{
		int s;
		try {
				s = dis.readInt();
				byte[] buff=new byte[s];
				dis.readFully(buff);
				if(new String(buff).equals(str))
				{
						new sendLive().start();
			
				}
				else
				{
						System.exit(0);
				}
			} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
		}
	}
	
	BufferedImage toGray(BufferedImage image)
	{
			width = image.getWidth();
			height = image.getHeight();
        
			for(int i=0; i<height; i++)
			{
				for(int j=0; j<width; j++)
				{
					Color c = new Color(image.getRGB(j, i));
					int red = (int)(c.getRed() * 0.299);
					int green = (int)(c.getGreen() * 0.587);
					int blue = (int)(c.getBlue() *0.114);
					Color newColor = new Color(red+green+blue,red+green+blue,red+green+blue);
					image.setRGB(j,i,newColor.getRGB());
				}
			}
        return image;
	}
	byte[] compressImage(BufferedImage img)
	{
			boas=new ByteArrayOutputStream();
			Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
			ImageWriter writer = (ImageWriter) writers.next();
			ImageOutputStream ios = null;
			try 
			{
				ios = ImageIO.createImageOutputStream(boas);
			} 
			catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.setOutput(ios);
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.1f);
			try 
			{
				writer.write(null, new IIOImage(img, null, null), param);
				boas.flush();
				boas.close();
				ios.close();
				writer.dispose();
			} 
			catch (IOException e) 
			{
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] imgone=boas.toByteArray();
			return imgone;
	}
	byte[] getbyte(BufferedImage bf)
	{
		boas=new ByteArrayOutputStream();
		try {
				ImageIO.write(bf, "jpeg", boas);
				boas.flush();
				byte[] img=boas.toByteArray();
				boas.close();
				return img;
			} 
		catch (IOException e) 
		{
		// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return null;
	}
	void serverStart()
	{
		Thread r=new  Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try
				{
					try {
						rob=new Robot();
						} 
					catch (AWTException e) 
					{
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ServerSocket ss=new ServerSocket(4444);
					start.setText("started");
					start.setBounds(300, 360, 180, 30);
					start.setEnabled(false);
					str=ta.getText().toString();
					Socket s=ss.accept();
					System.out.println("connected");
					in=s.getInputStream();
					out=s.getOutputStream();
					dos=new DataOutputStream(out);
					dis=new DataInputStream(in);
					start();
				}
				 catch (UnknownHostException e2) 
				 {
						// TODO Auto-generated catch block
						e2.printStackTrace();
						System.exit(0);
				 } 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(0);
				}
			}
		});
		r.start();
	}
	
	class sendLive extends Thread
	{
		public void run() 
		{
			super.run();
			this.setPriority(MAX_PRIORITY);
			for(;;)
			{
			
				img=capture();
	//			BufferedImage img1=toGray(img);
				buff=compressImage(img);
   //			send(buff); for udp
				try 
				{
					dos.writeInt(buff.length);
					dos.write(buff);
					out.flush();
					System.out.println(buff.length);
				} 
				catch (IOException e) {
				// TODO Auto-generated catch block
					System.out.println("connection closed");
					System.exit(0);
				}
			
			
			}
		}
	}
	public void send(final byte[] buff) {
		new Thread(new Runnable() {
			public void run() {
				try {
					DatagramSocket s = new DatagramSocket();
					InetAddress local = InetAddress.getByName("192.168.53.101");
					DatagramPacket p = new DatagramPacket(buff, buff.length, local, 21111);
					s.send(p);
					s.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}  catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	public static void main(String []z)
	{
		ServerMain sm=new ServerMain();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object oe=e.getSource();
		if(oe==start)
		{
			serverStart();
		}
	}
}
