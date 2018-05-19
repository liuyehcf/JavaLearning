package org.liuyehcf.annotation.source.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import org.liuyehcf.annotation.source.annotation.AllArgsConstructor;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("org.liuyehcf.annotation.source.annotation.AllArgsConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AllArgsConstructorProcessor extends BaseProcessor {
    private static final String THIS = "this";

    private static final String SET = "set";


    /**
     * 原始类名
     */
    private Name className;

    /**
     * 原始类的set方法的语法树节点集合
     */
    private List<JCTree.JCMethodDecl> setJCMethods;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 首先获取被Builder注解标记的元素
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(AllArgsConstructor.class);

        set.forEach(element -> {

            // 获取当前元素的JCTree对象
            JCTree jcTree = trees.getTree(element);

            // JCTree利用的是访问者模式，将数据与数据的处理进行解耦，TreeTranslator就是访问者，这里我们重写访问类时的逻辑
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClass) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "process class [" + jcClass.name.toString() + "], start");

                    // 进行一些初始化操作
                    before(jcClass);

                    // 添加全参构造方法
                    jcClass.defs = jcClass.defs.append(
                            createAllArgsConstructor()
                    );

                    messager.printMessage(Diagnostic.Kind.NOTE, "process class [" + jcClass.name.toString() + "], end");
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
        this.className = names.fromString(jcClass.name.toString());
        this.setJCMethods = getSetJCMethods(jcClass);
    }

    /**
     * 提取出所有set方法的语法树节点
     *
     * @param jcClass 原始类的语法树节点
     * @return set方法的语法树节点的集合
     */
    private List<JCTree.JCMethodDecl> getSetJCMethods(JCTree.JCClassDecl jcClass) {
        List<JCTree.JCMethodDecl> setJCMethods = List.nil();

        // 遍历jcClass的所有内部节点，可能是字段，方法等等
        for (JCTree jTree : jcClass.defs) {
            // 找出所有set方法节点，并添加
            if (isSetJCMethod(jTree)) {
                // 注意这个com.sun.tools.javac.util.List的用法，不支持链式操作，更改后必须赋值
                setJCMethods = setJCMethods.append((JCTree.JCMethodDecl) jTree);
            }
        }

        return setJCMethods;
    }


    /**
     * 判断是否为set方法
     *
     * @param jTree 原始类的语法树节点
     * @return 判断是否是Set方法
     */
    private boolean isSetJCMethod(JCTree jTree) {
        if (jTree.getKind().equals(JCTree.Kind.METHOD)) {
            JCTree.JCMethodDecl jcMethod = (JCTree.JCMethodDecl) jTree;
            return jcMethod.name.startsWith(names.fromString(SET))
                    && jcMethod.params.size() == 1;
        }
        return false;
    }

    /**
     * 创建全参数构造方法
     *
     * @return 全参构造方法语法树节点
     */
    private JCTree.JCMethodDecl createAllArgsConstructor() {
        List<JCTree.JCVariableDecl> jcVariables = List.nil();

        for (JCTree.JCMethodDecl jcMethod : setJCMethods) {
            jcVariables = jcVariables.append(jcMethod.params.get(0));
        }


        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        for (JCTree.JCMethodDecl jcMethod : setJCMethods) {
            // 添加构造方法的赋值语句 " this.xxx = xxx; "
            jcStatements.append(
                    treeMaker.Exec(
                            treeMaker.Assign(
                                    treeMaker.Select(
                                            treeMaker.Ident(names.fromString(THIS)),
                                            getNameFromSetJCMethod(jcMethod)
                                    ),
                                    treeMaker.Ident(getNameFromSetJCMethod(jcMethod))
                            )
                    )
            );
        }

        // 转换成代码块
        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , jcStatements.toList() // 所有的语句
        );

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC), // 访问标志
                names.fromString("<init>"), // 名字
                null, //返回类型
                List.nil(), // 泛型形参列表
                jcVariables, // 参数列表，这里必须创建一个新的JCVariable，否则注解处理时就会抛异常，原因目前还不清楚
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }

    private Name getNameFromSetJCMethod(JCTree.JCMethodDecl jcMethod) {
        String s = jcMethod.name.toString();
        return names.fromString(s.substring(3, 4).toLowerCase() + s.substring(4));
    }
}
