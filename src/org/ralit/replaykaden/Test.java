package org.ralit.replaykaden;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Test extends Thread {

	public Test() {
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DefaultHttpClient client = new DefaultHttpClient();
		ArrayList<HttpGet> list = new ArrayList<HttpGet>();
		HttpGet get = new HttpGet("http://192.168.1.142:31413/call.json?method=list");
		list.add(get);
		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting1,[0x80,[0x30]]]");
		list.add(get);
		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting2,[0x80,[0x30]]]");
		list.add(get);
		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting3,[0x80,[0x30]]]");
		list.add(get);
		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting4,[0x80,[0x30]]]");
		list.add(get);

		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting1,[0x80,[0x31]]]");
		list.add(get);
		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting2,[0x80,[0x31]]]");
		list.add(get);
		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting3,[0x80,[0x31]]]");
		list.add(get);
		get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting4,[0x80,[0x31]]]");
		
		
//		HttpGet get = new HttpGet("http://192.168.1.142:31413/call.json?method=set&params=[GeneralLighting2,[0x80,[0x30]]]");
//		HttpGet get = new HttpGet("http://192.168.1.142:31413/call.json?method=get&params=[GeneralLighting5,[0xB0,[0x00]]]");
		try {
			for(HttpGet httpGet : list) {
				HttpResponse response = client.execute(httpGet);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}
				Fun.log(builder.toString());
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
		
	}

	public void hue() {
		new Runnable() {
			public void run() {
				
			}
		};
	}

	public Test(Runnable runnable) {
		super(runnable);
		// TODO Auto-generated constructor stub
	}

	public Test(String threadName) {
		super(threadName);
		// TODO Auto-generated constructor stub
	}

	public Test(Runnable runnable, String threadName) {
		super(runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public Test(ThreadGroup group, Runnable runnable) {
		super(group, runnable);
		// TODO Auto-generated constructor stub
	}

	public Test(ThreadGroup group, String threadName) {
		super(group, threadName);
		// TODO Auto-generated constructor stub
	}

	public Test(ThreadGroup group, Runnable runnable, String threadName) {
		super(group, runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public Test(ThreadGroup group, Runnable runnable, String threadName, long stackSize) {
		super(group, runnable, threadName, stackSize);
		// TODO Auto-generated constructor stub
	}

}
