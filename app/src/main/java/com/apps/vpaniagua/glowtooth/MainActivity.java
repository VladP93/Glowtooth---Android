package com.apps.vpaniagua.glowtooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    LinearLayout foco1, foco2, foco3, foco4, foco5, foco6, foco7, foco8;
    TextView ub1,ub2,ub3,ub4,ub5,ub6,ub7,ub8, cons1,cons2,cons3,cons4,cons5,cons6,cons7,cons8;
    ImageView imgfoco1,imgfoco2, imgfoco3, imgfoco4, imgfoco5, imgfoco6, imgfoco7, imgfoco8;
    Handler bluetoothIn;
    final int handlerState = 0;
    boolean encendido1, encendido2, encendido3, encendido4, encendido5, encendido6, encendido7, encendido8;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setElementos();

        foco1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encendido1 = foco(1,ub1,cons1,imgfoco1,encendido1);
            }
        });

        foco2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                encendido2 = foco(2,ub2,cons2,imgfoco2,encendido2);
            }
        });

        foco3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encendido3 = foco(3,ub3,cons3,imgfoco3,encendido3);
            }
        });

        foco4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encendido4 = foco(4,ub4,cons4,imgfoco4,encendido4);
            }
        });
        foco5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encendido5 = foco(5,ub5,cons5,imgfoco5,encendido5);
            }
        });
        foco6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encendido6 = foco(6,ub6,cons6,imgfoco6,encendido6);
            }
        });
        foco7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encendido7 = foco(7,ub7,cons7,imgfoco7,encendido7);
            }
        });
        foco8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encendido8 = foco(8,ub8,cons8,imgfoco8,encendido8);
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        registerForContextMenu(foco1);
        registerForContextMenu(foco2);
        registerForContextMenu(foco3);
        registerForContextMenu(foco4);
        registerForContextMenu(foco5);
        registerForContextMenu(foco6);
        registerForContextMenu(foco7);
        registerForContextMenu(foco8);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_focos, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.mnuConsumo:
                Toast.makeText(this, "Consumo foco# :"+info.id, Toast.LENGTH_LONG).show();
                return true;
            case R.id.mnuUbicacion:
                Toast.makeText(this, "Ubicacion: foco# "+info.id, Toast.LENGTH_LONG).show();
            default:
                return super.onContextItemSelected(item);
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        address = intent.getStringExtra(SelectBT.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }

    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private boolean foco(int id, TextView ub, TextView cons, ImageView imgfoco, boolean encendido){

        if(encendido){
            encendido=false;
            Toast.makeText(this, "Foco "+id+" apagado", Toast.LENGTH_SHORT).show();
            imgfoco.setImageResource(R.drawable.f);
            MyConexionBT.write(""+id+",0");
        } else{
            encendido=true;
            Toast.makeText(this, "foco: "+id+" encendido",Toast.LENGTH_LONG).show();
            imgfoco.setImageResource(R.drawable.f3);
            MyConexionBT.write(""+id+",1");
        }

        return encendido;

    }

    private void setElementos(){
        //Layouts
        foco1 = (LinearLayout) findViewById(R.id.foco1);
        foco2 = (LinearLayout) findViewById(R.id.foco2);
        foco3 = (LinearLayout) findViewById(R.id.foco3);
        foco4 = (LinearLayout) findViewById(R.id.foco4);
        foco5 = (LinearLayout) findViewById(R.id.foco5);
        foco6 = (LinearLayout) findViewById(R.id.foco6);
        foco7 = (LinearLayout) findViewById(R.id.foco7);
        foco8 = (LinearLayout) findViewById(R.id.foco8);

        //TextView
        //Ubicaciones
        ub1 = (TextView) findViewById(R.id.txtUbicacion1);
        ub2 = (TextView) findViewById(R.id.txtUbicacion2);
        ub3 = (TextView) findViewById(R.id.txtUbicacion3);
        ub4 = (TextView) findViewById(R.id.txtUbicacion4);
        ub5 = (TextView) findViewById(R.id.txtUbicacion5);
        ub6 = (TextView) findViewById(R.id.txtUbicacion6);
        ub7 = (TextView) findViewById(R.id.txtUbicacion7);
        ub8 = (TextView) findViewById(R.id.txtUbicacion8);
        //Consumos
        cons1 = (TextView) findViewById(R.id.txtConsumo1);
        cons2 = (TextView) findViewById(R.id.txtConsumo2);
        cons3 = (TextView) findViewById(R.id.txtConsumo3);
        cons4 = (TextView) findViewById(R.id.txtConsumo4);
        cons5 = (TextView) findViewById(R.id.txtConsumo5);
        cons6 = (TextView) findViewById(R.id.txtConsumo6);
        cons7 = (TextView) findViewById(R.id.txtConsumo7);
        cons8 = (TextView) findViewById(R.id.txtConsumo8);

        //Imagenes
        imgfoco1 = (ImageView) findViewById(R.id.imgfoco1);
        imgfoco2 = (ImageView) findViewById(R.id.imgfoco2);
        imgfoco3 = (ImageView) findViewById(R.id.imgfoco3);
        imgfoco4 = (ImageView) findViewById(R.id.imgfoco4);
        imgfoco5 = (ImageView) findViewById(R.id.imgfoco5);
        imgfoco6 = (ImageView) findViewById(R.id.imgfoco6);
        imgfoco7 = (ImageView) findViewById(R.id.imgfoco7);
        imgfoco8 = (ImageView) findViewById(R.id.imgfoco8);

        //Encendido
        encendido1 = false;
        encendido2 = false;
        encendido3 = false;
        encendido4 = false;
        encendido5 = false;
        encendido6 = false;
        encendido7 = false;
        encendido8 = false;

    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }



}
