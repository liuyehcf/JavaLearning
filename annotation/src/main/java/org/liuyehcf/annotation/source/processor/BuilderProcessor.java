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

import static org.liuyehcf.annotation.source.processor.ProcessUtil.*;

@SupportedAnnotationTypes("org.liuyehcf.annotation.source.annotation.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends BaseProcessor {

    /**
     * 类名
     */
    private Name className;

    /**
     * Builder模式中的类名，例如原始类是User，那么建造者类名就是UserBuilder
     */
    private Name builderClassName;

    /**
     * set方法的语法树节点集合
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
                    messager.printMessage(Diagnostic.Kind.NOTE, "@Builder process [" + jcClass.name.toString() + "] begin!");

                    before(jcClass);

                    // 添加builder方法
                    jcClass.defs = jcClass.defs.append(
                            createStaticBuilderMethod()
                    );

                    // 添加静态内部类
                    jcClass.defs = jcClass.defs.append(
                            createJCClass()
                    );

                    after();

                    messager.printMessage(Diagnostic.Kind.NOTE, "@Builder process [" + jcClass.name.toString() + "] end!");
                }
            });
        });

        return true;
    }

    /**
     * 进行一些初始化工作
     *
     * @param jcClass 类的语法树节点
     */
    private void before(JCTree.JCClassDecl jcClass) {
        this.className = names.fromString(jcClass.name.toString());
        this.builderClassName = names.fromString(this.className + "Builder");
        this.setJCMethods = getSetJCMethods(jcClass);
    }

    /**
     * 进行一些清理工作
     */
    private void after() {
        this.className = null;
        this.builderClassName = null;
        this.setJCMethods = null;
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

        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , jcStatements.toList() // 所有的语句
        );

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC), // 访问标志
                names.fromString(BUILDER_STATIC_METHOD_NAME), // 名字
                treeMaker.Ident(builderClassName), //返回类型
                List.nil(), // 泛型形参列表
                List.nil(), // 参数列表
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }

    /**
     * 创建一个类的语法树节点。作为Builder模式中的Builder类
     *
     * @return 创建出来的类的语法树节点
     */
    private JCTree.JCClassDecl createJCClass() {

        ListBuffer<JCTree> jcTrees = new ListBuffer<>();

        jcTrees.appendList(createVariables(setJCMethods));
        jcTrees.appendList(createSetJCMethods(setJCMethods));
        jcTrees.append(createBuildJCMethod());

        return treeMaker.ClassDef(
                treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC + Flags.FINAL), // 访问标志
                builderClassName, // 名字
                List.nil(), // 泛型形参列表
                null, // 继承
                List.nil(), // 接口列表
                jcTrees.toList()); // 定义
    }

    /**
     * 根据方法集合创建对应的字段的语法树节点集合
     *
     * @param jcMethods 待插入的set方法的语法树节点集合
     * @return 字段的语法树节点集合
     */
    private List<JCTree> createVariables(List<JCTree.JCMethodDecl> jcMethods) {
        ListBuffer<JCTree> jcVariables = new ListBuffer<>();

        for (JCTree.JCMethodDecl jcMethod : jcMethods) {
            jcVariables.append(
                    treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PRIVATE), // 访问标志
                            names.fromString(fromSetMethodNameToPropertyName(jcMethod.name.toString())), // 名字
                            jcMethod.params.head.vartype // 类型
                            , null // 初始化语句
                    )
            );
        }

        return jcVariables.toList();
    }

    /**
     * 创建方法的语法树节点的集合。作为Builder模式中的setXXX方法
     *
     * @param jcMethods 方法节点集合
     * @return 方法节点集合
     */
    private List<JCTree> createSetJCMethods(List<JCTree.JCMethodDecl> jcMethods) {
        ListBuffer<JCTree> setJCMethods = new ListBuffer<>();

        for (JCTree.JCMethodDecl jcMethod : jcMethods) {
            setJCMethods.append(createSetJCMethod(jcMethod));
        }

        return setJCMethods.toList();
    }

    /**
     * 创建一个方法的语法树节点。作为Builder模式中的setXXX方法
     *
     * @param jcMethod 方法节点
     * @return 方法节点
     */
    private JCTree.JCMethodDecl createSetJCMethod(JCTree.JCMethodDecl jcMethod) {
        JCTree.JCVariableDecl jcVariable = jcMethod.params.get(0);

        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加语句 " this.xxx = xxx; "
        jcStatements.append(
                treeMaker.Exec(
                        treeMaker.Assign(
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString(THIS)),
                                        names.fromString(fromSetMethodNameToPropertyName(jcMethod.name.toString()))
                                ),
                                treeMaker.Ident(names.fromString(fromSetMethodNameToPropertyName(jcMethod.name.toString())))
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

        JCTree.JCBlock jcBlock = treeMaker.Block(
                0 // 访问标志
                , jcStatements.toList() // 所有的语句
        );

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC), // 访问标志
                names.fromString(fromSetMethodNameToPropertyName(jcMethod.name.toString())), // 名字
                treeMaker.Ident(builderClassName), //返回类型
                List.nil(), // 泛型形参列表
                List.of(cloneJCVariableAsParam(treeMaker, jcVariable)), // 参数列表
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }

    /**
     * 创建build方法的语法树节点
     *
     * @return build方法的语法树节点
     */
    private JCTree.JCMethodDecl createBuildJCMethod() {
        ListBuffer<JCTree.JCExpression> jcVariableExpressions = new ListBuffer<>();

        for (JCTree.JCMethodDecl jcMethod : setJCMethods) {
            jcVariableExpressions.append(
                    treeMaker.Select(
                            treeMaker.Ident(names.fromString(THIS)),
                            names.fromString(fromSetMethodNameToPropertyName(jcMethod.name.toString()))
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
}
