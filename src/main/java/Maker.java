import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 11:03 2018/12/3
 * @Modified by:
 */
public class Maker {


    private static final String FILE = "C:\\Users\\yaxuSong\\Desktop\\AAAAA";

    /**
     * 复制文件到另一个文件夹内
     * @param fileName
     * @return
     */
    private boolean copyFile(File fileName) {
        int bytesum = 0;
        int byteread = 0;
        File oldfile = fileName;
        try {
            if (oldfile.exists()) { //文件存在时
                FileInputStream inStream = new FileInputStream(oldfile); //读入原文件
                FileOutputStream fs = new FileOutputStream(new File(FILE, oldfile.getName()));
                byte[] buffer = new byte[5120];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            System.out.println("复制文件成功：" + fileName.getName());
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        String folderName = "C:\\Users\\yaxuSong\\Desktop\\avatar";
        File folder = new File(folderName);
        Maker maker = new Maker();
        maker.iterationFile(folder);
    }

    public void iterationFile(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (int i= 0;i<files.length;i++){
                iterationFile(files[i]);
            }
        }else {
            if (file.exists()){
                copyFile(file);
                return;
            }
        }
    }

}
