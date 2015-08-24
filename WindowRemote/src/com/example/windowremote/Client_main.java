package com.example.windowremote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class Client_main extends Activity {

	String ip,pass;
	private ImageView iv;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	
	protected OutputStream out;
	protected Socket s;
	protected boolean state=false;
	private float x;
	private float y;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_client_main);
		ip=getIntent().getExtras().getString("ip");
		pass=getIntent().getExtras().getString("pass");
		iv=(ImageView)findViewById(R.id.imageView1);
		iv.setOnTouchListener(new OnTouchListener() {
			
			

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				x=arg0.getX();
				y=arg0.getY();
				Toast.makeText(getApplicationContext(), x+""+y, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		Thread rea=new Thread(new Runnable() {
					
					

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							s=new Socket(InetAddress.getByName(ip), 4444);
						dis=new DataInputStream(s.getInputStream());
						out=s.getOutputStream();
						dos=new DataOutputStream(out);
						writeString(pass);
						state=true;
						try
						{while(state)
							{
							
							int size=dis.readInt();
							final byte[] buff=new byte[size];
							dis.readFully(buff);
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
								
									Bitmap bp=BitmapFactory.decodeByteArray(buff, 0, buff.length);
									iv.setImageBitmap(bp);
								}
							});
								
							
							
							}
							}
							catch(Exception e)
							{
								state=false;
								 
							}
							
						}
						 catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								state=false;
								s.close();
								dis.close();
								dos.close();
								
								
								
							} catch (IOException e1) {
								state=false;
								
							}
							 catch (Exception e1) 
							 {
								 state=false;
									 
							 }
						}
						
					}
				});
//				recieveUdp(); for udp
				rea.start();
			}
	void writePass()
	{
		try {
			s=new Socket(InetAddress.getByName(ip), 4444);
			dis=new DataInputStream(s.getInputStream());
			out=s.getOutputStream();
			dos=new DataOutputStream(out);
			writeString(pass);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	void recieveUdp()
	{
		Thread rea=new Thread(new Runnable() {
			
			

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					writePass();
					final byte[] message = new byte[20000000];
					DatagramPacket p = new DatagramPacket(message, message.length);
					DatagramSocket s;
					
						s = new DatagramSocket(null);
					
				    s.setReuseAddress(true);
				    s.setBroadcast(true);
				    s.bind(new InetSocketAddress(21111));
				state=true;
				try
				{while(state)
					{
					
					s.receive(p);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
						
							Bitmap bp=BitmapFactory.decodeByteArray(message, 0, message.length);
							iv.setImageBitmap(bp);
						}
					});
						
					
					
					}
					}
					catch(Exception e)
					{
						state=false;
						 
					}
					
				}
				 catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						state=false;
						s.close();
						dis.close();
						dos.close();
						
						
						
					} catch (IOException e1) {
						state=false;
						
					}
					 catch (Exception e1) 
					 {
						 state=false;
							 
					 }
				}
				
			}
		});
		rea.start();
	}
	protected void writeString(String pass2) {
		// TODO Auto-generated method stub
		try {
			dos.writeInt(pass2.length());
			dos.write(pass2.getBytes());
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		state=false;
		finish();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			state=false;
			out.close();
			dis.close();
			dos.close();
			s.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			state=false;
			e.printStackTrace();
		}
		 catch (Exception e) {
				// TODO Auto-generated catch block
			 state=false;
				e.printStackTrace();
				
			}
		
		
	}
	}



