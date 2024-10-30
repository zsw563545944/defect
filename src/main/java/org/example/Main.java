package org.example;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.DownloadUtils;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @className: ${NAME}
 * @author: zhongshuw
 * @date: 2024/10/30 14:32
 * @Version: 1.0
 * @description:
 */
public class Main {
    public static void main(String[] args) throws IOException, ModelNotFoundException, MalformedModelException, TranslateException {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(256))
//                .add(new CenterCrop(224, 224))
                .add(new ToTensor())
                .add(new Normalize(
                        new float[]{0.485f, 0.456f, 0.406f},
                        new float[]{0.229f, 0.224f, 0.225f}));
//        这段代码创建了一个图像预处理管道，它包括以下步骤：
//
//        Resize(256): 将图像大小调整为 256x256 像素。
//        CenterCrop(224, 224): 从图像中心裁剪出 224x224 像素的大小。
//        ToTensor(): 将图像转换为 PyTorch 张量。
//        Normalize(...): 对图像进行标准化处理。
//        Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
//                .setPipeline(pipeline)
//                .optApplySoftmax(true)
//                .build();
        Translator<Image, Segmentation> translator = new Translator<Image, Segmentation>() {

            @Override
            public NDList processInput(TranslatorContext ctx, Image input) {
                return new NDList(input.toNDArray(ctx.getNDManager()).div(255f));
            }

            @Override
            public Segmentation processOutput(TranslatorContext ctx, NDList list) {
                NDArray mask = list.singletonOrThrow().argMax(0); // 取最高概率的类别ID
                return new Segmentation(mask);
            }

            @Override
            public Batchifier getBatchifier() {
                return null;
            }
        };

//        这段代码创建了一个将图像转换为分类标签的翻译器。它使用上面创建的预处理管道，并应用 softmax 函数来输出概率分布。
        System.setProperty("ai.djl.repository.zoo.location", "build/pytorch_models/defect");
//        Criteria<Image, Classifications> criteria = Criteria.builder()
//                .setTypes(Image.class, Classifications.class)
//                // only search the model in local directory
//                // "ai.djl.localmodelzoo:{name of the model}"
//                .optArtifactId("ai.djl.localmodelzoo:epoch_649")
//                .optTranslator(translator)
//                .optProgress(new ProgressBar()).build();
        Criteria<Image, Segmentation> criteria = Criteria.builder()
                .setTypes(Image.class, Segmentation.class)
                .optArtifactId("ai.djl.localmodelzoo:epoch_649")
                .optTranslator(translator)
                .optProgress(new ProgressBar())
                .build();
//        这段代码定义了加载模型的条件。它指定了模型的类型（图像输入，分类输出），模型的位置（本地路径），使用的翻译器，以及是否显示进度条。
//        ZooModel model = ModelZoo.loadModel(criteria);
////        这行代码使用上面定义的标准从模型仓库加载模型。
//
//
//        // 自己本地
////        File fs=new File("C:\\Users\\zhongshuw\\Desktop\\th.jpg");
//
//        Image img = ImageFactory.getInstance().fromInputStream(new FileInputStream(fs));
//        Predictor<Image, Classifications> predictor = model.newPredictor();
//        Classifications classifications = predictor.predict(img);
//        System.out.println(classifications);
//        这段代码加载了一张图像，使用加载的模型进行预测，并打印出预测结果
        ZooModel<Image, Segmentation> model = ModelZoo.loadModel(criteria);

        File fs=new File("        C:\\Users\\zhongshuw\\Documents\\WeChat Files\\wxid_p577pm88yzml22\\FileStorage\\File\\2024-10\\24.7.29采集图片\\image\\Image010.bmp");
        Image img = ImageFactory.getInstance().fromInputStream(new FileInputStream(fs));

        Predictor<Image, Segmentation> predictor = model.newPredictor();
        Segmentation segmentation = predictor.predict(img);

        System.out.println(segmentation); // 打印分割结果

    }
}