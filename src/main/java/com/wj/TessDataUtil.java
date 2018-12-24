package com.wj;

import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date 2018/12/24
 **/
public class TessDataUtil {

    public final static String ENG = "eng";
    public final static String CH = "chi_sim";
    public final static  String MIX = "eng+chi_sim";

    /**
     * 从网络获取图片解析为字符串
     * @param imageUrl
     * @param language
     * @param tessDataPath 训练路径
     * @return
     */
    public static String parseNetImageToString(String imageUrl, String language, String tessDataPath) throws ParseImageException {
        InputStream in = null;
        try {
            URL url = new URL(imageUrl);
            in = url.openStream();
            Tesseract tesseract = new Tesseract();
            if (language == null || language.length() == 0) {
                language = MIX;
            }
            tesseract.setLanguage(language);
            if (tessDataPath == null || tessDataPath.length() == 0) {
                throw new NullPointerException("tessDataPath can not be empty");
            }
            tesseract.setDatapath(tessDataPath);
            BufferedImage image = ImageIO.read(in);
            String result = tesseract.doOCR(image);
            return result;
        }
        catch (Exception e) {
            throw new ParseImageException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从本地获取图片解析为字符串
     * @param imagePath
     * @param language
     * @param tessDataPath 训练路径
     * @return
     */
    public static String parseLocalImageToString(String imagePath, String language, String tessDataPath) throws ParseImageException {
        InputStream in = null;
        try {
            File file = new File(imagePath);
            in = new  FileInputStream(file);
            Tesseract tesseract = new Tesseract();
            if (language == null || language.length() == 0) {
                language = MIX;
            }
            tesseract.setLanguage(language);
            if (tessDataPath == null || tessDataPath.length() == 0) {
                throw new NullPointerException("tessDataPath can not be empty");
            }
            tesseract.setDatapath(tessDataPath);
            BufferedImage image = ImageIO.read(in);
            String result = tesseract.doOCR(image);
            return result;
        }
        catch (Exception e) {
            throw new ParseImageException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) throws Exception {
        String imageUrl = "http://es.bnuz.edu.cn/checkcode.aspx?0.33556625493951997/";
        String imagePath = "F:\\checkcode.gif";
        String tessDataPath = "F:\\tessdata";
        String result = parseNetImageToString(imageUrl, ENG, tessDataPath);
        System.out.println(result);
    }
}
