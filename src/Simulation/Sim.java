package Simulation;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Sim extends Canvas implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener{
	
	private static final long serialVersionUID = 1L;
	public static int WIDTH = 1200;
	public static int HEIGHT = 700;
	double SCALE = 4;
	int prevX = -10000, prevY = -10000;
	double cameraX = 0, cameraY = 0, pMouseX = 0, pMouseY = 0, pCameraX = 0, pCameraY = 0, mouseX = 0, mouseY = 0;
	boolean plot_num = false;
	double[] nums = new double[2];
	
	public BufferedImage layer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	Color gray = new Color(100, 100, 100);
	
	public Sim() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	public static void main(String[] args) {
		Sim game = new Sim();
		JFrame frame = new JFrame("Graphic");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(game);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		new Thread(game).start();
	}
	
	public double function(double x) {
		double y;
		y = x*x*x + 3*x*x - 2*x;
		return y;
	}
	
	public double module(double x) {
		if(x < 0) {
			return -x;
		}return x;
	}
	
	public boolean onScreen(double y){
		return(y > 0 && y < HEIGHT);
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = layer.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		double step = 5;
		if(SCALE > 25) {
			step = 0.1;
		} else if(SCALE > 10) {
			step = 0.5;
		} else if(SCALE > 3){
			step = 1;
		} else if(SCALE < 0.15) {
			step = 50;
		} else if(SCALE< 0.3){
			step = 20;
		} else  if(SCALE < 0.5) {
			step = 10;
		}
		
		for(double i = step*Math.round(((-35/SCALE)-(cameraX/20))/step); i <= (35/SCALE)-(cameraX/20); i += step) {
			int X_pos = (int)(i*20*SCALE + cameraX*SCALE + WIDTH/2);
			g.setColor(Color.WHITE);
			if(step % 1 != 0) {
				String num = String.format("%.1f", i);
				g.drawString(""+num, X_pos, HEIGHT/2+((int)(cameraY*SCALE)));
			} else {
				g.drawString(""+Math.round(i), X_pos, HEIGHT/2+((int)(cameraY*SCALE)));
			}
			if(i != 0) {
				g.setColor(gray);
				g.drawLine(X_pos, 0, X_pos, HEIGHT);
			}
			
		}
		
		for(double i = step*Math.round(((-35/SCALE)-(cameraY/20))/step); i <= (35/SCALE)-(cameraY/20); i += step) {
			int Y_pos = (int)(i*20*SCALE + cameraY*SCALE + HEIGHT/2);
			g.setColor(Color.WHITE);
			if(step % 1 != 0 && i != 0) {
				String num = String.format("%.1f", -i);
				g.drawString(""+num, WIDTH/2+((int)(cameraX*SCALE)), Y_pos);
			} else if(i != 0) {
				g.drawString(""+Math.round(-i), WIDTH/2+((int)(cameraX*SCALE)), Y_pos);
			}
			if(i != 0) {
				g.setColor(gray);
				g.drawLine(0, Y_pos, WIDTH, Y_pos);
			}
		}
		
		g.setColor(Color.WHITE);
		g.fillRect(0, HEIGHT/2+((int)(cameraY*SCALE)), Sim.WIDTH, 1);
		g.fillRect(WIDTH/2+((int)(cameraX*SCALE)), 0, 1, Sim.HEIGHT);
		
		for(double i =  (-35/SCALE)-(cameraX/20); i <= (35/SCALE)-(cameraX/20); i += 0.01) {
			int x = WIDTH/2+(int)(i*20*SCALE);
			int y = HEIGHT/2-(int)(function(i)*20*SCALE);
			if(i > (-35/SCALE)-(cameraX/20) && (onScreen(prevY+cameraY*SCALE) || onScreen(y+cameraY*SCALE))) {
				g.drawLine((int)(prevX+cameraX*SCALE), (int)(prevY+cameraY*SCALE), (int)(x+cameraX*SCALE), (int)(y+cameraY*SCALE));
			}
			prevX = x;
			prevY = y;
		}
		
		if(plot_num) {
			g.fillRect((int)(nums[0]*20*SCALE + cameraX*SCALE + WIDTH/2), (int)(nums[1]*20*SCALE + cameraY*SCALE + HEIGHT/2), 90, 65);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Serif", Font.PLAIN, 20));
			String num1 = String.format("%.2f", nums[0]);
			g.drawString("x = " + num1, (int)(nums[0]*20*SCALE + cameraX*SCALE + WIDTH/2)+10, (int)(nums[1]*20*SCALE + cameraY*SCALE + HEIGHT/2) + 25);
			String num2 = String.format("%.2f", -nums[1]);
			g.drawString("y = " + num2, (int)(nums[0]*20*SCALE + cameraX*SCALE + WIDTH/2)+10, (int)(nums[1]*20*SCALE + cameraY*SCALE + HEIGHT/2) + 50);
		}
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(layer, 0, 0, WIDTH, HEIGHT, null);
		bs.show();
	}
	
	@Override
	public void run() {
		requestFocus();
		while(true) {
			render();
			try {
				Thread.sleep(1000/60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		cameraX = (mouseX - pMouseX)/SCALE + pCameraX;
		cameraY = (mouseY - pMouseY)/SCALE + pCameraY;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == 3) {
			plot_num = false;
		}else if(Math.pow(function((e.getX()/(20*SCALE))-cameraX/20-30/SCALE)-(-(e.getY()/(20*SCALE))+cameraY/20+17/SCALE), 2) < 0.2/SCALE && e.getButton() == 1) {
			plot_num = true;
			nums[0] = (e.getX()/(20*SCALE))-cameraX/20-30/SCALE;
			nums[1] = -function(nums[0]);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		pMouseX = e.getX();
		pMouseY = e.getY();
		pCameraX = cameraX;
		pCameraY = cameraY;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//contX = mouseX;
		//contY = mouseY;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() < 0 && SCALE < 30) {
			SCALE = SCALE*1.1;
		} else if(e.getWheelRotation() > 0 && SCALE > 0.1) {
			SCALE = SCALE/1.1;
		}
	}
}
