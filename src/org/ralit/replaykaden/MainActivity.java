package org.ralit.replaykaden;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends Activity implements OnClickListener {

	private Test test;
	private ArrayList<String> nicknameList = new ArrayList<String>();
	private LinearLayout downlightLayout;
	private Activity activity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		Button recButton = new Button(this);
		recButton.setText("記録はじめ");
		recButton.setOnClickListener(this);

		Button stopButton = new Button(this);
		stopButton.setText("記録おわり");
		stopButton.setOnClickListener(this);

		Button playButton = new Button(this);
		playButton.setText("再生");
		playButton.setOnClickListener(this);

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(recButton);
		linearLayout.addView(stopButton);
		linearLayout.addView(playButton);
		setContentView(linearLayout);

		downlightLayout = new LinearLayout(this);
		downlightLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(downlightLayout);

		Thread thread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					DefaultHttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet("http://192.168.1.142:31413/call.json?method=list");
					HttpResponse response = client.execute(get);
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						builder.append(line + "\n");
					}
					JsonNode jsonNode = new ObjectMapper().readTree(builder.toString());
					JsonNode jsonArray = jsonNode.path("result");

					for (int i = 0; i < jsonArray.size(); i++) {
						//						Fun.log(jsonArray.get(i).path("deviceType").asText());
						if( jsonArray.get(i).path("deviceType").asText().equals("0x0290") ) {
							nicknameList.add(jsonArray.get(i).path("nickname").asText());
						}
					}
					//					Fun.log(jsonArray.size());
					//					Fun.log(builder.toString());
					//					Fun.log(nicknameList.size());
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							for(String nickname : nicknameList) {
								//								Fun.log(nickname);
								Button lightButton = new Button(getApplicationContext());
								lightButton.setText(nickname);
								downlightLayout.addView(lightButton);
								lightButton.setOnClickListener((OnClickListener)activity);
								lightButton.setTextColor(Color.BLACK);
							}
						}
					});
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				// 記録はじめのコピー
				
				super.run();
			}
		};
		thread.start();

		Thread thread2 = new Thread() {

			@Override
			public void run() {
				
				super.run();
			}
		};
		thread2.start();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private Button button;
	private Button childButton;
	private int value;
	
	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub
		//		Test test = new Test();
		//		test.start();
		button = (Button)v;
		if(button.getText().toString().equals("記録はじめ")) {
			Fun.log(button.getText().toString());
			isRecording = true;
			commandList = new ArrayList<Command>();
			initialCommandList = new ArrayList<String>();

			Thread thread = new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					for(int i = 0; i < downlightLayout.getChildCount(); i++) {
						childButton = (Button)downlightLayout.getChildAt(i);
						
						String nickname = childButton.getText().toString();
						DefaultHttpClient client = new DefaultHttpClient();
						HttpGet get = new HttpGet("http://192.168.1.142:31413/call.json?method=get&params=[" + nickname + ",0x80]");
						HttpResponse response;
						try {
							response = client.execute(get);
							BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
							StringBuilder builder = new StringBuilder();
							String line = null;
							while ((line = reader.readLine()) != null) {
								builder.append(line + "\n");
							}
							Fun.log(builder.toString());
							JsonNode jsonNode = new ObjectMapper().readTree(builder.toString());
							value = jsonNode.path("result").path("property").get(0).path("value").get(0).asInt();
							Fun.log(value);
							String url = "http://192.168.1.142:31413/call.json?method=set&params=[" + nickname + ",[0x80,["+ value + "]]]";
							initialCommandList.add(url);
							runOnUiThread(new Runnable() {
								public void run() {
									if(value == 0x30) {
										childButton.setBackgroundColor(Color.YELLOW);
									} else {
										childButton.setBackgroundColor(Color.DKGRAY);
									}
								}
							});
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					previousTime = System.currentTimeMillis();
					super.run();
				}
			};
			thread.start();
			

			
		} else if(button.getText().toString().equals("記録おわり")) {
			Fun.log(button.getText().toString());
			isRecording = false;
		} else if(button.getText().toString().equals("再生")) { 
			Fun.log(button.getText().toString());
			Thread runnable = new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					for(String string : initialCommandList) {
						try {
							DefaultHttpClient client = new DefaultHttpClient();
							HttpGet get = new HttpGet(string);
							client.execute(get);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					for(Command command : commandList) {
						try {
							sleep(command.time);
							DefaultHttpClient client = new DefaultHttpClient();
							HttpGet get = new HttpGet(command.url);
							client.execute(get);
							Fun.log(command.time);
							Fun.log(command.url);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					super.run();
				}
			};
			runnable.start();
		} else {
			Thread runnable = new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//					Button button = (Button)v;
					String nickname = button.getText().toString();
					DefaultHttpClient client = new DefaultHttpClient();
					//					ArrayList<HttpGet> list = new ArrayList<HttpGet>();
					//					HttpGet get = new HttpGet("http://192.168.1.142:31413/call.json?method=list");
					//					list.add(get);
					HttpGet get = new HttpGet("http://192.168.1.142:31413/call.json?method=get&params=[" + nickname + ",0x80]");
					HttpResponse response;
					try {
						response = client.execute(get);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						StringBuilder builder = new StringBuilder();
						String line = null;
						while ((line = reader.readLine()) != null) {
							builder.append(line + "\n");
						}
						Fun.log(builder.toString());
						JsonNode jsonNode = new ObjectMapper().readTree(builder.toString());
						int value = jsonNode.path("result").path("property").get(0).path("value").get(0).asInt();
						Fun.log(value);
						if(value == 0x30) {
							Fun.log(value + "うえ");
							String url = "http://192.168.1.142:31413/call.json?method=set&params=[" + nickname + ",[0x80,[0x31]]]";
							get = new HttpGet(url);
							if (isRecording) {
								Command command = new Command();
								command.time = System.currentTimeMillis() - previousTime;
								command.url = url;
								commandList.add(command);
								runOnUiThread(new Runnable() {
									public void run() {
										button.setBackgroundColor(Color.DKGRAY);
									}
								});
								previousTime = System.currentTimeMillis();
							}
						} else {
							Fun.log(value + "した");
							String url = "http://192.168.1.142:31413/call.json?method=set&params=[" + nickname + ",[0x80,[0x30]]]";
							get = new HttpGet(url);
							if (isRecording) {
								Command command = new Command();
								command.time = System.currentTimeMillis() - previousTime;
								command.url = url;
								commandList.add(command);
								runOnUiThread(new Runnable() {
									public void run() {
										button.setBackgroundColor(Color.YELLOW);
									}
								});
								previousTime = System.currentTimeMillis();
							}
						}
						client.execute(get);

						//						for (int i = 0; i < jsonArray.size(); i++) {
						//							Fun.log(jsonArray.get(i).path("deviceType").asText());
						//							if( jsonArray.get(i).path("deviceType").asText().equals("0x0290") ) {
						//								nicknameList.add(jsonArray.get(i).path("nickname").asText());
						//							}
						//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			runnable.start();
		}

	}


	private boolean isRecording = false;
	private long previousTime;
	private ArrayList<Command> commandList = new ArrayList<Command>();
	private ArrayList<String> initialCommandList = new ArrayList<String>(); 

}

class Command {
	public String url;
	public long time;
}
