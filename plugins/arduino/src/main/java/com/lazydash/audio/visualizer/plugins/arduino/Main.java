package com.lazydash.audio.visualizer.plugins.arduino;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
/*
        SerialPort[] commPorts = SerialPort.getCommPorts();
        IntStream.range(0, commPorts.length).forEach(i -> {
            System.out.println(commPorts[i].getDescriptivePortName());
        });*/


        SerialPort com3 = SerialPort.getCommPort("COM3");
        com3.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        com3.setBaudRate(9600);
        com3.openPort();

        String messageReceived = "";
        boolean flip = false;
        boolean run = true;
        while (run) {

            while (messageReceived.length()  == 0 || !messageReceived.contains("\n") ) {
                byte[] readBuffer = new byte[com3.bytesAvailable()];
                com3.readBytes(readBuffer, readBuffer.length);

                messageReceived = messageReceived + new String(readBuffer);
            }


            System.out.println(messageReceived.substring(0, messageReceived.length() -2));
            if (messageReceived.equals("refresh\r\n")) {
                byte[] bytes = new byte[30*3];
                if (flip) {
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] =  (byte) ((char) 255);
                    }

                } else {
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = (byte) ((char) 0);
                    }

                }

                flip = !flip;

                for (int i = 0; i < bytes.length; i++) {
                    System.out.print(bytes[i] + " ");
                }
                System.out.println();
                com3.writeBytes(bytes, bytes.length);
            }


            messageReceived = "";

        }

        com3.closePort();
    }

}
