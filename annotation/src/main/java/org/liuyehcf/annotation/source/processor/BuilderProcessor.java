package org.liuyehcf.annotation.source.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import org.liuyehcf.annotation.source.annotation.Builder;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("org.liuyehcf.annotation.source.annotation.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends BaseProcessor {

    private static final String THIS = "this";

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
     * 原始类名
     */
    private Name className;

    /**
     * Builder模式中的类名，例如原始类是User，那么建造者类名就是UserBuilder
     */
    private Name builderClassName;

    /**
     * 原始类的set方法的语法树节点集合
     */
    private List<JCTree.JCMethodDecl> setJCMethods;

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
                    messager.printMessage(Diagnostic.Kind.NOTE, "@Builder process [" + jcClass.getSimpleName().toString() + "] begin!");

                    // 进行一些初始化操作
                    before(jcClass);

                    // 添加全参构造方法
                    jcClass.defs = jcClass.defs.append(
                            createAllArgsConstructor()
                    );

                    // 添加builder方法
                    jcClass.defs = jcClass.defs.append(
                            createStaticBuilderMethod()
                    );

                    // 添加静态内部类
                    jcClass.defs = jcClass.defs.append(
                            createJCClass()
                    );

                    messager.printMessage(Diagnostic.Kind.NOTE, "@Builder process [" + jcClass.getSimpleName().toString() + "] end!");
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
        this.builderClassName = names.fromString(this.className + "Builder");
        this.setJCMethods = getSetJCMethods(jcClass);
    }

    /**
     * 创建全参数构造方法
     *
     * @return 全参构造方法语法树节点
     */
    private JCTree.JCMethodDecl createAllArgsConstructor() {
        List<JCTree.JCVariableDecl> jcVariables = List.nil();

        for (JCTree.JCMethodDecl jcMethod : setJCMethods) {
            jcVariables = jcVariables.append(jcMethod.getParameters().get(0));
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
                className, // 名字
                null, //返回类型
                List.nil(), // 泛型形参列表
                jcVariables, // 参数列表，这里必须创建一个新的JCVariable，否则注解处理时就会抛异常，原因目前还不清楚
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
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
                                List.nil(), // 泛型参数列表
                                treeMaker.Ident(builderClassName), // 创建的类名
                                List.nil(), // 参数列表
                                null // 类定义，估计是用于创建匿名内部类
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
                names.fromString(BUILDER_STATIC_METHOD_NAME), // 名字
                treeMaker.Ident(builderClassName), //返回类型
                List.nil(), // 泛型形参列表
                List.nil(), // 参数列表，这里必须创建一个新的JCVariable，否则注解处理时就会抛异常，原因目前还不清楚
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
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
            return jcMethod.getName().startsWith(names.fromString(SET))
                    && jcMethod.getParameters().size() == 1;
        }
        return false;
    }

    /**
     * 创建一个类的语法树节点。作为Builder模式中的Builder类
     *
     * @return 创建出来的类的语法树节点
     */
    private JCTree.JCClassDecl createJCClass() {

        List<JCTree> jcTrees = List.nil();

        List<JCTree> var = createVariables(setJCMethods);
        jcTrees = jcTrees.appendList(var);
        jcTrees = jcTrees.appendList(createSetJCMethods(setJCMethods));
        jcTrees = jcTrees.append(createBuildJCMethod());

        return treeMaker.ClassDef(
                treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC + Flags.FINAL), // 访问标志
                builderClassName, // 名字
                List.nil(), // 泛型形参列表
                null, // 继承
                List.nil(), // 接口列表
                jcTrees); // 定义
    }

    /**
     * 根据方法集合创建对应的域的语法树节点集合
     *
     * @param jcMethods 待插入的set方法的语法树节点集合
     * @return 域的语法树节点集合
     */
    private List<JCTree> createVariables(List<JCTree.JCMethodDecl> jcMethods) {
        List<JCTree> jcVariables = List.nil();

        for (JCTree.JCMethodDecl jcMethod : jcMethods) {
            jcVariables = jcVariables.append(
                    treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PRIVATE), // 访问标志
                            getNameFromSetJCMethod(jcMethod), // 名字
                            jcMethod.getParameters().head.vartype // 类型
                            , null // 初始化语句
                    )
            );
        }

        return jcVariables;
    }

    private Name getNameFromSetJCMethod(JCTree.JCMethodDecl jcMethod) {
        String s = jcMethod.getName().toString();
        return names.fromString(s.substring(3, 4).toLowerCase() + s.substring(4));
    }

    /**
     * 创建方法的语法树节点的集合。作为Builder模式中的setXXX方法
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
     * 创建一个方法的语法树节点。作为Builder模式中的setXXX方法
     *
     * @param jcMethod 方法节点
     * @return 方法节点
     */
    private JCTree.JCMethodDecl createSetJCMethod(JCTree.JCMethodDecl jcMethod) {
        JCTree.JCVariableDecl jcVariable = jcMethod.getParameters().get(0);

        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加语句 " this.xxx = xxx; "
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

        // 添加Builder模式中的返回语句 " return this; "
        jcStatements.append(
                treeMaker.Return(
                        treeMaker.Ident(names.fromString(THIS)
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
                getNameFromSetJCMethod(jcMethod), // 名字
                treeMaker.Ident(builderClassName), //返回类型
                jcMethod.getTypeParameters(), // 泛型形参列表
                List.of(cloneJCVariable(jcVariable)), // 参数列表
                jcMethod.getThrows(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }

    private JCTree.JCMethodDecl createBuildJCMethod() {
        ListBuffer<JCTree.JCExpression> jcVariableExpressions = new ListBuffer<>();

        for (JCTree.JCMethodDecl jcMethod : setJCMethods) {
            jcVariableExpressions.append(
                    treeMaker.Select(
                            treeMaker.Ident(names.fromString(THIS)),
                            getNameFromSetJCMethod(jcMethod)
                    )
            );
        }

        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加返回语句 " return new XXX(arg1, arg2, ...); "
        jcStatements.append(
                treeMaker.Return(
                        treeMaker.NewClass(
                                null, // 尚不清楚含义
                                List.nil(), // 泛型参数列表
                                treeMaker.Ident(className), // 创建的类名
                                jcVariableExpressions.toList(), // 参数列表
                                null // 类定义，估计是用于创建匿名内部类
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
                names.fromString(BUILD_METHOD_NAME), // 名字
                treeMaker.Ident(className), //返回类型
                List.nil(), // 泛型形参列表
                List.nil(), // 参数列表
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }

    /**
     * 克隆一个域的语法树节点
     * 具有位置信息的语法树节点是不能复用的！
     *
     * @param prototypeJCVariable 原始域节点
     * @return 克隆后的域节点
     */
    private JCTree.JCVariableDecl cloneJCVariable(JCTree.JCVariableDecl prototypeJCVariable) {
        return treeMaker.VarDef(prototypeJCVariable.sym, prototypeJCVariable.getNameExpression());
    }


}
