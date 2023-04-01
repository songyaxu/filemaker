package java;

import com.google.common.collect.Lists;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.AbstractFileHeader;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * zip文件操作.
 *
 * @author yaxuSong
 **/
public class ZipUtils {

    private static final Logger log = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 解压zip文件
     *
     * @param filePath 待解压文件
     * @param password 密码 可为空
     * @param outdir   解压到目录
     * @return
     */
    public static boolean unzipFiles(String filePath, String password, String outdir) {
        char[] passCharArray = null;
        if (StringUtils.isNotBlank(password)) {
            passCharArray = password.toCharArray();
        }
        try (ZipFile zip = new ZipFile(filePath, passCharArray)) {
            zip.extractAll(outdir);
            return true;
        } catch (Exception e) {
            log.error("解压文件[{}]-[{}]出错。错误信息：", filePath, password, e);
        }
        return false;
    }

    public static boolean unzipPackageFiles(String filePath, String password, String outdir) {
        char[] passCharArray = null;
        if (StringUtils.isNotBlank(password)) {
            passCharArray = password.toCharArray();
        }
        try (ZipFile zip = new ZipFile(filePath, passCharArray)) {
            List<FileHeader> headerList = zip.getFileHeaders();
            boolean directory = headerList.stream().anyMatch(AbstractFileHeader::isDirectory);
            if (directory) {
                for (FileHeader fileHeader : headerList) {
                    if (!fileHeader.isDirectory()) {
                        String filename = fileHeader.getFileName().substring(fileHeader.getFileName().lastIndexOf("/") + 1);
                        zip.extractFile(fileHeader, outdir, filename);
                    }
                }
            } else {
                zip.extractAll(outdir);
            }
            return true;
        } catch (Exception e) {
            log.error("解压文件[{}]-[{}]出错。错误信息：", filePath, password, e);
        }
        return false;
    }

    /**
     * 解压文件 并列出里边的内容
     *
     * @param filePath 待解压文件
     * @param password 密码 可为空
     * @param outdir   解压到的位置
     * @return
     */
    public static List<File> unzipFilesAndListFiles(String filePath, String password, String outdir) {
        if (unzipFiles(filePath, password, outdir)) {
            try {
                File folder = new File(outdir);
                File[] files = folder.listFiles();
                if (files != null && files.length > 0) {
                    return Stream.of(files).collect(Collectors.toList());
                }
            } catch (Exception e) {
                log.error("获取解压后文件[{}]列表出错:", filePath, e);
            }
        }
        return Lists.newArrayList();
    }

    /**
     * 添加文件到已存在的zip文件
     *
     * @param existZipFile zip文件(可没有创建的文件)
     * @param password     密码 可没有密码
     * @param fileLists    要加入的文件
     * @return
     */
    public static boolean addFileToExistZipFile(String existZipFile, String password, List<String> fileLists) {
        if (CollectionUtils.isEmpty(fileLists)) {
            log.info("没有可压缩的文件.");
            return false;
        }
        ZipParameters zipParameters = new ZipParameters();
        char[] passCharArray = null;
        //a password protected zip file needed.
        if (StringUtils.isNotBlank(password)) {
            passCharArray = password.toCharArray();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            // Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        }
        List<File> filesToAdd = fileLists.stream().map(File::new).collect(Collectors.toList());

        try (ZipFile zipFile = new ZipFile(existZipFile, passCharArray)) {
            zipFile.addFiles(filesToAdd, zipParameters);
            return true;
        } catch (Exception e) {
            log.error("添加文件到zip:[{}]失败:", existZipFile, e);
        }
        return false;
    }


    public static void main(String[] args) {
        String zipFile = "C:\\Users\\Administrator\\Desktop\\Tencentflex-V2000120-20200421-feedback-1.zip";
        String password = "123456";
        String outDir = "C:\\Users\\Administrator\\Desktop\\pici212121";
//        List<File> fileList = unzipFilesAndListFiles(zipFile, password, outDir);
//        System.out.println(fileList);

        String zipFile2 = "C:\\Users\\Administrator\\Desktop\\aaaaa.zip";

        List<String> listFiles = Lists.newArrayList("C:\\Users\\Administrator\\Desktop\\工作簿1.xlsx", "C:\\Users\\Administrator\\Desktop\\系统改进.doc");
        addFileToExistZipFile(zipFile2, "123456", listFiles);
    }
}
