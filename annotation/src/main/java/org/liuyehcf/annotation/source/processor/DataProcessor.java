package org.liuyehcf.annotation.source.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import org.liuyehcf.annotation.source.annotation.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;

@SupportedAnnotationTypes("org.liuyehcf.annotation.source.annotation.Data")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DataProcessor extends BaseProcessor {

    private static final String VOID = "void";

    private static final String THIS = "this";

    private List<JCTree.JCVariableDecl> fieldJCVariables;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 首先获取被Builder注解标记的元素
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Data.class);

        set.forEach(element -> {

            // 获取当前元素的JCTree对象
            JCTree jcTree = trees.getTree(element);

            // JCTree利用的是访问者模式，将数据与数据的处理进行解耦，TreeTranslator就是访问者，这里我们重写访问类时的逻辑
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClass) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "@Data process [" + jcClass.name.toString() + "] begin!");

                    // 进行一些初始化操作
                    before(jcClass);

                    // 添加全参构造方法
                    jcClass.defs = jcClass.defs.appendList(
                            createDataMethods()
                    );

                    messager.printMessage(Diagnostic.Kind.NOTE, "@Data process [" + jcClass.name.toString() + "] end!");
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
        this.fieldJCVariables = getJCVariables(jcClass);
    }

    private List<JCTree.JCVariableDecl> getJCVariables(JCTree.JCClassDecl jcClass) {
        ListBuffer<JCTree.JCVariableDecl> jcVariables = new ListBuffer<>();

        // 遍历jcClass的所有内部节点，可能是字段，方法等等
        for (JCTree jTree : jcClass.defs) {
            // 找出所有set方法节点，并添加
            if (isValidField(jTree)) {
                // 注意这个com.sun.tools.javac.util.List的用法，不支持链式操作，更改后必须赋值
                jcVariables = jcVariables.append((JCTree.JCVariableDecl) jTree);
            }
        }

        return jcVariables.toList();
    }


    private boolean isValidField(JCTree jTree) {
        if (jTree.getKind().equals(JCTree.Kind.VARIABLE)) {
            JCTree.JCVariableDecl jcVariable = (JCTree.JCVariableDecl) jTree;

            Set<Modifier> flagSets = jcVariable.mods.getFlags();
            return (!flagSets.contains(STATIC)
                    && !flagSets.contains(FINAL));
        }

        return false;
    }

    private List<JCTree> createDataMethods() {
        ListBuffer<JCTree> dataMethods = new ListBuffer<>();

        for (JCTree.JCVariableDecl jcVariable : fieldJCVariables) {
            dataMethods.append(createSetJCMethod(jcVariable));
            dataMethods.append(createGetJCMethod(jcVariable));
        }

        return dataMethods.toList();
    }

    private JCTree.JCMethodDecl createSetJCMethod(JCTree.JCVariableDecl jcVariable) {

        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加语句 " this.xxx = xxx; "
        jcStatements.append(
                treeMaker.Exec(
                        treeMaker.Assign(
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString(THIS)),
                                        jcVariable.name
                                ),
                                treeMaker.Ident(jcVariable.name)
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
                names.fromString(createSetMethodName(jcVariable)), // 名字
                null, //返回类型
                List.nil(), // 泛型形参列表
                List.of(cloneJCVariable(jcVariable)), // 参数列表
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }

    private String createSetMethodName(JCTree.JCVariableDecl jcVariable) {
        String fieldName = jcVariable.name.toString();
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private JCTree.JCMethodDecl createGetJCMethod(JCTree.JCVariableDecl jcVariable) {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        // 添加语句 " return this.xxx; "
        jcStatements.append(
                treeMaker.Return(
                        treeMaker.Select(
                                treeMaker.Ident(names.fromString(THIS)),
                                jcVariable.name
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
                names.fromString(createGetMethodName(jcVariable)), // 名字
                jcVariable.vartype, //返回类型
                List.nil(), // 泛型形参列表
                List.nil(), // 参数列表
                List.nil(), // 异常列表
                jcBlock, // 方法体
                null // 默认方法（可能是interface中的那个default）
        );
    }

    private String createGetMethodName(JCTree.JCVariableDecl jcVariable) {
        String fieldName = jcVariable.name.toString();
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private JCTree.JCVariableDecl cloneJCVariable(JCTree.JCVariableDecl prototypeJCVariable) {
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PARAMETER), // 极其坑爹！！！
                prototypeJCVariable.name,
                prototypeJCVariable.vartype,
                null
        );
    }
}
