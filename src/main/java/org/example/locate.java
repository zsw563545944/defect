package org.example;




import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * @className: locate
 * @author: zhongshuw
 * @date: 2024/10/30 16:57
 * @Version: 1.0
 * @description:
 */
public class locate {


    public class WeldSeamSegmentation {


    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // 输入和输出文件夹路径
        String inputFolderPath = "C:\\Users\\zhongshuw\\Desktop\\24.7.29\\image";
        String outputFolderPath = "C:\\Users\\zhongshuw\\Desktop\\24.7.29\\output";

        // 创建输出文件夹，如果不存在则创建
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // 遍历输入文件夹中的所有图像文件
        File inputFolder = new File(inputFolderPath);
        for (File file : inputFolder.listFiles()) {
            if (file.isFile() && isImageFile(file)) {
                processImage(file, outputFolderPath);
            }
        }

        System.out.println("所有图像处理完成并保存到输出文件夹。");
    }

    private static boolean isImageFile(File file) {
        // 判断文件是否为支持的图像格式
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".bmp");
    }

    private static void processImage(File file, String outputFolderPath) {
        // 读取图像
        Mat image = Imgcodecs.imread(file.getAbsolutePath());
        if (image.empty()) {
            System.out.println("图像未加载：" + file.getName());
            return;
        }

        // 转换为灰度图并进行二值化处理
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 200, 255, Imgproc.THRESH_BINARY);

        // Canny 边缘检测
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 200);

        // 膨胀操作
        Mat dilated = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.dilate(edges, dilated, kernel);

        // 闭运算
        Mat closed = new Mat();
        Imgproc.morphologyEx(dilated, closed, Imgproc.MORPH_CLOSE, kernel);

        // 找到轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(closed, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 找到最大轮廓
        double maxArea = 0;
        Rect maxRect = null;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            double area = rect.area();
            if (area > maxArea) {
                maxArea = area;
                maxRect = rect;
            }
        }

        // 检查是否找到最大矩形
        if (maxRect != null) {
            // 扩展矩形
            int expandWidth = 30;
//            int x = Math.max(maxRect.x, 0);
            int x =  0;

            int y = Math.max(maxRect.y - expandWidth, 0);
            int width = maxRect.width;
            int height = maxRect.height + 2 * expandWidth;
            width =  image.cols() - x;
            height = Math.min(height, image.rows() - y);
            Rect expandedRect = new Rect(x, y, width, height);

            // 在原始图像上绘制扩展后的矩形
            Mat output = image.clone();
            Imgproc.rectangle(output, expandedRect, new Scalar(0, 255, 0), 2);

            // 裁剪扩展后的矩形区域
            Mat cropped = new Mat(image, expandedRect);

            // 保存结果，使用原文件名加后缀
            String baseFileName = file.getName().substring(0, file.getName().lastIndexOf("."));
            Imgcodecs.imwrite(outputFolderPath + "\\" + baseFileName + "_with_rectangle.jpg", output);
            Imgcodecs.imwrite(outputFolderPath + "\\" + baseFileName + "_cropped.jpg", cropped);
            Imgcodecs.imwrite(outputFolderPath + "\\" + baseFileName + "_edges.jpg", edges);
            System.out.println("处理并保存图像：" + file.getName());
        } else {
            System.out.println("未找到轮廓：" + file.getName());
        }
    }


//    public static void main(String[] args) {
////        // 加载 OpenCV 库
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        // 读取图像
////        Mat image = Imgcodecs.imread("C:\\Users\\zhongshuw\\Desktop\\24.7.29\\image\\Image010.bmp");
////        if (image.empty()) {
////            System.out.println("图像未加载");
////            return;
////        }
////
////        // 转换为灰度图
////        Mat gray = new Mat();
////        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
//////        Imgcodecs.imwrite("weld_seam_segmentation_resultgray.jpg", gray);
////
////        // 二值化
////        Mat binary = new Mat();
////        Imgproc.threshold(gray, binary, 127, 255, Imgproc.THRESH_BINARY);
//////        Imgcodecs.imwrite("weld_seam_segmentation_resultbinary.jpg", binary);
//////
//////        // 高斯滤波
//////        Mat blurred = new Mat();
//////        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);
////
////        // Canny 边缘检测
////        Mat edges = new Mat();
////        Imgproc.Canny(binary, edges, 50, 150);
//////        Imgcodecs.imwrite("weld_seam_segmentation_resultedges.jpg", edges);
////
////        // 膨胀操作
////        Mat dilated = new Mat();
////        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
////        Imgproc.dilate(edges, dilated, kernel);
//////        Imgcodecs.imwrite("weld_seam_segmentation_resultdilated.jpg", dilated);
////        // 闭运算
////        Mat closed = new Mat();
////        Imgproc.morphologyEx(dilated, closed, Imgproc.MORPH_CLOSE, kernel);
//////        Imgcodecs.imwrite("weld_seam_segmentation_resultkernel.jpg", dilated);
////
////        // 找到轮廓
////        List<MatOfPoint> contours = new ArrayList<>();
////        Imgproc.findContours(closed, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
////
////        // 找到最大轮廓
////        double maxArea = 0;
////        Rect maxRect = null;
////        for (MatOfPoint contour : contours) {
////            Rect rect = Imgproc.boundingRect(contour);
////            double area = rect.area();
////            if (area > maxArea) {
////                maxArea = area;
////                maxRect = rect;
////            }
////        }
////
////        // 检查是否找到最大矩形
////        if (maxRect != null) {
////            // 扩展矩形
////            int expandWidth = 30;
////            int x = Math.max(maxRect.x, 0);
////            int y = Math.max(maxRect.y - expandWidth, 0);
////            int width = maxRect.width;
////            int height = maxRect.height + 2 * expandWidth;
////
////            // 确保不超出图像边界
////            width = Math.min(width, image.cols() - x);
////            height = Math.min(height, image.rows() - y);
////
////            Rect expandedRect = new Rect(x, y, width, height);
////
////            // 在原始图像上绘制扩展后的矩形
////            Mat output = image.clone();
////            Imgproc.rectangle(output, expandedRect, new Scalar(0, 255, 0), 2);
////
////            // 裁剪扩展后的矩形区域
////            Mat cropped = new Mat(image, expandedRect);
////
////            // 保存和显示结果
////            Imgcodecs.imwrite("output_with_rectangle.jpg", output);
////            Imgcodecs.imwrite("cropped_image.jpg", cropped);
////
////            System.out.println("最大轮廓的扩展矩形已绘制，裁剪后的图像已保存为 cropped_image.jpg");
////        } else {
////            System.out.println("未找到轮廓");
////        }
////        // 绘制轮廓
////        Mat output = image.clone();
////        Imgproc.drawContours(output, contours, -1, new Scalar(0, 255, 0), 2);
////
////        // 保存和显示结果
////        Imgcodecs.imwrite("weld_seam_segmentation_result.jpg", output);
////        System.out.println("焊缝分割结果已保存");
//    }
}
