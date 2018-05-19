package org.liuyehcf.annotation.source.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import org.liuyehcf.annotation.source.annotation.NoArgsConstructor;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("org.liuyehcf.annotation.source.annotation.NoArgsConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NoArgsConstructorProcessor extends BaseProcessor {

    /**
     * 原始类名
     */
    private Name className;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 首先获取被Builder注解标记的元素
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(NoArgsConstructor.class);

        set.forEach(element -> {

            // 获取当前元素的JCTree对象
            JCTree jcTree = trees.getTree(element);

            // JCTree利用的是访问者模式，将数据与数据的处理进行解耦，TreeTranslator就是访问者，这里我们重写访问类时的逻辑
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClass) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "@NoArgsConstructor process [" + jcClass.getSimpleName().toString() + "] begin!");

                    before(jcClass);

                    // 添加全参构造方法
                    jcClass.defs = jcClass.defs.append(
                            createNoArgsConstructor()
                    );

                    messager.printMessage(Diagnostic.Kind.NOTE, "@NoArgsConstructor process [" + jcClass.getSimpleName().toString() + "] end!");
                }
            });
        });

        return true;
    }

    /**
     * 进行一些预处理
     *
     * @param jcClass 原始类的语法树节点
     */
    private void before(JCTree.JCClassDecl jcClass) {
        this.className = names.fromString(jcClass.getSimpleName().toString());
    }

    /**
     * 创建无参数构造方法
     *
     * @return 无参构造方法语法树节点
     */
    private JCTree.JCMethodDecl createNoArgsConstructor() {
        // 转换成代码块
        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , List.nil() // 所有的语句
        );

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC), // 访问标志
                className, // 名字
                null, //返回类型
                List.nil(), // 泛型形参列表
                List.nil(), // 参数列表，这里必须创建一个新的JCVariable，否则注解处理时就会抛异常，原因目前还不清楚
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }
}
