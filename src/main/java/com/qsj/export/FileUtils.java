package com.qsj.export;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

/**
 * 文件操作工具类
 *
 * @author yzh
 */
public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    /**
     * jpg文件后缀名
     */
    public static final String JPG_EXTENSION = "jpg";
    /**
     * xlsx文件后缀名
     */
    public static final String XLSX_EXTENSION = "xlsx";
    /**
     * xls文件后缀名
     */
    public static final String XLS_EXTENSION = "xls";
    /**
     * zip文件后缀名
     */
    public static final String ZIP_EXTENSION = "zip";
    /**
     * json文件后缀名
     */
    public static final String JSON_EXTENSION = "json";



    /**
     * 修正路径，将 \\ 或 / 等替换为 File.separator
     *
     * @param path 待修正的路径
     * @return 修正后的路径
     */
    public static String conversionPath(String path) {
        String p = StringUtils.replace(path, "\\", "/");
        p = StringUtils.join(StringUtils.split(p, "/"), "/");
        if (!StringUtils.startsWith(p, "/") && StringUtils.startsWithAny(path, new String[]{"\\", "/"})) {
            p += "/";
        }
        if (!StringUtils.endsWith(p, "/") && StringUtils.startsWithAny(path, new String[]{"\\", "/"})) {
            p = p + "/";
        }
        if (path != null && path.startsWith("/")) {
            p = "/" + p; // linux下路径
        }
        return p;
    }


    /**
     * 将byte数组转为图片并返回地址
     *
     * @param bytes    byte数组
     * @param savePath 本地路径前缀
     * @return
     */
    public static String byteToImagePath(byte[] bytes, String savePath) {
        try {
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            // 文件保存目录URL
            SimpleDateFormat day = new SimpleDateFormat("yyyyMMddHH");
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + ".jpg";
            String dayName = "/head_img/" + day.format(new Date()) + "/";
            String savePath1 = dayName + newFileName;
            savePath = savePath + dayName;
            File uploadedFile = new File(savePath);

            if (!uploadedFile.exists()) {
                uploadedFile.mkdirs();
            }
            String url = savePath + newFileName;
            OutputStream out = new FileOutputStream(url);
            out.write(bytes);
            out.flush();
            out.close();
            return savePath1;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件扩展名(返回小写)
     *
     * @param fileName 文件名
     * @return 例如：test.jpg  返回：  jpg
     */
    public static String getFileExtension(String fileName) {
        if ((fileName == null) || (fileName.lastIndexOf(".") == -1) || (fileName.lastIndexOf(".") == fileName.length() - 1)) {
            return null;
        }
        return StringUtils.lowerCase(fileName.substring(fileName.lastIndexOf(".") + 1));
    }

    /**
     * 获取文件名，不包含扩展名
     *
     * @param fileName 文件名
     * @return 例如：d:\files\test.jpg  返回：d:\files\test
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if ((fileName == null) || (fileName.lastIndexOf(".") == -1)) {
            return null;
        }
        return fileName.substring(0, fileName.lastIndexOf(".")).trim();
    }

    /**
     * 根据当天日期随机获取文件名
     * 格式：yyyyMMdd/yyyyMMddHHmmss_123.jpg
     *
     * @param fileExtension
     * @return
     */
    public static String getAbsolutelyFileName(String directoryPath, String fileExtension) {
        String relativelyFileName = getRelativelyFileName(fileExtension);
        String absolutelyFileName = directoryPath + relativelyFileName;
        File file = new File(absolutelyFileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return absolutelyFileName;
    }

    /**
     * 根据当天日期随机获取文件名
     * 格式：yyyyMMdd/yyyyMMddHHmmss_123.jpg
     *
     * @param fileExtension
     * @return
     */
    public static String getRelativelyFileName(String fileExtension) {
        SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExtension;
        String dayName = day.format(new Date()) + "/" + newFileName;
        return dayName;
    }


    /**
     * 根据指定路径获取所有的文件
     *
     * @param path
     * @return
     */
    public static List<File> getFiles(String path, List<File> fileList) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileIndex : files) {
                if (fileIndex.isDirectory()) {
                    //如果这个文件是目录，则进行递归搜索
                    getFiles(fileIndex.getPath(), fileList);
                } else {
                    //如果文件是普通文件，则将文件句柄放入集合中
                    fileList.add(fileIndex);
                }
            }
        }
        return fileList;
    }


    /**
     * 创建单个文件
     *
     * @param descFileName 文件名，包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createFile(String descFileName) {
        File file = new File(descFileName);
        if (file.exists()) {
            logger.debug("文件 " + descFileName + " 已存在!");
            return false;
        }
        if (descFileName.endsWith(File.separator)) {
            logger.debug(descFileName + " 为目录，不能创建目录!");
            return false;
        }
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            if (!file.getParentFile().mkdirs()) {
                logger.debug("创建文件所在的目录失败!");
                return false;
            }
        }
        // 创建文件
        try {
            if (file.createNewFile()) {
                logger.debug(descFileName + " 文件创建成功!");
                return true;
            } else {
                logger.debug(descFileName + " 文件创建失败!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(descFileName + " 文件创建失败!");
            return false;
        }
    }

    /**
     * 根据路径创建文件以及目录
     *
     * @param path 绝对路径
     * @return
     */
    public static File createFileByPath(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            return file;
        } else {
            file.mkdirs();
        }
        return file;
    }


    /**
     * 复制单个文件
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @param coverlay     如果目标文件已存在，是否覆盖
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFileCover(String srcFileName,
                                        String descFileName, boolean coverlay) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            logger.debug("复制文件失败，源文件 " + srcFileName + " 不存在!");
            return false;
        }
        // 判断源文件是否是合法的文件
        else if (!srcFile.isFile()) {
            logger.debug("复制文件失败，" + srcFileName + " 不是一个文件!");
            return false;
        }
        File descFile = new File(descFileName);
        // 判断目标文件是否存在
        if (descFile.exists()) {
            // 如果目标文件存在，并且允许覆盖
            if (coverlay) {
                logger.debug("目标文件已存在，准备删除!");
                if (!delFile(descFileName)) {
                    logger.debug("删除目标文件 " + descFileName + " 失败!");
                    return false;
                }
            } else {
                logger.debug("复制文件失败，目标文件 " + descFileName + " 已存在!");
                return false;
            }
        } else {
            if (!descFile.getParentFile().exists()) {
                // 如果目标文件所在的目录不存在，则创建目录
                logger.debug("目标文件所在的目录不存在，创建目录!");
                // 创建目标文件所在的目录
                if (!descFile.getParentFile().mkdirs()) {
                    logger.debug("创建目标文件所在的目录失败!");
                    return false;
                }
            }
        }

        // 准备复制文件
        // 读取的位数
        int readByte = 0;
        InputStream ins = null;
        OutputStream outs = null;
        try {
            // 打开源文件
            ins = new FileInputStream(srcFile);
            // 打开目标文件的输出流
            outs = new FileOutputStream(descFile);
            byte[] buf = new byte[1024];
            // 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
            while ((readByte = ins.read(buf)) != -1) {
                // 将读取的字节流写入到输出流
                outs.write(buf, 0, readByte);
            }
            logger.debug("复制单个文件 " + srcFileName + " 到" + descFileName
                    + "成功!");
            return true;
        } catch (Exception e) {
            logger.debug("复制文件失败：" + e.getMessage());
            return false;
        } finally {
            // 关闭输入输出流，首先关闭输出流，然后再关闭输入流
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException oute) {
                    oute.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ine) {
                    ine.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制同时删除源文件
     *
     * @param srcFileName
     * @param descFileName
     * @param coverlay
     * @return
     */
    public static boolean copyFileAndDelBefore(String srcFileName,
                                               String descFileName, boolean coverlay) {
        boolean isCopy = copyFileCover(srcFileName, descFileName, coverlay);
        delFile(srcFileName);
        return isCopy;
    }

    /**
     * 删除文件，可以删除单个文件或文件夹
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否是返回false
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            logger.debug(fileName + " 文件不存在!");
            return true;
        } else {
            System.gc();
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }


    /**
     * 删除单个文件
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.debug("删除文件 " + fileName + " 成功!");
                return true;
            } else {
                logger.debug("删除文件 " + fileName + " 失败!");
                return false;
            }
        } else {
            logger.debug(fileName + " 文件不存在!");
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dirName 被删除的目录所在的文件路径
     * @return 如果目录删除成功，则返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirName) {
        String dirNames = dirName;
        if (!dirNames.endsWith(File.separator)) {
            dirNames = dirNames + File.separator;
        }
        File dirFile = new File(dirNames);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            logger.debug(dirNames + " 目录不存在!");
            return true;
        }
        boolean flag = true;
        // 列出全部文件及子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                // 如果删除文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                // 如果删除子目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            logger.debug("删除目录失败!");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            logger.debug("删除目录 " + dirName + " 成功!");
            return true;
        } else {
            logger.debug("删除目录 " + dirName + " 失败!");
            return false;
        }

    }


    /**
     * 计算图片md5
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String file2Base64(String filePath) throws IOException {
        return file2Base64(new File(filePath));
    }

    /**
     * 计算图片md5
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String file2Base64(File file) throws IOException {
        return file2Base64(new FileInputStream(file));
    }

    /**
     * 计算图片md5
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String file2Base64(InputStream inputStream) throws IOException {
        Base64 base64 = new Base64();
        String md5Hex = DigestUtils.md5Hex(inputStream);
        byte[] aaa = new byte[16];
        for (int i = 0; i < md5Hex.toCharArray().length; i += 2) {
            String substring = md5Hex.substring(i, i + 2);
            byte b = (byte) Integer.parseInt(substring, 16);
            aaa[i / 2] = b;
        }
        String s = base64.encodeAsString(aaa);
        return s.replace("+", "-").replace("/", "_");
    }


    /**
     * 读取网络地址图片流
     *
     * @param url 请求地址
     * @return
     */
    public static byte[] getImageStream(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 1024;
                byte tmp[] = new byte[len];
                int i;
                while ((i = inputStream.read(tmp, 0, len)) > 0) {
                    baos.write(tmp, 0, i);
                }
                byte imgs[] = baos.toByteArray();
                return imgs;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 创建目录
     *
     * @param ubuntuPath
     */
    public static void mkdir(String ubuntuPath) {
        File file = new File(ubuntuPath);
        if (!file.exists()) {
            logger.info(ubuntuPath + "路径不存在 开始创建");
            file.mkdirs();
        }
    }

    /**
     * 获取图片分辨率（文件宽和高）
     *
     * @param absolutelyFileName
     * @return
     */
    public static String getFileResolution(String absolutelyFileName) {
        File file = new File(absolutelyFileName);
        if (file.exists() && file.isFile()) {
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            String photoResolution = width + "*" + height;
            return photoResolution;
        }
        return null;
    }

    /**
     * 压缩文件或目录
     *
     * @param srcDirName   压缩的根目录
     * @param fileName     根目录下的待压缩的文件名或文件夹名，其中*或""表示跟目录下的全部文件
     * @param descFileName 目标zip文件
     */
    public static boolean zipFiles(String srcDirName, String fileName,
                                   String descFileName) {
        // 判断目录是否存在
        if (srcDirName == null) {
            logger.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
            return false;
        }
        File fileDir = new File(srcDirName);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            logger.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
            return false;
        }
        String dirPath = fileDir.getAbsolutePath();
        File descFile = new File(descFileName);
        try {
            ZipOutputStream zouts = new ZipOutputStream(new FileOutputStream(descFile));
            if ("*".equals(fileName) || "".equals(fileName)) {
                zipDirectoryToZipFile(dirPath, fileDir, zouts);
            } else {
                File file = new File(fileDir, fileName);
                if (file.isFile()) {
                    return zipFilesToZipFile(dirPath, file, zouts);
                } else {
                    return zipDirectoryToZipFile(dirPath, file, zouts);
                }
            }
            zouts.close();
            logger.debug(descFileName + " 文件压缩成功!");
            return true;
        } catch (Exception e) {
            logger.debug("文件压缩失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将目录压缩到ZIP输出流
     *
     * @param dirPath 目录路径
     * @param fileDir 文件信息
     * @param zouts   输出流
     */
    public static boolean zipDirectoryToZipFile(String dirPath, File fileDir, ZipOutputStream zouts) {
        if (!fileDir.isDirectory()) {
            logger.error("指定路径不是一个有效目录");
        }
        File[] files = fileDir.listFiles();
        // 空的文件夹
        if (files.length == 0) {
            // 目录信息
            ZipEntry entry = new ZipEntry(getEntryName(dirPath, fileDir));
            try {
                zouts.putNextEntry(entry);
                zouts.closeEntry();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // 如果是文件，则调用文件压缩方法
                if (!zipFilesToZipFile(dirPath, files[i], zouts)) {
                    logger.error("压缩目录下文件失败，失败文件下标" + i + ",根目录地址：" + dirPath + ",文件名称：" + files[i].getName());
                    return false;
                }
            } else {
                // 如果是目录，则递归调用
                if (!zipDirectoryToZipFile(dirPath, files[i], zouts)) {
                    logger.error("压缩目录下目录失败，失败文件下标" + i + ",根目录地址：" + dirPath + ",文件名称：" + files[i].getName());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取待压缩文件在ZIP文件中entry的名字，即相对于跟目录的相对路径名
     *
     * @param dirPath 目录名
     * @param file    entry文件名
     * @return
     */
    private static String getEntryName(String dirPath, File file) {
        String dirPaths = dirPath;
        if (!dirPaths.endsWith(File.separator)) {
            dirPaths = dirPaths + File.separator;
        }
        String filePath = file.getAbsolutePath();
        // 对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储
        if (file.isDirectory()) {
            filePath += "/";
        }
        int index = filePath.indexOf(dirPaths);

        return filePath.substring(index + dirPaths.length());
    }

    /**
     * 将文件压缩到ZIP输出流
     *
     * @param dirPath 目录路径
     * @param file    文件
     * @param zouts   输出流
     */
    public static boolean zipFilesToZipFile(String dirPath, File file, ZipOutputStream zouts) {
        FileInputStream fin = null;
        ZipEntry entry = null;
        // 创建复制缓冲区
        byte[] buf = new byte[4096];
        int readByte = 0;
        if (!file.isFile()) {
            logger.error("该路径下不是文件");
            return false;
        }
        try {
            // 创建一个文件输入流
            fin = new FileInputStream(file);
            // 创建一个ZipEntry
            entry = new ZipEntry(getEntryName(dirPath, file));
            // 存储信息到压缩文件
            zouts.putNextEntry(entry);
            // 复制字节到压缩文件
            while ((readByte = fin.read(buf)) != -1) {
                zouts.write(buf, 0, readByte);
            }
            zouts.closeEntry();
            fin.close();
            logger.debug("添加文件 " + file.getAbsolutePath() + " 到zip文件中!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解压缩ZIP文件，将ZIP文件里的内容解压到descFileName目录下
     *
     * @param zipFilePath  需要解压的ZIP文件路径
     * @param descFilePath 目标文件路径
     */
    public static boolean unZipFiles(String zipFilePath, String descFilePath) {
        String descFileNames = descFilePath;
        if (!descFileNames.endsWith(File.separator)) {
            descFileNames = descFileNames + File.separator;
        }
        try {
            // 根据ZIP文件创建ZipFile对象
            ZipFile zipFile = new ZipFile(zipFilePath);
            ZipEntry entry = null;
            String entryName = null;
            String descFileDir = null;
            byte[] buf = new byte[4096];
            int readByte = 0;
            // 获取ZIP文件里所有的entry
            @SuppressWarnings("rawtypes")
            Enumeration enums = zipFile.getEntries();
            // 遍历所有entry
            while (enums.hasMoreElements()) {
                entry = (ZipEntry) enums.nextElement();
                // 获得entry的名字
                entryName = entry.getName();
                descFileDir = descFileNames + entryName;
                if (entry.isDirectory()) {
                    // 如果entry是一个目录，则创建目录
                    new File(descFileDir).mkdirs();
                    continue;
                } else {
                    // 如果entry是一个文件，则创建父目录
                    new File(descFileDir).getParentFile().mkdirs();
                }
                File file = new File(descFileDir);
                // 打开文件输出流
                OutputStream os = new FileOutputStream(file);
                // 从ZipFile对象中打开entry的输入流
                InputStream is = zipFile.getInputStream(entry);
                while ((readByte = is.read(buf)) != -1) {
                    os.write(buf, 0, readByte);
                }
                os.close();
                is.close();
            }
            zipFile.close();
            logger.debug("文件解压成功!");
            return true;
        } catch (Exception e) {
            logger.debug("文件解压失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 读取文本
     *
     * @param fileName
     * @return
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 读取文本
     *
     * @param inStream
     * @return
     */
    public static String readFileContentByInputStream(InputStream inStream) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(inStream));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 向文件追加内容
     *
     * @param content  写入的内容
     * @param fileName 文件
     */
    public static void writeFile(String content, String fileName) {
        // 在文件夹目录下新建文件
        File file = new File(fileName);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            if (!file.exists()) {
                boolean hasFile = file.createNewFile();
                if (hasFile) {
                    logger.info("file not exists, create new file");
                }
                fos = new FileOutputStream(file);
            } else {
                fos = new FileOutputStream(file, true);
            }
            osw = new OutputStreamWriter(fos, "utf-8");
            // 写入内容
            osw.write(content);
            // 换行
            osw.write("\r\n");
            logger.info("成功向文件 [{}] 写入内容：[{}]", fileName, content);
        } catch (Exception e) {
            logger.info("写入文件发生异常", e);
        } finally {
            // 关闭流
            try {
                if (osw != null) {
                    osw.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                logger.info("关闭流异常", e);
            }
        }
    }

    public static void main(String[] args) {
//        //读取网络地址流
//        byte[] imageStream = getImageStream("http://103.219.33.101/image-upload/20200928151601278762438.jpg");
//        //将流写入本地文件
//        String s = byteToImagePath(imageStream, "C:\\Users\\Administrator\\Desktop");

//        String fileResolution = getFileResolution("C:\\Users\\Administrator\\Desktop\\456.jpg");
//        System.out.println(fileResolution);
    }


}
