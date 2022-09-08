## Hive解析API初探

- 递归的思想遍历多叉树的节点，但是下面的代码有重复计算，使用`HashSet`去重了

```java



import java.io.IOException;
import java.util.*;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.BaseSemanticAnalyzer;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;

/**
 * @author:
 * @date: 2021/5/26 15:06
 * @description: 目的：获取AST中的表，列，以及对其所做的操作，如SELECT,INSERT
 * 重点：获取SELECT操作中的表和列的相关操作。其他操作这判断到表级别。
 * 实现思路：对AST深度优先遍历，遇到操作的token则判断当前的操作，
 * 遇到TOK_TAB或TOK_TABREF则判断出当前操作的表，遇到子句则压栈当前处理，处理子句。
 * 子句处理完，栈弹出。
 */
public class HiveParse {

    private static final String UNKNOWN = "UNKNOWN";
    private Map<String, String> alias = new HashMap<>();
    private Map<String, String> cols = new TreeMap<>();
    private Map<String, String> colAlais = new TreeMap<>();
    private Set<String> tables = new HashSet<>();
    private Stack<String> tableNameStack = new Stack<>();
    private Stack<Oper> operStack = new Stack<>();
    private String nowQueryTable = "";//定义及处理不清晰，修改为query或from节点对应的table集合或许好点。目前正在查询处理的表可能不止一个。
    private Oper oper;
    private boolean joinClause = false;

    private enum Oper {
        SELECT, INSERT, DROP, TRUNCATE, LOAD, CREATETABLE, ALTER
    }

    public Set<String> parseIterate(ASTNode ast) {
        Set<String> set = new HashSet<>();//当前查询所对应到的表集合
        prepareToParseCurrentNodeAndChilds(ast);
        set.addAll(parseChildNodes(ast));
        set.addAll(parseCurrentNode(ast, set));
        endParseCurrentNode(ast);
        return set;
    }

    /**
     * 结束遍历
     *
     * @param ast
     */
    private void endParseCurrentNode(ASTNode ast) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {//join 从句结束，跳出join
                case HiveParser.TOK_RIGHTOUTERJOIN:
                case HiveParser.TOK_LEFTOUTERJOIN:
                case HiveParser.TOK_JOIN:
                    joinClause = false;
                    break;
                case HiveParser.TOK_QUERY:
                    break;
                case HiveParser.TOK_INSERT:
                case HiveParser.TOK_SELECT:
                    nowQueryTable = tableNameStack.pop();
                    oper = operStack.pop();
                    break;
            }
        }
    }

    /**
     * 遍历当前节点
     *
     * @param ast
     * @param set
     * @return
     */
    private Set<String> parseCurrentNode(ASTNode ast, Set<String> set) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {
                case HiveParser.TOK_TABLE_PARTITION:
//            case HiveParser.TOK_TABNAME:
                    if (ast.getChildCount() != 2) {
                        String table = BaseSemanticAnalyzer
                                .getUnescapedName((ASTNode) ast.getChild(0));
                        if (oper == Oper.SELECT) {
                            nowQueryTable = table;
                        }
                        tables.add(table + "\t" + oper);
                    }
                    break;

                case HiveParser.TOK_TAB:// outputTable
                    String tableTab = BaseSemanticAnalyzer
                            .getUnescapedName((ASTNode) ast.getChild(0));
                    if (oper == Oper.SELECT) {
                        nowQueryTable = tableTab;
                    }
                    tables.add(tableTab + "\t" + oper);
                    break;
                case HiveParser.TOK_TABREF:// inputTable
                    ASTNode tabTree = (ASTNode) ast.getChild(0);
                    String tableName = (tabTree.getChildCount() == 1) ? BaseSemanticAnalyzer
                            .getUnescapedName((ASTNode) tabTree.getChild(0))
                            : BaseSemanticAnalyzer
                            .getUnescapedName((ASTNode) tabTree.getChild(0))
                            + "." + tabTree.getChild(1);
                    if (oper == Oper.SELECT) {
                        if (joinClause && !"".equals(nowQueryTable)) {
                            nowQueryTable += "&" + tableName;//
                        } else {
                            nowQueryTable = tableName;
                        }
                        set.add(tableName);
                    }
                    tables.add(tableName + "\t" + oper);
                    if (ast.getChild(1) != null) {
                        String alia = ast.getChild(1).getText().toLowerCase();
                        alias.put(alia, tableName);//sql6 p别名在tabref只对应为一个表的别名。
                    }
                    break;
                case HiveParser.TOK_TABLE_OR_COL:
                    if (ast.getParent().getType() != HiveParser.DOT) {
                        String col = ast.getChild(0).getText().toLowerCase();
                        if (alias.get(col) == null
                                && colAlais.get(nowQueryTable + "." + col) == null) {
                            if (nowQueryTable.indexOf("&") > 0) {//sql23
                                cols.put(UNKNOWN + "." + col, "");
                            } else {
                                cols.put(nowQueryTable + "." + col, "");
                            }
                        }
                    }
                    break;
                case HiveParser.TOK_ALLCOLREF:
                    cols.put(nowQueryTable + ".*", "");
                    break;
                case HiveParser.TOK_SUBQUERY:
                    if (ast.getChildCount() == 2) {
                        String tableAlias = unescapeIdentifier(ast.getChild(1)
                                .getText());
                        String aliaReal = "";
                        for (String table : set) {
                            aliaReal += table + "&";
                        }
                        if (aliaReal.length() != 0) {
                            aliaReal = aliaReal.substring(0, aliaReal.length() - 1);
                        }
//                    alias.put(tableAlias, nowQueryTable);//sql22
                        alias.put(tableAlias, aliaReal);//sql6
//                    alias.put(tableAlias, "");// just store alias
                    }
                    break;

                case HiveParser.TOK_SELEXPR:
                    if (ast.getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL) {
                        String column = ast.getChild(0).getChild(0).getText()
                                .toLowerCase();
                        if (nowQueryTable.indexOf("&") > 0) {
                            cols.put(UNKNOWN + "." + column, "");
                        } else if (colAlais.get(nowQueryTable + "." + column) == null) {
                            cols.put(nowQueryTable + "." + column, "");
                        }
                    } else if (ast.getChild(1) != null) {// TOK_SELEXPR (+
                        // (TOK_TABLE_OR_COL id)
                        // 1) dd
                        String columnAlia = ast.getChild(1).getText().toLowerCase();
                        colAlais.put(nowQueryTable + "." + columnAlia, "");
                    }
                    break;
                case HiveParser.DOT:
                    if (ast.getType() == HiveParser.DOT) {
                        if (ast.getChildCount() == 2) {
                            if (ast.getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL
                                    && ast.getChild(0).getChildCount() == 1
                                    && ast.getChild(1).getType() == HiveParser.Identifier) {
                                String alia = BaseSemanticAnalyzer
                                        .unescapeIdentifier(ast.getChild(0)
                                                .getChild(0).getText()
                                                .toLowerCase());
                                String column = BaseSemanticAnalyzer
                                        .unescapeIdentifier(ast.getChild(1)
                                                .getText().toLowerCase());
                                String realTable = null;
                                if (!tables.contains(alia + "\t" + oper)
                                        && alias.get(alia) == null) {// [b SELECT, a
                                    // SELECT]
                                    alias.put(alia, nowQueryTable);
                                }
                                if (tables.contains(alia + "\t" + oper)) {
                                    realTable = alia;
                                } else if (alias.get(alia) != null) {
                                    realTable = alias.get(alia);
                                }
                                if (realTable == null || realTable.length() == 0 || realTable.indexOf("&") > 0) {
                                    realTable = UNKNOWN;
                                }
                                cols.put(realTable + "." + column, "");

                            }
                        }
                    }
                    break;
                case HiveParser.TOK_ALTERTABLE_ADDPARTS:
                case HiveParser.TOK_ALTERTABLE_RENAME:
                case HiveParser.TOK_ALTERTABLE_ADDCOLS:
                    ASTNode alterTableName = (ASTNode) ast.getChild(0);
                    tables.add(alterTableName.getText() + "\t" + oper);
                    break;
            }
        }
        return set;
    }

    /**
     * 遍历孩子节点
     *
     * @param ast
     * @return
     */
    private Set<String> parseChildNodes(ASTNode ast) {
        Set<String> set = new HashSet<String>();
        int numCh = ast.getChildCount();
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
                set.addAll(parseIterate(child));
            }
        }
        return set;
    }

    /**
     * 准备遍历当前与孩子节点
     *
     * @param ast
     */
    private void prepareToParseCurrentNodeAndChilds(ASTNode ast) {
        if (ast.getToken() != null) {
            switch (ast.getToken().getType()) {//join 从句开始
                case HiveParser.TOK_RIGHTOUTERJOIN:
                case HiveParser.TOK_LEFTOUTERJOIN:
                case HiveParser.TOK_JOIN:
                    joinClause = true;
                    break;
                case HiveParser.TOK_QUERY:
                    tableNameStack.push(nowQueryTable);
                    operStack.push(oper);
                    nowQueryTable = "";//sql22
                    oper = Oper.SELECT;
                    break;
                case HiveParser.TOK_INSERT:
                    tableNameStack.push(nowQueryTable);
                    operStack.push(oper);
                    oper = Oper.INSERT;
                    break;
                case HiveParser.TOK_SELECT:
                    tableNameStack.push(nowQueryTable);
                    operStack.push(oper);
//                    nowQueryTable = nowQueryTable
                    // nowQueryTable = "";//语法树join
                    // 注释语法树sql9， 语法树join对应的设置为""的注释逻辑不符
                    oper = Oper.SELECT;
                    break;
                case HiveParser.TOK_DROPTABLE:
                    oper = Oper.DROP;
                    break;
                case HiveParser.TOK_TRUNCATETABLE:
                    oper = Oper.TRUNCATE;
                    break;
                case HiveParser.TOK_LOAD:
                    oper = Oper.LOAD;
                    break;
                case HiveParser.TOK_CREATETABLE:
                    oper = Oper.CREATETABLE;
                    break;
            }
            if (ast.getToken() != null
                    && ast.getToken().getType() >= HiveParser.TOK_ALTERDATABASE_PROPERTIES
                    && ast.getToken().getType() <= HiveParser.TOK_ALTERVIEW_RENAME) {
                oper = Oper.ALTER;
            }
        }
    }

    public static String unescapeIdentifier(String val) {
        if (val == null) {
            return null;
        }
        if (val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`') {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    private void output(Map<String, String> map) {
        java.util.Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println(key + "\t" + map.get(key));
        }
    }

    public void parse(ASTNode ast) {
        parseIterate(ast);
        System.out.println("***************表***************");
        for (String table : tables) {
            System.out.println(table);
        }
        System.out.println("***************列***************");
        output(cols);
        System.out.println("***************别名***************");
        output(alias);
    }

    public static void main(String[] args) throws IOException, ParseException,
            SemanticException {

        // HiveConf conf = new HiveConf();
        String sql1 = "Select * from zpc1";
       

        String parsesql = sql25;
        HiveParse hp = new HiveParse();
        System.out.println(parsesql);
        ParseDriver pd = new ParseDriver();
        ASTNode ast = pd.parse(parsesql);
        System.out.println(ast.toStringTree());
        hp.parse(ast);
    }
}
```



### Reference

- [Hive SQL 解析及应用](http://itpcb.com/a/246483)

- [hive中sql解析出对应表和字段的实现](https://www.cnblogs.com/drawwindows/p/4595771.html)

