package yitgogo.smart.tools;

import android.os.Environment;

import com.baidu.location.BDLocation;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import yitgogo.smart.model.ModelMachineArea;

public class Device {

//    private static String deviceCode = "4a332b20bba343773a70d9b3d9a42196";
            private static String deviceCode = "FFFFFFFFFFFFFFFFFFFFFFFFFF";
    private static BDLocation location = null;
    private static ModelMachineArea machineArea = new ModelMachineArea();

    public static void init() {
        File codeFileDirectory = new File(
                Environment.getExternalStorageDirectory() + "/Yitgogo/Smart");
        File codeFile = new File(codeFileDirectory.getPath(), "activeCode");
        if (!codeFileDirectory.exists()) {
            codeFileDirectory.mkdirs();
        }
        if (!codeFile.exists()) {
            writeSDFile(codeFile, MD5.GetMD5Code(new Date().getTime() + ""));
        }
//        setDeviceCode(readSDFile(codeFile));
    }

    public static void setDeviceCode(String deviceCode) {
        Device.deviceCode = deviceCode;
    }

    public static String getDeviceCode() {
        return deviceCode;
    }

    public static void setLocation(BDLocation location) {
        Device.location = location;
    }

    public static BDLocation getLocation() {
        return location;
    }

    public static void setMachineArea(ModelMachineArea machineArea) {
        Device.machineArea = machineArea;
    }

    public static ModelMachineArea getMachineArea() {
        return machineArea;
    }

    // public static void setMachineArea(ModelMachineArea machineArea) {
    // Device.machineArea = machineArea;
    // }

    // public static ModelMachineArea getMachineArea() {
    // return machineArea;
    // }

    public static String readSDFile(File file) {
        String codeString = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int length = fileInputStream.available();
            byte[] buffer = new byte[length];
            fileInputStream.read(buffer);
            codeString = EncodingUtils.getString(buffer, "UTF-8");
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codeString;
    }

    public static void writeSDFile(File file, String string) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bytes = string.getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
