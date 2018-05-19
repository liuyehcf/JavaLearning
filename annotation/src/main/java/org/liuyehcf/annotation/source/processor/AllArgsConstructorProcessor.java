package org.liuyehcf.annotation.source.processor;

import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("org.liuyehcf.annotation.source.annotation.AllArgsConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AllArgsConstructorProcessor extends BaseProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        // 首先获取被Builder注解标记的元素
//        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(AllArgsConstructor.class);
//
//        set.forEach(element -> {
//
//            // 获取当前元素的JCTree对象
//            JCTree jcTree = trees.getTree(element);
//
//            // JCTree利用的是访问者模式，将数据与数据的处理进行解耦，TreeTranslator就是访问者，这里我们重写访问类时的逻辑
//            jcTree.accept(new TreeTranslator() {
//                @Override
//                public void visitClassDef(JCTree.JCClassDecl jcClass) {
//                    messager.printMessage(Diagnostic.Kind.NOTE, "process class [" + jcClass.getSimpleName().toString() + "], start");
//
//                    // 添加全参构造方法
//                    jcClass.defs = jcClass.defs.append(
//                            createAllArgsConstructor()
//                    );
//
//                    messager.printMessage(Diagnostic.Kind.NOTE, "process class [" + jcClass.getSimpleName().toString() + "], end");
//                }
//            });
//        });
//
//        return true;

        return false;
    }

    /**
     * 创建全参数构造方法
     *
     * @return 全参构造方法语法树节点
     */
    private JCTree.JCMethodDecl createAllArgsConstructor() {
//        List<JCTree.JCVariableDecl> jcVariables = List.nil();
//
//        for (JCTree.JCMethodDecl jcMethod : setJCMethods) {
//            jcVariables = jcVariables.append(jcMethod.getParameters().get(0));
//        }
//
//
//        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
//        for (JCTree.JCMethodDecl jcMethod : setJCMethods) {
//            // 添加构造方法的赋值语句 " this.xxx = xxx; "
//            jcStatements.append(
//                    treeMaker.Exec(
//                            treeMaker.Assign(
//                                    treeMaker.Select(
//                                            treeMaker.Ident(names.fromString(THIS)),
//                                            getNameFromSetJCMethod(jcMethod)
//                                    ),
//                                    treeMaker.Ident(getNameFromSetJCMethod(jcMethod))
//                            )
//                    )
//            );
//        }
//
//        // 转换成代码块
//        JCTree.JCBlock jcBlock = treeMaker.Block(
//                0 // 访问标志
//                , jcStatements.toList() // 所有的语句
//        );
//
//        return treeMaker.MethodDef(
//                treeMaker.Modifiers(Flags.PUBLIC), // 访问标志
//                className, // 名字
//                null, //返回类型
//                List.nil(), // 泛型形参列表
//                jcVariables, // 参数列表，这里必须创建一个新的JCVariable，否则注解处理时就会抛异常，原因目前还不清楚
//                List.nil(), // 异常列表
//                jcBlock, // 方法体
//                null // 默认方法（可能是interface中的那个default）
//        );
        return null;
    }

}
