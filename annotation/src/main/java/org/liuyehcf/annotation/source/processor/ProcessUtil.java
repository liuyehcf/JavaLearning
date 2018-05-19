package org.liuyehcf.annotation.source.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.lang.model.element.Modifier;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;

public class ProcessUtil {
    static final String THIS = "this";

    private static final String SET = "set";

    private static final String GET = "get";

    /**
     * 创建建造者的静态方法名
     */
    static final String BUILDER_STATIC_METHOD_NAME = "builder";

    /**
     * 建造方法名
     */
    static final String BUILD_METHOD_NAME = "build";

    static final String CONSTRUCTOR_NAME = "<init>";

    /**
     * 克隆一个域的语法树节点，该节点作为方法的参数
     * 具有位置信息的语法树节点是不能复用的！
     *
     * @param prototypeJCVariable 域节点
     * @return 克隆后的域节点
     */
    static JCTree.JCVariableDecl cloneJCVariableAsParam(TreeMaker treeMaker, JCTree.JCVariableDecl prototypeJCVariable) {
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PARAMETER), // 极其坑爹！！！
                prototypeJCVariable.name,
                prototypeJCVariable.vartype,
                null
        );
    }


    /**
     * 判断是否是合法的属性
     *
     * @param jTree 语法树节点
     * @return 是否是合法属性
     */
    private static boolean isValidField(JCTree jTree) {
        if (jTree.getKind().equals(JCTree.Kind.VARIABLE)) {
            JCTree.JCVariableDecl jcVariable = (JCTree.JCVariableDecl) jTree;

            Set<Modifier> flagSets = jcVariable.mods.getFlags();
            return (!flagSets.contains(STATIC)
                    && !flagSets.contains(FINAL));
        }

        return false;
    }


    /**
     * 获取域的语法树节点的集合
     *
     * @param jcClass 类的语法树节点
     * @return 域的语法树节点的集合
     */
    static List<JCTree.JCVariableDecl> getJCVariables(JCTree.JCClassDecl jcClass) {
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


    /**
     * 判断是否为set方法
     *
     * @param jTree 语法树节点
     * @return 判断是否是Set方法
     */
    private static boolean isSetJCMethod(JCTree jTree) {
        if (jTree.getKind().equals(JCTree.Kind.METHOD)) {
            JCTree.JCMethodDecl jcMethod = (JCTree.JCMethodDecl) jTree;
            return jcMethod.name.toString().startsWith(SET)
                    && jcMethod.params.size() == 1
                    && !jcMethod.mods.getFlags().contains(Modifier.STATIC);
        }
        return false;
    }


    /**
     * 提取出所有set方法的语法树节点
     *
     * @param jcClass 类的语法树节点
     * @return set方法的语法树节点的集合
     */
    static List<JCTree.JCMethodDecl> getSetJCMethods(JCTree.JCClassDecl jcClass) {
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
     * set方法名转换为属性名
     *
     * @param setMethodName set方法名
     * @return 属性名
     */
    static String fromSetMethodNameToPropertyName(String setMethodName) {
        return setMethodName.substring(3, 4).toLowerCase() + setMethodName.substring(4);
    }

    /**
     * 属性名转换为set方法名
     *
     * @param propertyName 属性名
     * @return set方法名
     */
    static String fromPropertyNameToSetMethodName(String propertyName) {
        return SET + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    /**
     * 属性名转换为get方法名
     *
     * @param propertyName 属性名
     * @return get方法名
     */
    static String fromPropertyNameToGetMethodName(String propertyName) {
        return GET + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
}
