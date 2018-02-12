import java.net.*;
import java.io.*;

public class JavaIperfer {
    public static void main(String args []) {
        if (args[0].equals("-c")) {
            if (args.length != 7) {
                System.out.println("Error: missing or additional arguments");
                return;
            }
            if (!args[1].equals("-h") || !args[3].equals("-p") || !args[5].equals("-t")) {
                System.out.println("Error: command not found");
                return;
            }
            int port_num = Integer.parseInt(args[4]);
            if (port_num > 65535 || port_num < 1024) {
                System.out.println("Error: port number must be in the range 1024 to 65535");
                return;
            }
            String hostname = args[2];
            long time = Long.valueOf(args[6]);
            client(port_num, hostname, time);
        }
        else if (args[0].equals("-s")) {
            if (args.length != 3) {
                System.out.println("Error: missing or additional arguments");
                return;
            }
            if (!args[1].equals("-p")) {
                System.out.println("Error: command not found");
                return;
            }
            int port_num = Integer.parseInt(args[2]);
            if (port_num > 65535 || port_num < 1024) {
                System.out.println("Error: port number must be in the range 1024 to 65535");
                return;
            }
            server(port_num);
        }
        else {
            System.out.println("Error: command not found");
            return;
        }
    }

    public static void server(int port) {
        try {
            ServerSocket s_socket = new ServerSocket(port);
            Socket assigned_socket = s_socket.accept();
            InputStream in = assigned_socket.getInputStream();
            byte[] buffer = new byte[1000];
            int size = 0;
            int data_sum = 0;
            long initial_time = 0;
            boolean flag = true;
            while((size = in.read(buffer)) != -1) {
                if (flag) {
                    initial_time = System.nanoTime();
                    flag = false;
                }
                data_sum += size;
            }
            long end_time = System.nanoTime();
            assigned_socket.close();
            s_socket.close();
            double time = (end_time - initial_time) / Math.pow(10, 9);
            int received = data_sum / 1000;
            double bandwidth = data_sum * 8 / Math.pow(10, 6) / time;
            System.out.format("received= %d KB rate= %.3f Mpbs", received, bandwidth);
        }
        catch (IOException e) {
            System.out.println("Fail to create a socket");
        }
    }

    public static void client(int port, String hostname, long time) {
        try {
            Socket c_socket = new Socket(hostname, port);
            byte[] buffer = new byte[1000];
            OutputStream o = c_socket.getOutputStream();
            int data_sum = 0;
            long initial_time = System.nanoTime();
            while(true) {
                o.write(buffer);
                data_sum += 1000;
                long flag_time = System.nanoTime();
                if (flag_time - initial_time >= time * Math.pow(10, 9)) {
                    break;
                }
            }
            c_socket.close();
            int sent = data_sum / 1000;
            double bandwidth = data_sum * 8 / Math.pow(10, 6) / time;
            System.out.format("sent= %d KB rate= %.3f Mpbs", sent, bandwidth);
        }
        catch (IOException e) {
            System.out.println("Fail to create a socket");
        }
    }
}
