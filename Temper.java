// from https://www.igorkromin.net/index.php/2013/02/16/using-java-hidapi-on-os-x-to-read-temperature-from-the-temper1-sensor/
//
// HIDDeviceInfo [path=USB_0c45_7401_0x7f8733426a10,
//   vendor_id=3141, 
//   product_id=29697, 
//   serial_number=, 
//   release_number=1, 
//   manufacturer_string=RDing, 
//   product_string=TEMPer1V1.2, 
//   usage_page=65280, 
//   usage=1, 
//   interface_number=-1]
// HIDDeviceInfo [path=USB_0c45_7401_0x7f8733426c90, 
//   vendor_id=3141,
//   product_id=29697,
//   serial_number=,
//   release_number=1,
//   manufacturer_string=RDing,
//   product_string=TEMPer1V1.2,
//   usage_page=1,
//   usage=6,
//   interface_number=-1]

import com.codeminders.hidapi.*;
import java.io.IOException;

class Temper {
    static final int VENDOR_ID = 3141;
    static final int PRODUCT_ID = 29697;
    static final int BUFSIZE = 2048;

    static final int READ_UPDATE_DELAY_MS = 1000;

    public static void main(String[] args) {
        ClassPathLibraryLoader.loadNativeHIDLibrary();
        readDevice();
    }

    static float raw_to_c(int rawtemp) {
        float temp_c = rawtemp * (125.f / 32000.f);
        return temp_c;
    }

    static float c_to_u(float deg_c, char unit) {
        if (unit == 'F')
            return (deg_c * 1.8f) + 32.f;
        else if (unit == 'K')
            return (deg_c + 273.15f);
        else
            return deg_c;
    }

    private static void readDevice() {
        HIDDevice dev;
        try {
            HIDManager hid_mgr = HIDManager.getInstance();
            dev = hid_mgr.openById(VENDOR_ID, PRODUCT_ID, null);
            byte[] temp = new byte[] {
                (byte)0x01, (byte)0x80, (byte)0x33, (byte)0x01, 
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
            };

            int res = dev.write(temp);
            try {
                byte[] buf = new byte[BUFSIZE];
                int n = dev.read(buf);

                int rawtemp = (buf[3] & (byte)0xFF) + (buf[2] << 8);
                if ((buf[2] & 0x80) != 0) {
                    /* return the negative of magnitude of the temperature */
                    rawtemp = -((rawtemp ^ 0xffff) + 1);
                }
                System.out.println("temp = " + c_to_u(raw_to_c(rawtemp), 'C'));
                try {
                    Thread.sleep(READ_UPDATE_DELAY_MS);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                dev.close();
                hid_mgr.release();    
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
