package com.example.hotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button botonServer, botonCliente, botonSend;
    TextView IPDispositivo;
    TextInputEditText inputIP, inputMensaje;

    static final int MESSAGE_READ=1;
    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IPDispositivo=findViewById(R.id.IPDispositivo);
        inputIP=findViewById(R.id.inputIPServer);
        inputMensaje=findViewById(R.id.inputMensaje);
        botonServer=findViewById(R.id.botonServer);
        botonSend=findViewById(R.id.botonSend);
        botonCliente=findViewById(R.id.botonCliente);
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        IPDispositivo.setText(getIPAddress(true));
        botonServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tocaste server");
                serverClass=new ServerClass();
                serverClass.start();
            }
        });

        botonCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tocaste cliente");
                String ipServer=inputIP.getText().toString();
                System.out.println(ipServer);
                clientClass=new ClientClass(ipServer);
                clientClass.start();
            }
        });

        botonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=inputMensaje.getText().toString();
                byte[] bytesMsg=msg.getBytes();
                for(int i=0;i<bytesMsg.length;i++){
                    System.out.println(bytesMsg[i]);
                }
                System.out.println("tocaste send 1"+ msg);
                Write escribir=new Write();
                escribir.execute(bytesMsg);
//                sendReceive.write("hola".getBytes());
            }
        });
    }
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                System.out.println("entre al run server");
                serverSocket=new ServerSocket(7028);
                socket=serverSocket.accept();
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (Exception e) {
                System.out.println("entre al catch");
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt){
            System.out.println("entre al constructor");
            socket=skt;
            try {
                System.out.println("se construyo el sendReceive");
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                System.out.println("entre al catch");
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
        public ClientClass(String hostAddress){
            hostAdd=hostAddress;
            socket=new Socket();
        }

        @Override
        public void run() {
            try {
                System.out.println("entre al run client");
                socket.connect(new InetSocketAddress(hostAdd,7028),5000);
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public class Write extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                sendReceive.write((byte[]) objects[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}