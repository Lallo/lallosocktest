package zeta.idee.lallosocktest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LallosocktestActivity extends Activity implements OnClickListener {
   
	final String TAG = "LALLOSOCKET";
  	SharedPreferences sharedPreferences; 
  	String pref_server_ip = "192.168.1.1";
  	String pref_server_port = "4444";
  	ArrayList<String> stringhe_comandi = new ArrayList<String>();
  	ArrayList<String> stringhe_bottoni = new ArrayList<String>();
  	ArrayList<Button> bottoni = new ArrayList<Button>();
  	private Socket socket;
	
  	ToggleButton togglebuttonconnect;
  	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); 
        
        togglebuttonconnect = (ToggleButton) findViewById(R.id.toggleButtonConnect);
        togglebuttonconnect.setOnClickListener(this);
        bottoni.add((Button) findViewById(R.id.Button01));
        bottoni.add((Button) findViewById(R.id.Button02));
        bottoni.add((Button) findViewById(R.id.Button03));
        bottoni.add((Button) findViewById(R.id.Button04));
        bottoni.add((Button) findViewById(R.id.Button05));
        for(int i=0; i<5; i++)  {
        	bottoni.get(i).setOnClickListener(this);
        	bottoni.get(i).setEnabled(togglebuttonconnect.isChecked());
        }
        
    }
    
    
	@Override
	protected void onStart() {
		super.onStart();
		//-- recupero preferences
        getMyPreferences();
        //-- ridefinisci le stringhe in visualizzazione
        setMyButtons();
	}

	  void getMyPreferences() {
		    try {
				Log.i(TAG, "recupero preferences");
		    	// configurazione server
		    	pref_server_ip = sharedPreferences.getString("pref_server_ip", pref_server_ip);
		    	pref_server_port = sharedPreferences.getString("pref_server_port", pref_server_port);
		        // configurazione bottoni e comandi
		      	stringhe_comandi = new ArrayList<String>();
		      	stringhe_bottoni = new ArrayList<String>();
		    	for(int i=1; i<=5; i++) {
		    		stringhe_bottoni.add(sharedPreferences.getString("pref_comando_button_"+i, getString(R.string.bottonetest)));
		    		stringhe_comandi.add(sharedPreferences.getString("pref_comando_comando_"+i, ""));	    			
		    	}
		    } catch (Exception e) {
		    	// --> Log.e(TAG, e.toString());
		    }    	
	    }  		
	
	  
	   void setMyButtons()  {
		   Log.i(TAG, "settaggio visualizzazione");
	       for(int i=0; i<5; i++) {
		       	String nomebottone = stringhe_bottoni.get(i);
		       	String comandobottone = stringhe_comandi.get(i);
		       	Log.i(TAG, "nome bottone "+(i+1)+" = "+nomebottone);
		       	bottoni.get(i).setVisibility(nomebottone.contentEquals("") ||  comandobottone.contentEquals("") ? View.INVISIBLE : View.VISIBLE);
		       	bottoni.get(i).setText(nomebottone);
	       }	  
	   }
	   
	   public boolean onCreateOptionsMenu(Menu menu) {
	    	getMenuInflater().inflate(R.menu.menu, menu);
	    	return true;
	    }     
	    
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	    	return true;
	    }      
	      
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
	    	case R.id.itemSettings:
	        	Intent i = new Intent(this, LallosocketPreferencesActivity.class);   
	    		startActivity(i);
	    		break;
	    	}
	    	return super.onOptionsItemSelected(item);
	    }


		@Override
		public void onClick(View v) {
			if(v.getId()==togglebuttonconnect.getId())  {
				if(togglebuttonconnect.isChecked())
					serverConnect();
				else
					serverSconnect();
			} else 	{
				for(int i=0; i<5; i++)  {
					if(bottoni.get(i).getId()==v.getId()) sendCommand(i);
				}
			}
		} 
	    
		void serverConnect()  {
			Log.i(TAG, "inizio connessione");		
		      try {
		          InetAddress serverAddr = InetAddress.getByName(pref_server_ip);
		          socket = new Socket(serverAddr, Integer.parseInt(pref_server_port));
		          socket.setSoTimeout(5000);
		          buttonSetEnabledState(true);
		       } catch (Exception e) {
		    	  errore(e.toString());
		          e.printStackTrace();
		          togglebuttonconnect.setChecked(false);
		       } 
		}
		
		void serverSconnect()  {
			Log.i(TAG, "sconnessione");	
			socket = null;
			buttonSetEnabledState(false);
		}
    
		void buttonSetEnabledState(boolean enable) {
	        for(int i=0; i<5; i++)  {
	        	bottoni.get(i).setEnabled(enable);
	        }			
		}
		
		void sendCommand(int i)  {
			String comando = stringhe_comandi.get(i);
			Log.i(TAG, "invio comando: "+comando);	
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                out.println(comando);
                Log.d(TAG, "client sent message");
             } catch (UnknownHostException e) {
                errore(e.toString());
                e.printStackTrace();
             } catch (IOException e) {
            	errore(e.toString());
                e.printStackTrace();
             } catch (Exception e) {
            	errore(e.toString());
                e.printStackTrace();
             }
		}
		
		void errore(String err) {
			Toast.makeText(getBaseContext(), getString(R.string.errore)+err, Toast.LENGTH_LONG).show();
		}
}