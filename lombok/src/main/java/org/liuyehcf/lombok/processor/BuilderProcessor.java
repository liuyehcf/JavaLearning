package org.liuyehcf.lombok.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import org.liuyehcf.lombok.annotation.Builder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("org.liuyehcf.lombok.annotation.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {

    private static final String IDENTIFIER_THIS = "this";

    private static final String IDENTIFIER_DATA = "data";

    private static final String IDENTIFIER_SET = "set";

    /**
     * 用于在编译器打印消息的组件
     */
    private Messager messager;

    /**
     * 语法树
     */
    private JavacTrees trees;

    /**
     * 用来构造语法树节点
     */
    private TreeMaker treeMaker;

    /**
     * 创建标识符的方法
     */
    private Names names;

    /**
     * 插入式注解处理器的处理逻辑
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 首先获取被Builder注解标记的元素
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Builder.class);

        set.forEach(element -> {

            // 获取当前元素的JCTree对象
            JCTree jcTree = trees.getTree(element);

            // JCTree利用的是访问者模式，将数据与数据的处理进行解耦，TreeTranslator就是访问者，这里我们重写访问类时的逻辑
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {

                    // 为当前jcClassDecl添加JCTree节点
                    jcClassDecl.defs = jcClassDecl.defs.append(
                            // 创建了一个静态内部类作为一个Builder
                            createJCClassDecl(
                                    jcClassDecl,
                                    getSetJCMethodDecls(jcClassDecl)
                            )
                    );

//                    messager.printMessage(Diagnostic.Kind.WARNING, jcClassDecl.toString());
                }
            });
        });

        return true;
    }

    /**
     * 提取出所有set方法
     *
     * @param jcClassDecl
     * @return
     */
    private List<JCTree.JCMethodDecl> getSetJCMethodDecls(JCTree.JCClassDecl jcClassDecl) {
        List<JCTree.JCMethodDecl> setJCMethodDecls = List.nil();

        // 遍历jcClassDecl的所有内部节点，可能是字段，方法等等
        for (JCTree jTree : jcClassDecl.defs) {
            // 找出所有set方法节点，并添加
            if (isSetJCMethodDecl(jTree)) {
                // 注意这个com.sun.tools.javac.util.List的用法，不支持链式操作，更改后必须赋值
                setJCMethodDecls = setJCMethodDecls.prepend((JCTree.JCMethodDecl) jTree);
            }
        }

        return setJCMethodDecls;
    }

    /**
     * 判断是否为set方法
     *
     * @param jTree
     * @return
     */
    private boolean isSetJCMethodDecl(JCTree jTree) {
        if (jTree.getKind().equals(JCTree.Kind.METHOD)) {
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) jTree;
            if (jcMethodDecl.getName().startsWith(getNameFromString(IDENTIFIER_SET))
                    && jcMethodDecl.getParameters().size() == 1) {
                return true;
            }
        }
        return false;
    }

    private Name getNameFromString(String name) {
        return names.fromString(name);
    }

    /**
     * 创建一个语法树节点，其类型为JCClassDecl。作为Builder模式中的Builder类
     *
     * @param jcClassDecl
     * @param jcMethodDecls
     * @return
     */
    private JCTree.JCClassDecl createJCClassDecl(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCMethodDecl> jcMethodDecls) {

        List<JCTree> jcTrees = List.nil();

        JCTree.JCVariableDecl jcVariableDecl = createDataField(jcClassDecl);
        jcTrees = jcTrees.append(jcVariableDecl);
        jcTrees = jcTrees.appendList(createSetJCMethodDecls(jcClassDecl, jcMethodDecls, jcVariableDecl));

        return treeMaker.ClassDef(
                treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC + Flags.FINAL), // 访问标志
                getNameFromString(jcClassDecl.getSimpleName().toString() + "Builder"), // 名字
                List.nil(), // 泛型形参列表
                null, // 继承
                List.nil(), // 接口列表
                jcTrees); // 定义
    }

    /**
     * 创建一个语法树节点，其类型为JCVariableDecl。作为Builder模式中被Build的对象
     *
     * @param jcClassDecl
     * @return
     */
    private JCTree.JCVariableDecl createDataField(JCTree.JCClassDecl jcClassDecl) {
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE), // 访问标志
                getNameFromString(IDENTIFIER_DATA), // 名字
                treeMaker.Ident(getNameFromString(jcClassDecl.getSimpleName().toString())), // 类型
                null // 初始化表达式
        );
    }

    /**
     * 创建一些语法树节点，其类型为JCMethodDecl。作为Builder模式中的setXXX方法
     *
     * @param jcClassDecl
     * @param methodDecls
     * @param jcVariableDecl
     * @return
     */
    private List<JCTree> createSetJCMethodDecls(JCTree.JCClassDecl jcClassDecl, List<JCTree.JCMethodDecl> methodDecls, JCTree.JCVariableDecl jcVariableDecl) {
        List<JCTree> clonedSetMethods = List.nil();

        for (JCTree.JCMethodDecl jcMethodDecl : methodDecls) {
            clonedSetMethods = clonedSetMethods.append(createSetJCMethodDecl(jcClassDecl, jcMethodDecl));
        }

        return clonedSetMethods;
    }

    /**
     * 创建一个语法树节点，其类型为JCMethodDecl。作为Builder模式中的setXXX方法
     *
     * @param jcClassDecl
     * @param jcMethodDecl
     * @return
     */
    private JCTree.JCMethodDecl createSetJCMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCMethodDecl jcMethodDecl) {
        JCTree.JCVariableDecl jcVariableDecl = jcMethodDecl.getParameters().get(0);

        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加调用语句" data.setXXX(xxx); "
        jcStatements.append(
                treeMaker.Exec(
                        treeMaker.Apply(
                                List.nil(),
                                treeMaker.Select(
                                        treeMaker.Ident(getNameFromString(IDENTIFIER_DATA)),
                                        jcMethodDecl.getName()
                                ),
                                List.of(treeMaker.Ident(jcVariableDecl.getName()))
                        )
                )
        );

        // 添加Builder模式中的返回语句 " return this; "
        jcStatements.append(
                treeMaker.Return(
                        treeMaker.Ident(getNameFromString(IDENTIFIER_THIS)
                        )
                )
        );

        // 转换成代码块
        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , jcStatements.toList() // 所有的语句
        );


        return treeMaker.MethodDef(
                jcMethodDecl.getModifiers(), // 访问标志
                jcMethodDecl.getName(), // 名字
                treeMaker.Ident(names.fromString(jcClassDecl.getSimpleName().toString() + "Builder")), //返回类型
                jcMethodDecl.getTypeParameters(), // 泛型形参列表
                List.of(copyJCVariableDecl(jcVariableDecl)), // 参数列表，这里必须创建一个新的JCVariableDecl，否则注解处理时就会抛异常，原因目前还不清楚
                jcMethodDecl.getThrows(), // 异常列表
                jcBlock, // 方法体
                null // 默认值
        );
    }

    /**
     * 克隆一个JCVariableDecl语法树节点
     * 我觉得TreeMaker.MethodDef()方法需要克隆参数列表的原因是：从JCMethodDecl拿到的JCVariableDecl会与这个JCMethodDecl有关联，因此需要创建一个与该JCMethodDecl无关的语法树节点（JCVariableDecl）
     *
     * @param prototypeJCVariableDecl
     * @return
     */
    private JCTree.JCVariableDecl copyJCVariableDecl(JCTree.JCVariableDecl prototypeJCVariableDecl) {
        return treeMaker.VarDef(prototypeJCVariableDecl.sym, prototypeJCVariableDecl.getNameExpression());
    }

    /**
     * 获取一些注解处理器执行处理逻辑时需要用到的一些关键对象
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }
}
