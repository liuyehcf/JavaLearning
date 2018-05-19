package org.liuyehcf.annotation.source;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("org.liuyehcf.annotation.source.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {

    private static final String THIS = "this";

    private static final String DATA = "data";

    private static final String SET = "set";

    /**
     * 创建建造者的静态方法名
     */
    private static final String BUILDER_STATIC_METHOD_NAME = "builder";

    /**
     * 建造方法名
     */
    private static final String BUILD_METHOD_NAME = "build";

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
     * 用于创建标识符的对象
     */
    private Names names;

    /**
     * 原始类名
     */
    private String className;

    /**
     * Builder模式中的类名，例如原始类是User，那么建造者类名就是UserBuilder
     */
    private String builderClassName;


    /**
     * 插入式注解处理器的处理逻辑
     *
     * @param annotations 注解
     * @param roundEnv    环境
     * @return 处理结果
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
                public void visitClassDef(JCTree.JCClassDecl jcClass) {

                    before(jcClass);

                    // 为当前jcClass添加JMethod节点
                    jcClass.defs = jcClass.defs.append(
                            createStaticBuilderMethod()
                    );

                    // 为当前jcClass添加JCTree节点
                    jcClass.defs = jcClass.defs.append(
                            // 创建了一个静态内部类作为一个Builder
                            createJCClass(
                                    getSetJCMethods(jcClass)
                            )
                    );
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
        this.className = jcClass.getSimpleName().toString();
        this.builderClassName = this.className + "Builder";
    }


    /**
     * 创建静态方法，即builder方法，返回静态内部类的实例
     *
     * @return builder方法的语法树节点
     */
    private JCTree.JCMethodDecl createStaticBuilderMethod() {

        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加Builder模式中的返回语句 " return new XXXBuilder(); "
        jcStatements.append(
                treeMaker.Return(
                        treeMaker.NewClass(
                                null, // 尚不清楚含义
                                List.nil(), // 构造器方法参数
                                treeMaker.Ident(getNameFromString(builderClassName)), // 创建的类名
                                List.nil(), // 构造器方法列表
                                null // 尚不清楚含义
                        )
                )
        );

        // 转换成代码块
        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , jcStatements.toList() // 所有的语句
        );

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC), // 访问标志
                getNameFromString(BUILDER_STATIC_METHOD_NAME), // 名字
                treeMaker.Ident(getNameFromString(builderClassName)), //返回类型
                List.nil(), // 泛型形参列表
                List.nil(), // 参数列表，这里必须创建一个新的JCVariableDecl，否则注解处理时就会抛异常，原因目前还不清楚
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认值
        );
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
                setJCMethods = setJCMethods.prepend((JCTree.JCMethodDecl) jTree);
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
            return jcMethod.getName().startsWith(getNameFromString(SET))
                    && jcMethod.getParameters().size() == 1;
        }
        return false;
    }

    private Name getNameFromString(String name) {
        return names.fromString(name);
    }

    /**
     * 创建一个类的语法树节点。作为Builder模式中的Builder类
     *
     * @param jcMethods 待插入的方法的语法树节点集合
     * @return 创建出来的类的语法树节点
     */
    private JCTree.JCClassDecl createJCClass(List<JCTree.JCMethodDecl> jcMethods) {

        List<JCTree> jcTrees = List.nil();

        JCTree.JCVariableDecl jcVariable = createDataField();
        jcTrees = jcTrees.append(jcVariable);
        jcTrees = jcTrees.appendList(createSetJCMethods(jcMethods));
        jcTrees = jcTrees.append(createBuildJCMethod());

        return treeMaker.ClassDef(
                treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC + Flags.FINAL), // 访问标志
                getNameFromString(builderClassName), // 名字
                List.nil(), // 泛型形参列表
                null, // 继承
                List.nil(), // 接口列表
                jcTrees); // 定义
    }

    /**
     * 创建一个域的语法树节点。作为Builder模式中被Build的对象
     *
     * @return 域节点
     */
    private JCTree.JCVariableDecl createDataField() {
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE), // 访问标志
                getNameFromString(DATA), // 名字
                treeMaker.Ident(getNameFromString(className)), // 类型
                createInitializeJCExpression() // 初始化表达式
        );
    }

    /**
     * 创建一个语法树节点，其类型为JCExpression。即" new XXX(); "的语句
     *
     * @return 表达式的语法树节点
     */
    private JCTree.JCExpression createInitializeJCExpression() {
        return treeMaker.NewClass(
                null, // 尚不清楚含义
                List.nil(), // 构造器方法参数
                treeMaker.Ident(getNameFromString(className)), // 创建的类名
                List.nil(), // 构造器方法列表
                null // 尚不清楚含义
        );
    }

    /**
     * 创建一些语法树节点，其类型为JCMethod。作为Builder模式中的setXXX方法
     *
     * @param jcMethods 方法节点集合
     * @return 方法节点集合
     */
    private List<JCTree> createSetJCMethods(List<JCTree.JCMethodDecl> jcMethods) {
        List<JCTree> setJCMethods = List.nil();

        for (JCTree.JCMethodDecl jcMethod : jcMethods) {
            setJCMethods = setJCMethods.append(createSetJCMethod(jcMethod));
        }

        return setJCMethods;
    }

    /**
     * 创建一个语法树节点，其类型为JCMethodDecl。作为Builder模式中的setXXX方法
     *
     * @param jcMethod 方法节点
     * @return 方法节点
     */
    private JCTree.JCMethodDecl createSetJCMethod(JCTree.JCMethodDecl jcMethod) {
        JCTree.JCVariableDecl jcVariable = jcMethod.getParameters().get(0);

        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加调用语句" data.setXXX(xxx); "
        jcStatements.append(
                treeMaker.Exec(
                        treeMaker.Apply(
                                List.nil(),
                                treeMaker.Select(
                                        treeMaker.Ident(getNameFromString(DATA)),
                                        jcMethod.getName()
                                ),
                                List.of(treeMaker.Ident(jcVariable.getName()))
                        )
                )
        );

        // 添加Builder模式中的返回语句 " return this; "
        jcStatements.append(
                treeMaker.Return(
                        treeMaker.Ident(getNameFromString(THIS)
                        )
                )
        );

        // 转换成代码块
        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , jcStatements.toList() // 所有的语句
        );


        return treeMaker.MethodDef(
                jcMethod.getModifiers(), // 访问标志
                getModifiedName(jcMethod.getName()), // 名字
                treeMaker.Ident(getNameFromString(builderClassName)), //返回类型
                jcMethod.getTypeParameters(), // 泛型形参列表
                List.of(copyJCVariable(jcVariable)), // 参数列表，这里必须创建一个新的JCVariableDecl，否则注解处理时就会抛异常，原因目前还不清楚
                jcMethod.getThrows(), // 异常列表
                jcBlock, // 方法体
                null // 默认值
        );
    }

    /**
     * 修改名字，将setX，改成x
     *
     * @param originName 原始名字
     * @return 修改后的名字
     */
    private Name getModifiedName(Name originName) {
        String s = originName.toString();
        return getNameFromString(s.substring(3, 4).toLowerCase() + s.substring(4));
    }

    private JCTree.JCMethodDecl createBuildJCMethod() {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加返回语句 " return data; "
        jcStatements.append(
                treeMaker.Return(
                        treeMaker.Ident(
                                getNameFromString(DATA)
                        )
                )
        );

        // 转换成代码块
        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , jcStatements.toList() // 所有的语句
        );


        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC), // 访问标志
                getNameFromString(BUILD_METHOD_NAME), // 名字
                treeMaker.Ident(getNameFromString(className)), //返回类型
                List.nil(), // 泛型形参列表
                List.nil(), // 参数列表，这里必须创建一个新的JCVariableDecl，否则注解处理时就会抛异常，原因目前还不清楚
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认值
        );
    }

    /**
     * 克隆一个JCVariableDecl语法树节点
     * 我觉得TreeMaker.MethodDef()方法需要克隆参数列表的原因是：从JCMethodDecl拿到的JCVariableDecl会与这个JCMethodDecl有关联，因此需要创建一个与该JCMethodDecl无关的语法树节点（JCVariableDecl）
     *
     * @param prototypeJCVariable 域节点
     * @return 域节点
     */
    private JCTree.JCVariableDecl copyJCVariable(JCTree.JCVariableDecl prototypeJCVariable) {
        return treeMaker.VarDef(prototypeJCVariable.sym, prototypeJCVariable.getNameExpression());
    }

    /**
     * 获取一些注解处理器执行处理逻辑时需要用到的一些关键对象
     *
     * @param processingEnv 处理环境
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
