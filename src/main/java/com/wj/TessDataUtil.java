package com.wj;

import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date 2018/12/24
 **/
public class TessDataUtil {

    public final static String ENG = "eng";
    public final static String CH = "chi_sim";
    public final static  String MIX = "eng+chi_sim";

    private final static String HTTP_PREFIX = "http";
    private final static String HTTPS_PREFIX = "https";

    /**
     * 从网络获取图片解析为字符串
     * @param imageUrl
     * @param language
     * @param tessDataPath 训练路径
     * @return
     */
    public static String parseNetImageToString(String imageUrl, String language, String tessDataPath) throws ParseImageException {
        if (imageUrl == null || imageUrl.length() == 0) {
            throw new NullPointerException("imageUrl can not be empty");
        }
        if (tessDataPath == null || tessDataPath.length() == 0) {
            throw new NullPointerException("tessDataPath can not be empty");
        }
        if (!imageUrl.startsWith(HTTP_PREFIX) && imageUrl.startsWith(HTTPS_PREFIX)) {
            throw  new ParseImageException("imageUrl must be start with" + HTTP_PREFIX + " or" + HTTPS_PREFIX);
        }
        InputStream in = null;
        try {
            URL url = new URL(imageUrl);
            in = url.openStream();
            Tesseract tesseract = new Tesseract();
            if (language == null || language.length() == 0) {
                language = MIX;
            }
            tesseract.setLanguage(language);
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

        java.util.List<String> imagePathList = Arrays.asList(
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif",
                "F:\\checkcode.gif"
        );


        long start = System.currentTimeMillis();
        for (String path: imagePathList) {
            String result = parseLocalImageToString(imagePath, ENG, tessDataPath);
            //System.out.println(result);
        }
        System.out.println("耗时:" + String.valueOf(System.currentTimeMillis()-start));

        long start1 = System.currentTimeMillis();
        java.util.List<String> results = imagePathList.stream().map(path-> {
                    try {
                        String result = parseLocalImageToString(path, ENG, tessDataPath);
                        return result;
                    } catch (ParseImageException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        ).collect(Collectors.toList());
        for (String result: results) {
            //System.out.println(result);
        }
        System.out.println("stream耗时:" + String.valueOf(System.currentTimeMillis()-start1));

        long start2 = System.currentTimeMillis();
        //Executor executor = Executors.newCachedThreadPool();
        Executor  executor = Executors.newFixedThreadPool(4);
        java.util.List<CompletableFuture<String>> codeFutures = imagePathList.stream().map(path->CompletableFuture.supplyAsync(()->{
            try {
                String result = parseLocalImageToString(path, ENG, tessDataPath);
                return result;
            } catch (ParseImageException e) {
                throw new RuntimeException(e);
            }
        }, executor)).collect(Collectors.toList());
        CompletableFuture<String> completableFuture = codeFutures.get(0);
        System.out.println(completableFuture.toString());
        System.out.println("CompletableFuture耗时:" + String.valueOf(System.currentTimeMillis()-start1));
        codeFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("CompletableFuture耗时:" + String.valueOf(System.currentTimeMillis()-start1));
    }
}
