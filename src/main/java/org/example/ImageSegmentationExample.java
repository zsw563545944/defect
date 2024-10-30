//package org.example;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//public class ImageSegmentationExample {
//
//    public static void main(String[] args) throws IOException, ModelException, TranslateException {
//        // 加载模型
//        Model model = Model.newInstance("your-model-path", Application.CV.IMAGE_SEGMENTATION);
//        Translator<Image, Segmentation> translator = new MySegmentationTranslator();
//        Predictor<Image, Segmentation> predictor = model.newPredictor(translator);
//
//        // 加载图像
//        Image img = ImageFactory.getInstance().fromInputStream(new FileInputStream("path/to/your/image.jpg"));
//
//        // 进行分割预测
//        Segmentation segmentation = predictor.predict(img);
//
//        // 保存分割掩码
//        BufferedImage maskImage = createMaskImage(segmentation);
//        ImageIO.write(maskImage, "png", new File("output-mask.png"));
//    }
//
//    // 根据分割输出创建掩码图像
//    private static BufferedImage createMaskImage(Segmentation segmentation) {
//        int width = segmentation.getMaskWidth();
//        int height = segmentation.getMaskHeight();
//        BufferedImage maskImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int classId = segmentation.getClass(x, y);
//                maskImage.setRGB(x, y, classToColor(classId));
//            }
//        }
//        return maskImage;
//    }
//
//    // 将类别ID映射到颜色
//    private static int classToColor(int classId) {
//        switch (classId) {
//            case 0: return 0x000000;   // 背景黑色
//            case 1: return 0xFF0000;   // 类别1红色
//            case 2: return 0x00FF00;   // 类别2绿色
//            // 根据需要添加其他颜色映射
//            default: return 0xFFFFFF;  // 默认白色
//        }
//    }
//}
//
//class MySegmentationTranslator implements Translator<Image, Segmentation> {
//
//    @Override
//    public NDList processInput(TranslatorContext ctx, Image input) {
//        NDArray array = input.toNDArray(ctx.getNDManager()).div(255f);
//        return new NDList(array);
//    }
//
//    @Override
//    public Segmentation processOutput(TranslatorContext ctx, NDList list) {
//        NDArray mask = list.singletonOrThrow().argMax(0);
//        return new Segmentation(mask);
//    }
//
//    @Override
//    public Batchifier getBatchifier() {
//        return null; // 不使用批处理
//    }
//}