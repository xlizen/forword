package cocmmon;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

/**
 * @author lengchunyun
 */
public class SerialUtils {

    /**
     * 开启串口
     *
     * @param serialPortName 串口名称
     * @param baudRate       波特率
     * @return 串口对象
     */
    public static SerialPort openSerialPort(String serialPortName, int baudRate) {
        try {
            // 通过端口名称得到端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
            // 打开端口  自定义名字，打开超时时间
            CommPort commPort = portIdentifier.open(serialPortName, 2222);
            // 判断是不是串口
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                //设置串口参数（波特率，数据位8， 停止位1， 校验位无）
                serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                System.out.println("串口开启成功，串口名称： " + serialPortName);
                return serialPort;
            } else {
                //是其他类型端口
                throw new NoSuchPortException();
            }
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭串口
     *
     * @param serialPort 要关闭的串口对象
     */
    public static void closeSerialPort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            System.out.println("关闭串口：" + serialPort.getName());
            serialPort = null;
        }
    }

    /**
     * 向串口发送数据
     *
     * @param serialPort 串口对象
     * @param data       发送的数据
     */
    public static void sendData(SerialPort serialPort, byte[] data) {
        OutputStream os = null;
        try {
            //获取串口的输出流
            os = serialPort.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 要读取的串口
     * @return 读取的数据
     */
    public static byte[] readData(SerialPort serialPort) {
        InputStream is = null;
        byte[] bytes = null;
        try {
            //获得串口的输入流
            is = serialPort.getInputStream();
            //获取数据长度
            int bufflenth = is.available();
            while (bufflenth != 0) {
                bytes = new byte[bufflenth];
                int read = is.read(bytes);
                bufflenth = is.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public static void setListenerToSerialPort(SerialPort serialPort, SerialPortEventListener listener) {
        try {
            //给串口添加事件监听
            serialPort.addEventListener(listener);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        //串口有数据监听
        serialPort.notifyOnDataAvailable(true);
        //中断事件监听
        serialPort.notifyOnBreakInterrupt(true);
    }
}
