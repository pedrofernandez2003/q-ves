package com.example.conectividad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.InetAddresses;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button botonWifi, botonDiscover, botonSend;
    TextView estadoWifi, textoDiscover;
    ListView peerListView;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ=1;
    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialWork();
        exqListener();
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    estadoWifi.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    public void exqListener() {
        botonWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    estadoWifi.setText("OFF");
                } else {
                    wifiManager.setWifiEnabled(true);
                    estadoWifi.setText("ON");
                }
            }
        });

        Context context = this.getApplicationContext();
        botonDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tocaste discover");
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("entro el if");

                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            System.out.println("entro success");
                            textoDiscover.setText("Discovery started");
                        }

                        @Override
                        public void onFailure(int reason) {
                            System.out.println("entro failure");
                            textoDiscover.setText("Discovery failed");
                        }
                    });
                }
            }
        });
        peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = deviceArray[position];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "connected to " + ip, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(getApplicationContext(), "not connected", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        botonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="hola";
                byte[] barr=msg.getBytes();
                for(int i=0;i<barr.length;i++){
                    System.out.println(barr[i]);
                }
                System.out.println("tocaste send 1"+ "hola");
                sendReceive.write("hola".getBytes());
            }
        });
    }

    public void initialWork(){
        botonWifi=(Button)findViewById(R.id.botonWifi);
        botonSend=(Button)findViewById(R.id.botonSend);
        botonDiscover=(Button)findViewById(R.id.botonDiscover);
        estadoWifi=(TextView)findViewById(R.id.estadoWifi);
        textoDiscover=(TextView)findViewById(R.id.textoDiscover);
        peerListView=(ListView)findViewById(R.id.peerListView);
        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager= (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel=mManager.initialize(this,getMainLooper(),null); //canal que conecta la aplicacion con el framework de wifi p2p
        mReceiver=new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                deviceNameArray=new String[peerList.getDeviceList().size()];//ver que hace
                deviceArray=new WifiP2pDevice[peerList.getDeviceList().size()];
                int index=0;
                for(WifiP2pDevice device:peerList.getDeviceList()){
                    deviceNameArray[index]=device.deviceName;
                    deviceArray[index]=device;
                    index++;
                }
                ArrayAdapter<String>adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray);
                peerListView.setAdapter(adapter);
            }
            if(peers.size()==0){
                Toast.makeText(getApplicationContext(),"No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;
            if(info.groupFormed && info.isGroupOwner) {
                textoDiscover.setText("Host");
                serverClass=new ServerClass();
                serverClass.start();
            }
            else if (info.groupFormed) {
                textoDiscover.setText("Client");
                clientClass=new ClientClass(groupOwnerAddress);
                clientClass.start();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                System.out.println("entre al run");
                serverSocket=new ServerSocket(7028);
                socket=serverSocket.accept();
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        public SendReceive(Socket skt){
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer= new byte[1024];
            int bytes;
            while (socket!=null){
                try {
                    bytes=inputStream.read(buffer);
                    if (bytes>0){
                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;
        public ClientClass(InetAddress hostAddress){
          hostAdd=hostAddress.getHostAddress();
          socket=new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,7028),500);
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}