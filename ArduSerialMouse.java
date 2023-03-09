package arduserialmouse;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Slam
 */
public class ArduSerialMouse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Robot rob;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        final int maxWidth = gd.getDisplayMode().getWidth();
        final int maxHeight = gd.getDisplayMode().getHeight();
        List<SerialPort> availablePorts = new ArrayList<>();
        try {
            rob = new Robot();
            Enumeration ports = CommPortIdentifier.getPortIdentifiers();
            System.out.println("COM Ports available: ");
            while (ports.hasMoreElements()) {
                CommPortIdentifier identifier = (CommPortIdentifier) ports.nextElement();
                System.out.println("Port: " + identifier.getName() + " detected!");
                availablePorts.add((SerialPort) identifier.open(identifier.getName(), 9600));
                break;
            }
            int aPsize = availablePorts.size();
            System.out.println("Ports availables: " + aPsize);
            if (aPsize > 0) {
                SerialPort sp = availablePorts.get(0);
                if (sp != null) {
                    System.out.println("Openning port: " + sp.toString());
                    sp.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    //sp.enableReceiveTimeout(0);
                    System.out.println("Reading data...");
                    int posx = maxWidth / 2;
                    int posy = maxHeight / 2;
                    while (true) {
                        InputStream in = sp.getInputStream();
                        Scanner scan = new Scanner(in);
                        String line = "";
                        while (scan.hasNextLine()) {
                            line = scan.nextLine();
                            System.out.println(line);
                            //moveToPos(posx, posy, line, rob);
                            String[] cap = line.split(":");
                            System.out.println(posx + ";" + posy);
                            if (cap[0].contains("GyroX")) {
                                int valx = Integer.valueOf(cap[1].trim());
                                if (valx > 0) {
                                    posx++;
                                } else {
                                    posx--;
                                }
                                if (posx >= maxWidth) {
                                    posx = maxWidth;
                                }
                                if (posx <= 0) {
                                    posx = 0;
                                }
                            } else if (cap[0].contains("GyroY")) {
                                int valy = Integer.valueOf(cap[1].trim());
                                if (valy > 0) {
                                    posy++;
                                } else {
                                    posy--;
                                }
                                if (posy >= maxHeight) {
                                    posy = maxHeight;
                                }
                                if (posy <= 0) {
                                    posy = 0;
                                }
                            }
                            rob.mouseMove(posx, posy);
                        }
                        in.close();
                    }
                }
            } else {
                System.out.println("No COM Ports available");
            }
        } catch (UnsupportedCommOperationException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nCause: " + ex.getCause());
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nCause: " + ex.getCause());
        } catch (PortInUseException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nCause: " + ex.getCause());
        } catch (AWTException ex) {
            Logger.getLogger(ArduSerialMouse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void moveToPos(int posx, int posy, String line, Robot r) {
        String[] cap = line.split(":");
        System.out.println(posx + ";" + posy);
        if (cap[0].contains("GyroX")) {
            int valx = Integer.valueOf(cap[1].trim());
            if (valx > 0) {
                posx++;
            } else {
                posx--;
            }
        } else if (cap[0].contains("GyroY")) {
            int valy = Integer.valueOf(cap[1].trim());
            if (valy > 0) {
                posy++;
            } else {
                posy--;
            }
        }
        r.mouseMove(posx, posy);
    }
}
