package com.example.hp.muscle;

import android.util.Log;

public class HardwareInterface {
    //Led
    static public native int setLedState(int LedID,int ledState);
    //Serial
    static public native int openSerialPort(String DevName,long Baudrate,int DataBits,char Parity,int StopBits);
    static public native int write(int fd,byte [] data);
    static public native int read(int fd,byte [] data,int count);
    static public native int select(int fd,int sec,int usec);
    static public native int close(int fd);
    //PWM
    static public native int PWMOpen();
    static public native int PWMPlay(int fd,int Frequency);
    static public native void PWMStop(int fd);
    //485
    static public native int open485Port(String DevName,long Baudrate,int Send);
    static public native int read485(int fd,byte [] data,int count);
    static public native int write485(int fd,byte [] data);
    static public native int select485(int fd,int sec,int usec);
    static public native void close485(int fd);
    //WDT
    static public native int openWatchDog();
    static public native void keepAliveWatchDog(int fd);
    static public native int setWatchDogTimeout(int fd,int timeout);
    static public native int getWatchDogTimeout(int fd);
    static public native int getWatchDogTimeLeft(int fd);
    static public native void closeWatchDog(int fd);
    //ADC
    static public native int readADC();
    //CAN
    static public native int canup();
    static public native void candown();
    static public native void canset(int rate);
    static public native int cansend(byte [] data,int count);
    static public native int canrecv(byte [] data);

    static {
        try {
            System.loadLibrary("forlinx-hardware");
        } catch (UnsatisfiedLinkError e){
            Log.e("forlinux-hardware", "load library error");
        }
    }
}

