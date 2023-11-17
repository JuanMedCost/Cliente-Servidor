package org.example;

import java.net.*;
import java.util.Scanner;

public class UsuarioPSP1 {
    private final static int MAX_BYTES = 1400;
    private final static String COD_TEXTO = "UTF-8";
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("ERROR: Indicar puerto");
            System.exit(1);
        }
        int ipLocal = Integer.parseInt(args[0]);
        int ipEnviar = Integer.parseInt(args[2]);
        String servidor = args[1];
        int controlador = 0;
        try(DatagramSocket serverSocket = new DatagramSocket(ipLocal);DatagramSocket clientSocket = new DatagramSocket()){
            Scanner sc = new Scanner(System.in);
            System.out.printf("Creado socket de datagramas para puerto %s.\n", ipLocal);

            while (true){
                if(controlador % 2 == 0){
                    System.out.println("Esperando datagramas.");
                    byte[] datosRecibidos = new byte[MAX_BYTES];
                    DatagramPacket paqueteRecibido = new DatagramPacket(datosRecibidos, datosRecibidos.length);
                    serverSocket.receive(paqueteRecibido);
                    int puertoPaqueteRecibido = paqueteRecibido.getPort();
                    String lineaRecibida = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength(), COD_TEXTO);
                    System.out.println("Esta es la linea recibida: " + lineaRecibida);
                    String hacerHash = MD5.getMd5(lineaRecibida);
                    System.out.println("Comprobación Hash: " + hacerHash);
                    byte[] b = lineaRecibida.getBytes(COD_TEXTO);
                    DatagramPacket paqueteEnviado = new DatagramPacket(b, b.length, InetAddress.getByName(servidor), puertoPaqueteRecibido);
                    serverSocket.send(paqueteEnviado);
                    controlador++;
                }else{
                    System.out.println("Escribir mensaje a enviar:");
                    String mensaje = sc.nextLine();
                    String hacerHash = MD5.getMd5(mensaje);
                    mensaje = mensaje + " " + hacerHash;
                    byte[] b = mensaje.getBytes(COD_TEXTO);
                    DatagramPacket paqueteEnviado = new DatagramPacket(b, b.length, InetAddress.getByName(servidor), ipEnviar);
                    clientSocket.send(paqueteEnviado);
                    byte[] datosRecibidos = new byte[MAX_BYTES];
                    DatagramPacket paqueteRecibido = new DatagramPacket(datosRecibidos, datosRecibidos.length);
                    clientSocket.receive(paqueteRecibido);
                    String lineaRecibida = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength(), COD_TEXTO);
                    System.out.printf("Mensaje recibido: %s\n", lineaRecibida);
                    controlador++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
